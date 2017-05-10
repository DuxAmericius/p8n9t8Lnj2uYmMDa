/* FblaPagerAdapter.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This simply loads the appropriate fragment onto the YardSaleMain activity.

   It also slightly adjusts the color of the button icons that represent each
   fragment (appearing as tabs).
*/
package com.fbla.dulaney.fblayardsale;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class FblaPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
    protected YardSaleMain mContext;

    public FblaPagerAdapter(FragmentManager fm, YardSaleMain context)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position)
        {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new LocalFragment();
                break;
            default:
                fragment = new MapFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putInt("page_position", position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0: // Home
                mContext.mBinding.home.setTextColor(Color.BLACK);
                mContext.mBinding.local.setTextColor(Color.DKGRAY);
                mContext.mBinding.map.setTextColor(Color.DKGRAY);
                break;
            case 1: // Local
                mContext.mBinding.home.setTextColor(Color.DKGRAY);
                mContext.mBinding.local.setTextColor(Color.BLACK);
                mContext.mBinding.map.setTextColor(Color.DKGRAY);
                break;
            case 2: // Map
                mContext.mBinding.home.setTextColor(Color.DKGRAY);
                mContext.mBinding.local.setTextColor(Color.DKGRAY);
                mContext.mBinding.map.setTextColor(Color.BLACK);
                break;
            default:
                Log.d("FblaPager:Selected", "Other");
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
