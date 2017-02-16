/* Data.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This class handles interacts with the Azure Mobile App server.
   The Azure Mobile App handles user authentication and holds the shared database.

*/

package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.fbla.dulaney.fblayardsale.model.Account;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Data {
    final public static String AZUREURL = "https://fbla-yardsale.azurewebsites.net";
    private static MobileServiceClient mClient = null;
    private static Context mContext;
    private static String mUserId;
    private static com.fbla.dulaney.fblayardsale.model.Account mAccount;

    // Establish an internet connection to Azure and login using a Google account.
    public static void Initialize(Context context) {
        mContext = context;
        if (mUserId != null) return;
        if (mClient != null) {
            authenticate();
            return;
        }
        try {
            mClient = new MobileServiceClient(AZUREURL, context);
            authenticate();
        } catch (MalformedURLException e) {
            mClient = null;
            Log.d("Data", "Verify Azure URL");
            //Toast.makeText(mParent, "Unable to Post Picture", Toast.LENGTH_SHORT).show();
        }
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

    public static void Logout() {
        mUserId = null;
        SharedPreferences prefs = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("usr");
        editor.remove("tkn");
        editor.commit();
        mClient = null;

        // Google+ also stores the token as a cookie, so we have to remove all
        // cookies first before initializing again.
        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                Log.d("Data:Logout", "Cookies cleared");
                Initialize(mContext);
            }
        });
    }

    // Use Azure Mobile App to authenticate with Google+.
    private static void authenticate() {
        // First try reloading from cache.
        if (loadUserTokenCache()) return;

        // Login using the Google provider.
        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Google);

        // Have to use an asyncronous call to do internet operations.
        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onSuccess(MobileServiceUser mobileServiceUser) {
                // Successfully logged on, so cache the user and token in shared preferences.
                String tkn = mobileServiceUser.getAuthenticationToken();
                mUserId = mobileServiceUser.getUserId();
                Log.d("Data:authenticate", "Logged in as " + mUserId);
                SharedPreferences prefs = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("usr", mUserId);
                editor.putString("tkn", tkn);
                editor.commit();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Data:authenticate", throwable.toString());
                mUserId = null;
            }
        });
    }

    // This function reads the user's token from cache, if it's available.
    // It then sets the token to be used by the Azure methods.
    private static boolean loadUserTokenCache() {
        // Get the user and token from Shared Preferences.
        SharedPreferences prefs = mContext.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        mUserId = prefs.getString("usr", null);
        if (mUserId == null) return false; // Exit if it doesn't exist.
        String tkn = prefs.getString("tkn", null);
        if (tkn == null) return false; // Exit if it doesn't exist.

        if (isTokenExpired(tkn)) {
            Logout(); // Logout to clear the expired token.
            // Return true so that this authorization will terminate, since the
            // logout will initialize again once cookies are cleared.
            return true;
        }

        // Load the user and token onto the Azure client object.
        MobileServiceUser user = new MobileServiceUser(mUserId);
        user.setAuthenticationToken(tkn);
        mClient.setCurrentUser(user);
        // Successfully logged in using the cached token.
        Log.d("Data:loadUserTokenCache", "Logged in from cache as " + mUserId);
        return true;
    }

    // Need to make sure the token has not expired.
    // https://blogs.msdn.microsoft.com/writingdata_services/2015/04/27/check-for-expired-azure-mobile-services-authentication-tokens/
    private static boolean isTokenExpired(String token) {
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
            Log.d("Data:isTokenExpired", isExpired ? "Expired" : "Valid");
            return isExpired;
        } catch (Exception e) {
            Log.d("Data:isTokenExpired", e.toString());
        }
        return false;
    }

    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            return task.execute();
    }

}
