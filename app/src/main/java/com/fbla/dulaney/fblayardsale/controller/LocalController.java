/* LocalController.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Used by LocalFragment to control access to the list of Sale Items in
   the user's local area (by distance from their school). Attaching a recycler
   view to the class so that when the list of items is refreshed or changed, the
   recycler view is notified of that change.

   Getting the list of nearby schools is very complicated. The Schools database
   table has been loaded with all public and private schools in the USA and its
   territories, including each school's latitude and longitude. Another table,
   called SchoolDistance, has the distance of every school within a 10-mile
   circle. This has been pre-calculated so that the query is very fast, and is
   why you are limited to either a 5-mile radius or 10-mile radius.

   We start with the school selected by the user from the Accounts page. All
   nearby schools are fetched from the SchoolDistance table. Details for each
   school is fetched from the Schools table, because we need to display those
   details with each item. Then we have to fetch all users currently tied to
   each school from the Account table. Then we fetch all items for each user
   from the SaleItem table (excluding your own). Finally, we count the number
   of comments on each item using the ItemComment table, so that it's
   displayed on the comments button.

   SchoolDistance -> Schools -> Account -> SaleItem -> ItemComment

   Finally, for each SaleItem, we download a picture (if it has one) from Azure
   storage, using FblaPicture.
*/
package com.fbla.dulaney.fblayardsale.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fbla.dulaney.fblayardsale.FblaLogon;
import com.fbla.dulaney.fblayardsale.FblaPicture;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.ItemComment;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.fbla.dulaney.fblayardsale.model.SchoolDistance;
import com.fbla.dulaney.fblayardsale.model.Schools;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;

public class LocalController {
    private static ArrayList<SaleItem> mSaleItems = new ArrayList<>();
    private static ArrayList<RecyclerView.Adapter> mAdapters = new ArrayList<>();

    public static void AttachAdapter(RecyclerView.Adapter adapter) {
        mAdapters.add(adapter);
    }

    public static int getItemCount() {
        return mSaleItems.size();
    }

    public static SaleItem getItem(int position) {
        if (mSaleItems.size() > position) return mSaleItems.get(position);
        else return null;
    }

    public static void notifyItem(SaleItem item) {
        if (mSaleItems.contains(item)) {
            int position = mSaleItems.indexOf(item);
            for (RecyclerView.Adapter adapter : mAdapters) {
                adapter.notifyItemChanged(position);
            }
        }
    }

    public static void addItem(SaleItem item) {
        mSaleItems.add(item);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void removeItem(int position) {
        mSaleItems.remove(position);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    /*
       The Refresh executes a new search. It can be called from anywhere.
       For example, when you change your school, we have to refresh.
    */
    private static MobileServiceTable<SchoolDistance> mSchoolDistanceTable;
    private static MobileServiceTable<Schools> mSchoolsTable;
    private static MobileServiceTable<Account> mAccountTable;
    private static MobileServiceTable<SaleItem> mSaleItemTable;
    private static MobileServiceTable<ItemComment> mItemCommentTable;
    public static void Refresh(Context context) {
        if (!FblaLogon.getLoggedOn()) return;
        mSaleItems.clear();

        final int searchMiles = FblaLogon.getSearchMiles(context);
        Account myAccount = FblaLogon.getAccount();
        if (myAccount.getSchool() == null) {
            for (RecyclerView.Adapter adapter : mAdapters) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        final String searchUserId = FblaLogon.getUserId();
        final Schools searchSchool = myAccount.getSchool();
        Log.d("LocalController:Refresh", searchSchool.getId()+" "+searchMiles);

        mSchoolDistanceTable = FblaLogon.getClient().getTable(SchoolDistance.class);
        mSchoolsTable = FblaLogon.getClient().getTable(Schools.class);
        mAccountTable = FblaLogon.getClient().getTable(Account.class);
        mSaleItemTable = FblaLogon.getClient().getTable(SaleItem.class);
        mItemCommentTable = FblaLogon.getClient().getTable(ItemComment.class);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    ArrayList<SaleItem> saleItems = new ArrayList<>();
                    // First get all of the schools nearby
                    Log.d("LocalController:Refresh", "Starting");
                    final MobileServiceList<SchoolDistance> distances =
                            mSchoolDistanceTable.where().field("fromid").eq(searchSchool.getId())
                                    .and().field("miles").le(searchMiles)
                                    .select("id", "fromid", "toid", "miles")
                                    .execute().get();
                    for (SchoolDistance toSchool : distances) {
                        // Get each school details
                        final Schools school = mSchoolsTable.lookUp(toSchool.getToId()).get();
                        // Get all accounts for each school
                        final MobileServiceList<Account> accounts =
                                mAccountTable.where().field("schoolid").eq(school.getId()).execute().get();
                        for (Account account : accounts) {
                            // Now get all the items for each account (excluding your own)
                            if (!account.getId().equals(searchUserId)) {
                                account.setSchool(school);
                                final MobileServiceList<SaleItem> items =
                                        mSaleItemTable.where().field("userid").eq(account.getId()).execute().get();
                                for (SaleItem item : items) {
                                    item.setAccount(account);
                                    // Get its picture
                                    if (item.getHasPicture())
                                        item.setPicture(FblaPicture.DownloadImage(item.getId()));
                                    // Finally, count the number of comments that are on each item.
                                    final MobileServiceList<ItemComment> cnt =
                                            mItemCommentTable.where().field("itemid").eq(item.getId()).includeInlineCount().execute().get();
                                    item.setNumComments(cnt.getTotalCount());
                                    saleItems.add(item);
                                }
                            }
                        }
                    }
                    return saleItems;
                } catch (Exception exception) {
                    Log.e("LocalController:Refresh", exception.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object result) {
                Log.d("LocalController:Refresh", "Complete");
                if (result != null) {
                    ArrayList<SaleItem> saleItems = (ArrayList<SaleItem>)result;
                    for (SaleItem item : saleItems) {
                        mSaleItems.add(item);
                    }
                    for (RecyclerView.Adapter adapter : mAdapters) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }
}
