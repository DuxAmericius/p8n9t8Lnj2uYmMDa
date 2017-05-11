/* FblaLogon.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This class establishes the connection with the Azure Mobile App server
   and the entire logon process. Part of the logon includes creating and/or fetching
   the user's Account information. It's done with the logon to make sure communication
   with the Azure server is working. This class extends AsyncTask because almost all
   of the login processes must be done in the background.

   Users must create or use a Microsoft account. The only piece of information about the
   account that is stored in the database is Azure's user token string, which
   looks like this: sid:fb96bd335c1fba115e191a4526df5353
   Also, when using the Microsoft provider, we have to continually clear cookies
   because the token caching interferes with our ability to logoff.

   The first time a user logs in, a new row is inserted into the Account table.
   The user's Account object and corresponding Schools object are stored as static
   variables in this class so that they are available to all activities and fragments
   in this mobile app. In addition, this class also stores the client object for
   Azure as a static variable, also to make is easily available to all activities
   and fragments in this mobile app.
*/

package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.Schools;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class FblaLogon extends AsyncTask {
    final public static String AZUREURL = "https://fbla-yardsale.azurewebsites.net";
    // Setup to use either Google+ or Microsoft.
    // However, Google changed their policy, so it doesn't work anymore.
    // Therefore, we will use Microsoft Accounts to authenticate.
    final private static MobileServiceAuthenticationProvider PROVIDER = MobileServiceAuthenticationProvider.MicrosoftAccount;

    private static boolean mLoggedOn = false;
    private static String mUserId = null;
    private static String mToken = null;
    private static MobileServiceClient mClient = null;
    private static Account mAccount = null;
    private static MobileServiceTable<Account> mAccountTable = null;
    private static MobileServiceTable<Schools> mSchoolsTable = null;

    private Context mContext;
    private ArrayList<LogonResultListener> mListeners = new ArrayList<LogonResultListener>();

    // Initialize the MobileServiceClient
    public FblaLogon(Context context) {
        mContext = context;
        if (getLoggedOn()) return;
        // Clear cookies now to support being able to logout easily later.
        //clearCookies();
        try {
            mClient = new MobileServiceClient(AZUREURL, mContext);
        } catch (Exception e) {
            Log.d("FblaLogon:init", e.toString());
            mClient = null;
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Object result = doLogon();
        return result;
    }

    @Override
    protected void onPostExecute(Object result) {
        clearCookies();
        if (result == null) onLogonSuccess();
        else onLogonFailure((Exception)result);
    }

    public static boolean getLoggedOn() {
        return mLoggedOn;
    }
    public void Logoff() {
        mLoggedOn = false;
        //clearCookies();
        setCache(mContext, null, null);
        mAccountTable = null;
        mAccount = null;
        Log.d("FblaLogon:Logoff", "Logged Off");
    }

    public static MobileServiceClient getClient() {
        return mClient;
    }

    public static String getUserId() {
        return mUserId;
    }

    public static Account getAccount() {
        return mAccount;
    }

    public static void setAccount(Account account) { mAccount = account; }

    public static int getSearchMiles(Context context) {
        if (context == null) return 5;
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (prefs == null) return 5;
        return prefs.getInt("miles", 5);
    }

    public static void setSearchMiles(Context context, int miles) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("miles", miles);
        editor.commit();
    }

    // Once all of the logon processes are complete, notify any listeners
    private void onLogonSuccess() {
        mLoggedOn = true;
        Log.d("FblaLogon", "onLogonSuccess");
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(null);
        }
    }

    // Notify any listeners that the logon has failed.
    private void onLogonFailure(Exception e) {
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(e);
        }
        Log.d("FblaLogon:Failure", e.toString());
    }

    // This starts the whole logon process.
    private Object doLogon() {
        if (mClient == null) return new Exception("Client Not Initialized");
        getCache(mContext);
        if (mUserId == null || mToken == null || isTokenExpired(mToken)) {
            // Missing or expired token, so need to kick off the Oauth logon process.
            return providerLogon();
        } else {
            // The cached token seems to be good, so load the account with it.
            MobileServiceUser user = new MobileServiceUser(mUserId);
            user.setAuthenticationToken(mToken);
            mClient.setCurrentUser(user);
            return loadAccount();
        }
    }

    // It seems to use WebKit to perform the OAuth authentication via Azure.
    // If successful, load the Account.  Otherwise return an exception to notify
    // the listeners that it didn't work.
    private Object providerLogon() {
        try {
            MobileServiceUser mobileServiceUser = mClient.login(PROVIDER).get();
            Log.d("FblaLogon:login", "Logged On");
            setCache(mContext, mobileServiceUser.getUserId(), mobileServiceUser.getAuthenticationToken());
            return loadAccount();
        } catch (Exception ex) {
            Log.d("FblaLogon:login", ex.toString());
            return ex;
        }
    }

    // A successfully loaded account means the token actually works and we can talk to Azure.
    // Some accounts may not be linked to a School.
    private Object loadAccount() {
        // Now load the account
        mAccountTable = mClient.getTable(Account.class);
        try {
            Account account = mAccountTable.lookUp(mUserId).get();
            // Found the account record, so set it on the Data object.
            Log.d("FblaLogon:account", "onSuccess - " + account.getId());
            setAccount(account);
            return loadSchool();
        } catch (ExecutionException e) {
            if (e.getCause().getClass() == MobileServiceException.class) {
                MobileServiceException mEx = (MobileServiceException) e.getCause();
                if (mEx.getResponse() != null && mEx.getResponse().getStatus().code == 404) { // Not Found
                    // The user is not in the table, so insert a new record for them.
                    Account act = new Account();
                    act.setId(mUserId);
                    mAccountTable.insert(act);
                    setAccount(act);
                    Log.d("FblaLogon:account", "AccountEdit Created");
                    return null;
                } else {
                    Log.d("FblaLogon:account", mEx.toString());
                    return mEx;
                }
            } else {
                Log.d("FblaLogon:account", e.toString());
                return e;
            }
        } catch (Exception ex) {
            // Something else bad happened.
            Log.d("FblaLogon:account", ex.toString());
            return ex;
        }
    }

    // Load the school, if the Account is linked to one.
    // Return a null if everything is successful. Otherwise return an Exception.
    private Object loadSchool() {
        if (mAccount.getSchoolId() != null && mAccount.getSchool() == null) {
            Log.d("FblaLogin:school", "School " + mAccount.getSchoolId());
            mSchoolsTable = mClient.getTable(Schools.class);
            try {
                Schools school = mSchoolsTable.lookUp(mAccount.getSchoolId()).get();
                // Found the account record, so set it on the Data object.
                Log.d("FblaLogon:school", "onSuccess - "+school.getId());
                mAccount.setSchool(school);
                return null;
            } catch (ExecutionException e) {
                if (e.getCause().getClass() == MobileServiceException.class) {
                    MobileServiceException mEx = (MobileServiceException) e.getCause();
                    if (mEx.getResponse() != null && mEx.getResponse().getStatus().code == 404) { // Not Found
                        Log.d("FblaLogon:school", "School Missing");
                        return null;
                    } else {
                        Log.d("FblaLogon:school", mEx.toString());
                        return mEx;
                    }
                } else {
                    Log.d("FblaLogon:school", e.toString());
                    return e;
                }
            } catch (Exception ex) {
                // Something else bad happened.
                Log.d("FblaLogon:school", ex.toString());
                return ex;
            }
        } else {
            Log.d("FblaLogin:school", "No School");
            return null;
        }
    }

    private void clearCookies() {
        // Clear cookies and cache before logging on
        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                Log.d("FblaLogon:cookies", "Cookies cleared");
            }
        });
    }

    // Helper functions to set and clear Shared Preferences cache.
    private void getCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        mUserId = prefs.getString("usr", null);
        mToken = prefs.getString("tkn", null);
    }
    private void setCache(Context context, String userId, String token) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (userId == null) editor.remove("usr");
        else editor.putString("usr", userId);
        if (token == null) editor.remove("tkn");
        else editor.putString("tkn", token);
        editor.commit();
        mUserId = userId;
        mToken = token;
    }

    // Need to make sure the token has not expired.
    // https://blogs.msdn.microsoft.com/writingdata_services/2015/04/27/check-for-expired-azure-mobile-services-authentication-tokens/
    private boolean isTokenExpired(String token) {
        // The expiration is embedded in the token.  This will rip apart the token to get the expiration.
        try {
            // Get the string after the period
            String jwt = token.split("\\.")[1];
            // Decode the URL encoding
            jwt = URLDecoder.decode(jwt, "UTF-8");
            // Decode from Base64
            byte[] bytes = Base64.decode(jwt, Base64.DEFAULT);
            // Convert the byte array into a string, which is JSON
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            // Create the JSON object
            JSONObject json = new JSONObject(jsonString);
            // Extract the expiration from the JSON object
            jwt = json.getString("exp");
            // The value is the number of seconds since 1/1/1970 UTC (epoch)
            long exp = Long.parseLong(jwt) * 1000;
            boolean isExpired = System.currentTimeMillis() >= exp;
            Log.d("FblaLogon:Token", isExpired ? "Expired " : "Valid " + (new Date(exp)).toString());
            return isExpired;
        } catch (Exception e) {
            Log.d("FblaLogon:Token", e.toString());
        }
        return false;
    }

    // Add a listener to call after logon is complete
    public void setLogonListener(LogonResultListener listener) {
        mListeners.add(listener);
    }

    // This is the interface to use on the logon callbacks.
    public interface LogonResultListener {
        void onLogonComplete(Exception e);
    }
}
