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
*/

package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.fbla.dulaney.fblayardsale.model.*;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class FblaLogon extends AsyncTask {
    final public static String AZUREURL = "https://fbla-yardsale.azurewebsites.net";

    private static boolean mLoggedOn = false;
    private static String mUserId = null;
    private static String mToken = null;
    private static MobileServiceClient mClient = null;
    private static Account mAccount = null;
    private static MobileServiceTable<Account> mAccountTable = null;

    private Context mContext;
    private ArrayList<LogonResultListener> mListeners = new ArrayList<LogonResultListener>();

    // Initialize the MobileServiceClient
    public FblaLogon(Context context) {
        mContext = context;
        if (getLoggedOn()) return;
        // Clear cookies now to support being able to logout easily later.
        clearCookies();
        try {
            mClient = new MobileServiceClient(AZUREURL, mContext);
        } catch (Exception e) {
            Log.d("FblaLogon:init", e.toString());
            mClient = null;
        }

        // DEBUG
        //setCache(mContext, null, null);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        doLogon();
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {

    }

    public static boolean getLoggedOn() {
        return mLoggedOn;
    }
    public void Logoff() {
        mLoggedOn = false;
        clearCookies();
        setCache(mContext, null, null);
        mAccountTable = null;
        mAccount = null;
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

    public static void setAccount(Account account) {
        mAccount = account;
    }

    // Once all of the logon processes are complete, notify any listeners
    private void onLogonSuccess() {
        mLoggedOn = true;
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(null);
        }
        Log.d("FblaLogon", "onLogonSuccess");
    }

    // Notify any listeners that the logon has failed.
    private void onLogonFailure(Exception e) {
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(e);
        }
        Log.d("FblaLogon", "onLogonFailure");
    }

    // This starts the whole logon chain of asynchronous calls.
    // So many things can happen in callbacks that I have to chain them all together.
    private void doLogon() {
        if (mClient == null) return;
        getCache(mContext);
        if (mUserId == null || mToken == null || isTokenExpired(mToken)) {
            // Missing or expired token, so need to kick off the Google+ logon process.
            googleLogon();
        } else {
            // The cached token seems to be good, so load the account with it.
            loadAccount();
        }
    }

    // This is the last part of the chain of asynchronous calls.
    // A successfully loaded account means the token actually works and we can talk to Azure.
    // So the callback from this one will call either onLogonSuccess or onLogonFailure.
    private void loadAccount() {
        MobileServiceUser user = new MobileServiceUser(mUserId);
        user.setAuthenticationToken(mToken);
        mClient.setCurrentUser(user);

        mAccountTable = mClient.getTable(Account.class);
        ListenableFuture<Account> account = mAccountTable.lookUp(mUserId);
        Futures.addCallback(account, new FutureCallback<Account>() {
            @Override
            public void onFailure(Throwable exc) {
                // See what kind of exception it is
                if (exc.getMessage().equals("{\"error\":\"The item does not exist\"}")) {
                    // The user is not in the table, so insert a new record for them.
                    Account act = new Account();
                    act.setId(mUserId);
                    mAccountTable.insert(act);
                    setAccount(act);
                    Log.d("FblaLogon:account", "AccountEdit Created");
                    onLogonSuccess();
                } else {
                    // Something else bad happened.
                    Log.d("FblaLogon:account", exc.toString());
                    onLogonFailure((Exception)exc);
                }
            }

            @Override
            public void onSuccess(Account result) {
                // Found the account record, so set it on the Data object.
                Log.d("FblaLogon:account", "onSuccess - "+result.getId());
                setAccount(result);
                clearCookies();
                onLogonSuccess();
            }
        });
    }

    // This is the longest chain of asynchronous processes.
    // It seems to use WebKit to perform the Google+ OAuth authentication via Azure.
    // If successful, chain it to loading Accounts.  Otherwise call onLogonFailure to notify
    // the listeners that it didn't work.
    private void googleLogon() {
        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Google);
        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onSuccess(MobileServiceUser mobileServiceUser) {
                Log.d("FblaLogon:login", "Logged On");
                setCache(mContext, mobileServiceUser.getUserId(), mobileServiceUser.getAuthenticationToken());
                loadAccount();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("FblaLogon:login", throwable.toString());
                onLogonFailure((Exception)throwable);
            }
        });
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
