/* CommentListController.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Used by CommentList to control access to the list of comments for
   a selected item. Attaching a recycler view to the class so that when the list of
   items is refreshed or changed, the recycler view is notified of that change.
*/
package com.fbla.dulaney.fblayardsale.controller;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fbla.dulaney.fblayardsale.FblaLogon;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.ItemComment;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;

public class CommentListController {
    private static ArrayList<ItemComment> mComments = new ArrayList<>();
    private static ArrayList<RecyclerView.Adapter> mAdapters = new ArrayList<>();
    private static MobileServiceTable<ItemComment> mItemCommentTable;
    private static SaleItem mItem;

    public static void AttachAdapter(RecyclerView.Adapter adapter) {
        mAdapters.add(adapter);
    }
    public static void RemoveAdapter(RecyclerView.Adapter adapter) { mAdapters.remove(adapter); }

    public static int getCommentCount() {
        return mComments.size();
    }

    public static ItemComment getComment(int position) {
        if (mComments.size() > position) return mComments.get(position);
        else return null;
    }

    public static void addComment(ItemComment comment) {
        mComments.add(comment);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void removeComment(int position) {
        mComments.remove(position);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static SaleItem getItem() { return mItem; }
    public static void setItem(SaleItem item) { mItem = item; }

    public static void Refresh() {
        if (!FblaLogon.getLoggedOn()) return;
        mComments.clear();

        mItemCommentTable = FblaLogon.getClient().getTable(ItemComment.class);
        final MobileServiceTable<Account> mAccountTable = FblaLogon.getClient().getTable(Account.class);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<ItemComment> result =
                            mItemCommentTable.where().field("itemid").eq(mItem.getId()).execute().get();
                    for (ItemComment comment : result) {
                        Account account = mAccountTable.lookUp(comment.getUserId()).get();
                        comment.setAccount(account);
                        mComments.add(comment);
                    }
                } catch (Exception exception) {
                    Log.e("CommentListController", exception.toString());
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
