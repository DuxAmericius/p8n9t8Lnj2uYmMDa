package com.fbla.dulaney.fblayardsale;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fbla.dulaney.fblayardsale.databinding.ActivityYardsaleBinding;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class YardSaleMain extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        TextFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener{

    FblaPagerAdapter mPagerAdapter;
    YardSaleMain mThis;
    int mCurrentPage;
    ViewPager mViewPager;
    ActivityYardsaleBinding mBinding;
    boolean forceClose = false;
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
        mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_yardsale);
        mBinding.home.setOnClickListener(this);
        mBinding.text.setOnClickListener(this);
        mBinding.search.setOnClickListener(this);

        //mUser = new UserInfo(this);
        mPagerAdapter = new FblaPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        //mHome = (Button) findViewById(R.id.home);
        //mText = (Button) findViewById(R.id.text);
        //mSearch = (Button) findViewById(R.id.search);
/*
        mHome.setOnClickListener(this);
        mText.setOnClickListener(this);
        mSearch.setOnClickListener(this);

        // FOR WHEN WE GET IMAGEBUTTONS
        //setPageDesplay(1);

        LinearLayout layoutMid = (LinearLayout) findViewById(R.id.layoutMid);
        //MasterPictureList.setLayoutMid(layoutMid);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /*
            @Override
            public void onPageSelected(int position) {
                setPageDisplay(position);
            }
            */
/*
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        public
*/


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

        //if (mCurrentPage != pg) {
            mViewPager.setCurrentItem(pg, true);
        //}

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
