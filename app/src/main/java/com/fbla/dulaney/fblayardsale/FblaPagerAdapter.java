/* =========================== FblaPagerAdapter.java ===========================
                           Josh Talley, Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
*/

package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * FblaPagerAdapter
 *
 * Purpose: This simply loads the appropriate fragment onto the YardSaleMain activity.
 */
public class FblaPagerAdapter extends FragmentStatePagerAdapter {
    protected Context mContext;

    public FblaPagerAdapter(FragmentManager fm, Context context)
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
                fragment = new TextFragment();
                break;
            default:
                fragment = new SearchFragment();
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
}
