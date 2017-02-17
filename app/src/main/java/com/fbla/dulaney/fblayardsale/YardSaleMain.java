/* YardSaleMain.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the main startup activity. It uses the FblaPagerAdapter to manage 2 different
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
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityYardsaleBinding;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class YardSaleMain extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        LocalFragment.OnFragmentInteractionListener,
        FblaLogon.LogonResultListener{

    // Class Variables
    FblaPagerAdapter mPagerAdapter;
    int mCurrentPage;
    ActivityYardsaleBinding mBinding;
    FblaLogon mLogon;
    public void Logoff() {
        mLogon.Logoff();
        //finish();
        mLogon = new FblaLogon(this);
        mLogon.setLogonListener(this);
        mLogon.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
    //private MobileServiceTable<AccountEdit> mAccountTable;

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

        mLogon = new FblaLogon(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_yardsale);
        mPagerAdapter = new FblaPagerAdapter(getSupportFragmentManager(), this);
        mBinding.pager.setAdapter(mPagerAdapter);
        mBinding.home.setOnClickListener(this);
        mBinding.local.setOnClickListener(this);
        setSupportActionBar(mBinding.myToolbar);

        mLogon.setLogonListener(this);
        if (!FblaLogon.getLoggedOn())
            mLogon.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

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
            case R.id.local:
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

    public void onLocalInteraction(View v)
    {

    }

    @Override
    public void onLogonComplete(Exception e) {
        if (e != null) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            MySalesController.Refresh();
            LocalController.Refresh();
            String name = FblaLogon.getAccount().getName();
            if (name == null || name.equals("")) {
                startActivity(new Intent(this, AccountEdit.class));
            }
        }
    }
}
