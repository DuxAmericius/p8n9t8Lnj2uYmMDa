package com.fbla.dulaney.fblayardsale;
import android.widget.LinearLayout;

/**
 * MasterPictureList
 *
 * Purpose: Since the ViewPager will cache fragments, it can be difficult to manage the list of
 * pictures displayed in the fragments. To get around this difficulty, we created this static
 * class to keep track of all pictures lists that are currently loaded in memory. Whenever a
 * fragment creates a new PictureInfoAdapter, it sets a variable here to point to that adapter.
 * Whenever that fragment is removed from cache (because the user has navigated away from it), that
 * variable is cleared. The observer design pattern was used to do this. Calling the static methods
 * RefreshAll or Refresh will refresh the PictureInfoAdapter results, if it exists, from anywhere
 * in the program. This class also exposes a way to calculate the height of an image in the layout.
 * This is needed in order to dynamically adjust the card views in each PictureInfoAdapter so that
 * the pictures are displayed proportionally to the phone's display.
 */
public class MasterPictureList {

    /*
    private static PictureInfoAdapter home = null, collective = null, favorite = null, profile = null;
    private static LinearLayout mLayoutMid;

    public static void setLayoutMid(LinearLayout layout)
    {
        mLayoutMid = layout;
    }
    public static int getImageHeight()
    {
        if (mLayoutMid.getHeight() > 0)
            return mLayoutMid.getHeight() / 2;
        else return 0;
    }

    public static void RefreshAll()
    {
        if (home != null) home.Refresh();
        if (collective != null) collective.Refresh();
        if (favorite != null) favorite.Refresh();
        if (profile != null) profile.Refresh();
    }

    public static void Refresh(ListType listType)
    {
        switch (listType)
        {
            case HOME:
                if (home != null) home.Refresh();
                break;
            case COLLECTIVE:
                if (collective != null) collective.Refresh();
                break;
            case FAVORITE:
                if (favorite != null) favorite.Refresh();
                break;
            case PROFILE:
                if (profile != null) profile.Refresh();
                break;
        }
    }

    public static void Attach(PictureInfoAdapter adapter)
    {
        switch (adapter.getPictureType())
        {
            case HOME:
                home = adapter;
                break;
            case COLLECTIVE:
                collective = adapter;
                break;
            case FAVORITE:
                favorite = adapter;
                break;
            case PROFILE:
                profile = adapter;
                break;
        }
    }
    public static void Detach(PictureInfoAdapter adapter)
    {
        switch (adapter.getPictureType())
        {
            case HOME:
                home = null;
                break;
            case COLLECTIVE:
                collective = null;
                break;
            case FAVORITE:
                favorite = null;
                break;
            case PROFILE:
                profile = null;
                break;
        }
    }
    */
}
