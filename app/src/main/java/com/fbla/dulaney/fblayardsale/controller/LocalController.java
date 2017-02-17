/* LocalController.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Used by LocalFragment to control access to the list of Sale Items in
   the user's local area (by zip code). Attaching a recycler view to the class
   so that when the list of items is refreshed or changed, the recycler view is
   notified of that change.
*/
package com.fbla.dulaney.fblayardsale.controller;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fbla.dulaney.fblayardsale.FblaLogon;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
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

    private static MobileServiceTable<Account> mAccountTable;
    private static MobileServiceTable<SaleItem> mSaleItemTable;
    public static void Refresh() {
        if (!FblaLogon.getLoggedOn()) return;
        mSaleItems.clear();

        mAccountTable = FblaLogon.getClient().getTable(Account.class);
        mSaleItemTable = FblaLogon.getClient().getTable(SaleItem.class);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Account myAccount = FblaLogon.getAccount();
                    // First get all of the accounts in the same zip code.
                    final MobileServiceList<Account> accounts =
                            mAccountTable.where().field("zipcode").eq(myAccount.getZipCode()).execute().get();
                    for (Account account : accounts) {
                        // Now get all the items for those accounts (excluding your own)
                        if (!account.getId().equals(myAccount.getId())) {
                            final MobileServiceList<SaleItem> result =
                                    mSaleItemTable.where().field("userid").eq(account.getId()).execute().get();
                            for (SaleItem item : result) {
                                item.setAccount(account);
                                mSaleItems.add(item);
                            }
                        }
                    }
                } catch (Exception exception) {
                    Log.e("MySalesController", exception.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void v) {
                for (RecyclerView.Adapter adapter : mAdapters) {
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }
}
