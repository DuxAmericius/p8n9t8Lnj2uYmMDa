/* FblaAzure.java
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
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.Schools;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FblaAzure {
    final private static String AZUREURL = "https://fbla-yardsale.azurewebsites.net";
    // Setup to use either Google+ or Microsoft.
    // However, Azure was updated and suddenly the Google logon doesn't work anymore.
    // Therefore, we will use Microsoft Accounts to authenticate.
    final private static MobileServiceAuthenticationProvider PROVIDER = MobileServiceAuthenticationProvider.MicrosoftAccount;

    private MobileServiceUser mUser = null;
    private MobileServiceClient mClient = null;
    private Account mAccount = null;
    private MobileServiceTable<Account> mAccountTable = null;
    private MobileServiceTable<Schools> mSchoolsTable = null;

    private Context mContext;
    private ArrayList<LogonResultListener> mListeners = new ArrayList<LogonResultListener>();

    public FblaAzure (Context context) {
        mContext = context;
        try {
            mClient = new MobileServiceClient(AZUREURL, mContext);
        } catch (Exception e) {
            Log.d("FblaAzure:init", e.toString());
            mClient = null;
        }
    }

    // It seems to use WebKit to perform the OAuth authentication via Azure.
    // If successful, load the Account.  Otherwise return an exception to notify
    // the listeners that it didn't work.
    public void doLogon() {
        Log.d("FblaAzure", "Logging on...");
        ListenableFuture<MobileServiceUser> futureUser = mClient.login(PROVIDER);
        Futures.addCallback(futureUser, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                onLogonFailure((Exception)exc);
            }
            @Override
            public void onSuccess(MobileServiceUser user) {
                mUser = user;
                doLoadAccount();
            }
        });
    }

    // This will load the account in the background.
    public void doLoadAccount() {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Log.d("FblaAzure", "Loading Account...");
                return loadAccount();
            }
            @Override
            protected void onPostExecute(Object result) {
                if (result == null) onLogonSuccess();
                else onLogonFailure((Exception)result);
            }
        }.execute();
    }
    public void doLogon(String userId, String token) {
        mUser = new MobileServiceUser(userId);
        mUser.setAuthenticationToken(token);
        mClient.setCurrentUser(mUser);

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Log.d("FblaAzure", "Loading Account...");
                return loadAccount();
            }
            @Override
            protected void onPostExecute(Object result) {
                if (result == null) onLogonSuccess();
                else onLogonFailure((Exception)result);
            }
        }.execute();
    }

    public void doLogoff(YardSaleMain main) {
        clearCookies();
        mAccountTable = null;
        mAccount = null;
        mUser = null;
        final YardSaleMain m = main;
        ListenableFuture<MobileServiceUser> mLogout = mClient.logout();
        Futures.addCallback(mLogout, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {

            }
            @Override
            public void onSuccess(MobileServiceUser user) {
                mClient = null;
                m.finish();
            }
        });
        Log.d("FblaAzure:Logoff", "Logged Off");
    }

    public boolean getLoggedOn() { return mUser != null;}

    public MobileServiceClient getClient() {
        return mClient;
    }

    public String getUserId() {
        if (mUser == null) return null;
        else return mUser.getUserId();
    }
    public String getToken() {
        if (mUser == null) return null;
        else return mUser.getAuthenticationToken();
    }

    public Account getAccount() {
        return mAccount;
    }
    public void setAccount(Account account) { mAccount = account; }

    public int getSearchMiles(Context context) {
        if (context == null) return 5;
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (prefs == null) return 5;
        return prefs.getInt("miles", 5);
    }

    public void setSearchMiles(Context context, int miles) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("miles", miles);
        editor.commit();
    }

    // Once all of the logon processes are complete, notify any listeners
    private void onLogonSuccess() {
        Log.d("FblaAzure", "onLogonSuccess");
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(null);
        }
    }

    // Notify any listeners that the logon has failed.
    private void onLogonFailure(Exception e) {
        for (LogonResultListener listener : mListeners) {
            listener.onLogonComplete(e);
        }
        Log.d("FblaAzure:Failure", e.toString());
    }

    // It seems to use WebKit to perform the OAuth authentication via Azure.
    // If successful, load the Account.  Otherwise return an exception to notify
    // the listeners that it didn't work.
    private Object providerLogon() {
        try {
            Log.d("FblaAzure:login", "Logging on to provider");
            mUser = mClient.login(PROVIDER).get();
            Log.d("FblaAzure:login", "Logged On");
            return loadAccount();
        } catch (Exception ex) {
            Log.d("FblaAzure:login", ex.toString());
            return ex;
        }
    }

    // A successfully loaded account means the token actually works and we can talk to Azure.
    // Some accounts may not be linked to a School.
    private Object loadAccount() {
        // Now load the account
        mAccountTable = mClient.getTable(Account.class);
        try {
            mAccount = mAccountTable.lookUp(mUser.getUserId()).get();
            // Found the account record, so set it on the Data object.
            Log.d("FblaAzure:account", "onSuccess - " + mAccount.getId());
            return loadSchool();
        } catch (ExecutionException e) {
            if (e.getCause().getClass() == MobileServiceException.class) {
                MobileServiceException mEx = (MobileServiceException) e.getCause();
                if (mEx.getResponse() != null && mEx.getResponse().getStatus().code == 404) { // Not Found
                    // The user is not in the table, so insert a new record for them.
                    mAccount = new Account();
                    mAccount.setId(mUser.getUserId());
                    mAccountTable.insert(mAccount);
                    Log.d("FblaAzure:account", "AccountEdit Created");
                    return null;
                } else {
                    Log.d("FblaAzure:account", mEx.toString());
                    return mEx;
                }
            } else {
                Log.d("FblaAzure:account", e.toString());
                return e;
            }
        } catch (Exception ex) {
            // Something else bad happened.
            Log.d("FblaAzure:account", ex.toString());
            return ex;
        }
    }

    // Load the school, if the Account is linked to one.
    // Return a null if everything is successful. Otherwise return an Exception.
    private Object loadSchool() {
        if (mAccount.getSchoolId() != null && mAccount.getSchool() == null) {
            Log.d("FblaAzure:school", "School " + mAccount.getSchoolId());
            mSchoolsTable = mClient.getTable(Schools.class);
            try {
                Schools school = mSchoolsTable.lookUp(mAccount.getSchoolId()).get();
                // Found the account record, so set it on the Data object.
                Log.d("FblaAzure:school", "onSuccess - "+school.getId());
                mAccount.setSchool(school);
                return null;
            } catch (ExecutionException e) {
                if (e.getCause().getClass() == MobileServiceException.class) {
                    MobileServiceException mEx = (MobileServiceException) e.getCause();
                    if (mEx.getResponse() != null && mEx.getResponse().getStatus().code == 404) { // Not Found
                        Log.d("FblaAzure:school", "School Missing");
                        return null;
                    } else {
                        Log.d("FblaAzure:school", mEx.toString());
                        return mEx;
                    }
                } else {
                    Log.d("FblaAzure:school", e.toString());
                    return e;
                }
            } catch (Exception ex) {
                // Something else bad happened.
                Log.d("FblaAzure:school", ex.toString());
                return ex;
            }
        } else {
            Log.d("FblaAzure:school", "No School");
            return null;
        }
    }

    private void clearCookies() {
        // Clear cookies and cache in order to logoff
        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                Log.d("FblaAzure:cookies", "Cookies cleared");
            }
        });
    }

    // Add a listener to call after logon is complete
    public void setLogonListener(LogonResultListener listener) {
        mListeners.add(listener);
    }

    // This is the interface to use on the logon callbacks.
    interface LogonResultListener {
        void onLogonComplete(Exception e);
    }
}
