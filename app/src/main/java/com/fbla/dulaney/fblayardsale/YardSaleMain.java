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
   This activity will also execute the initial login using a Microsoft account.
*/

package com.fbla.dulaney.fblayardsale;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityYardsaleBinding;
import com.fbla.dulaney.fblayardsale.model.Account;

public class YardSaleMain extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        LocalFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        FblaAzure.LogonResultListener{

    // Class Variables
    private FblaPagerAdapter mPagerAdapter;
    public ActivityYardsaleBinding mBinding;
    private FblaAzure mAzure;
    private boolean mLogonComplete = false;
    private String mTitle;

    public void Logoff() {
        mAzure.doLogoff(this);
        //this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("YardSaleMain", "onCreate");
        setContentView(R.layout.activity_yardsale);

        mAzure = new FblaAzure(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_yardsale);
        mPagerAdapter = new FblaPagerAdapter(getSupportFragmentManager(), this);
        mBinding.pager.setAdapter(mPagerAdapter);
        mBinding.pager.addOnPageChangeListener(mPagerAdapter);
        mPagerAdapter.onPageSelected(0);
        mBinding.home.setOnClickListener(this);
        mBinding.local.setOnClickListener(this);
        mBinding.map.setOnClickListener(this);
        setSupportActionBar(mBinding.myToolbar);
        mTitle = mBinding.myToolbar.getTitle().toString();

        // Make sure everything is disabled until the logon completes
        mBinding.local.setEnabled(false);
        mBinding.map.setEnabled(false);
        mBinding.pager.setEnabled(false);

        mAzure.setLogonListener(this);
        mAzure.doLogon();
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

    public FblaAzure getAzure() { return mAzure; }

    private HomeFragment mHomeFragment = null;
    public void onHomeAttach(HomeFragment f) {
        mHomeFragment = f;
        mHomeFragment.setEnabled(mLogonComplete);
    }
    public void onHomeDetach(HomeFragment f) {
        mHomeFragment = null;
    }

    private LocalFragment mLocalFragment = null;
    public void onLocalAttach(LocalFragment f) {
        mLocalFragment = f;
        mLocalFragment.setEnabled(mLogonComplete);
    }
    public void onLocalDetach(LocalFragment f) {
        mLocalFragment = null;
    }

    private MapFragment mMapFragment = null;
    public void onMapAttach(MapFragment f) {
        mMapFragment = f;
        mMapFragment.setEnabled(mLogonComplete);
    }
    public void onMapDetach(MapFragment f) {
        mMapFragment = null;
    }

    public void onLogonComplete(Exception e) {
        if (e != null) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            finish();
        } else if (!mLogonComplete) {
            Log.d("YardSaleMain", "Logon Complete");
            Account account = mAzure.getAccount();
            mLogonComplete = true;
            mBinding.myToolbar.setTitle(mTitle + " - " + account.getName());
            mBinding.local.setEnabled(true);
            mBinding.map.setEnabled(true);
            mBinding.pager.setEnabled(true);
            if (mHomeFragment != null) {
                mHomeFragment.setEnabled(true);
            }
            if (mLocalFragment != null) {
                mLocalFragment.setEnabled(true);
            }
            if (mMapFragment != null) {
                mMapFragment.setEnabled(true);
            }

            if (account.getName() == null || account.getName().equals("")) {
                Intent i = new Intent(this, AccountEdit.class);
                Bundle b = new Bundle();
                b.putString("userId", mAzure.getUserId());
                b.putString("token", mAzure.getToken());
                i.putExtras(b);
                this.startActivityForResult(i, 0);
            } else {
                MySalesController.Refresh(mAzure);
                LocalController.Refresh(this, mAzure);
            }
        } else {
            Account account = mAzure.getAccount();
            MySalesController.Refresh(mAzure);
            LocalController.Refresh(this, mAzure);
            mBinding.myToolbar.setTitle(mTitle + " - " + account.getName());
        }
    }

    // Require two presses on the back button to exit the activity.
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mAzure.doLoadAccount();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAzure = null;
    }
}
