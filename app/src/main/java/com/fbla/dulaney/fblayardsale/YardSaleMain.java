/* YardSaleMain.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the main startup activity. It uses the FblaPagerAdapter to manage 3 different
   activity fragments. This activity has the title bar and navigation buttons.
   The ViewPager fills the center, which holds each page fragment. It automatically handles
   swipes and smooth transitions between each page. Most navigation is handled by this activity.
   This activity will also execute the initial login using a Google+ account.  The login
   uses Azure Mobile Apps to get the user's Google ID and Token, which is cached and used
   by all other database calls to Azure Mobile Apps.
*/

package com.fbla.dulaney.fblayardsale;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fbla.dulaney.fblayardsale.databinding.ActivityYardsaleBinding;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class YardSaleMain extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        TextFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener{

    // Class Variables
    FblaPagerAdapter mPagerAdapter;
    int mCurrentPage;
    ActivityYardsaleBinding mBinding;
    private MobileServiceTable<Account> mAccountTable;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //private static final String TEMP_FILENAME = "Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yardsale);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_yardsale);
        mBinding.home.setOnClickListener(this);
        mBinding.text.setOnClickListener(this);
        mBinding.search.setOnClickListener(this);

        // Connect to Azure and authenticate the user.
        Data.Initialize(this);
        // See if there's already a row in the Account table
        mAccountTable = Data.getClient().getTable(Account.class);
        ListenableFuture<Account> account = mAccountTable.lookUp(Data.getUserId());
        Futures.addCallback(account, new FutureCallback<Account>() {
            @Override
            public void onFailure(Throwable exc) {
                // See what kind of exception it is
                if (exc.getMessage().equals("{\"error\":\"The item does not exist\"}")) {
                    // The user is not in the table, so insert a new record for them.
                    Account act = new Account();
                    act.setId(Data.getUserId());
                    mAccountTable.insert(act);
                    Data.setAccount(act);
                    Log.d("YardSaleMain:onCreate", "Account Created");
                } else {
                    // Something else bad happened.
                    Log.d("YardSaleMain:onCreate", exc.toString());
                }
            }

            @Override
            public void onSuccess(Account result) {
                // Found the account record, so set it on the Data object.
                Log.d("YardSaleMain:onCreate", "onSuccess - "+result.getId());
                Data.setAccount(result);
            }
        });

        //mUser = new UserInfo(this);
        mPagerAdapter = new FblaPagerAdapter(getSupportFragmentManager(), this);
        mBinding.pager.setAdapter(mPagerAdapter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //@Override
    public void onClick(View v) {
        // Perform page changes so they transition just like a swipe.
        int pg;
        switch (v.getId()) {
            case R.id.home:
                pg = 0;
                break;
            case R.id.text:
                pg = 1;
                break;
            default:
                pg = 2;
                break;
        }
        mBinding.pager.setCurrentItem(pg, true);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("YardSaleMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void onHomeInteraction(View v)
    {

    }

    public void onTextInteraction(View v)
    {

    }

    public void onSearchInteraction(View v)
    {

    }

}
