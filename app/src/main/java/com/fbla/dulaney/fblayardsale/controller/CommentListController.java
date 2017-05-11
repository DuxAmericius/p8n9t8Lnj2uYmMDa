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

    public static int getCommentCount() {
        return mComments.size();
    }

    public static ItemComment getComment(int position) {
        if (mComments.size() > position) return mComments.get(position);
        else return null;
    }

    // Add a comment and notify the adapter of the change
    public static void addComment(ItemComment comment) {
        mComments.add(comment);
        mItem.setNumComments(mItem.getNumComments()+1);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
        // Update the count on the display, if shown
        MySalesController.notifyItem(mItem);
        LocalController.notifyItem(mItem);
    }

    // Remove a comment and notify the adapter of the change
    public static void removeComment(int position) {
        mComments.remove(position);
        mItem.setNumComments(mItem.getNumComments()-1);
        for (RecyclerView.Adapter adapter : mAdapters) {
            adapter.notifyDataSetChanged();
        }
        // Update the count on the display, if shown
        MySalesController.notifyItem(mItem);
        LocalController.notifyItem(mItem);
    }

    public static SaleItem getItem() { return mItem; }
    public static void setItem(SaleItem item) { mItem = item; }

    // Refresh all comments and notify the adapter of the change
    public static void Refresh() {
        if (!FblaLogon.getLoggedOn()) return;
        mComments.clear();

        mItemCommentTable = FblaLogon.getClient().getTable(ItemComment.class);
        final MobileServiceTable<Account> mAccountTable = FblaLogon.getClient().getTable(Account.class);
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    ArrayList<ItemComment> comments = new ArrayList<>();
                    final MobileServiceList<ItemComment> result =
                            mItemCommentTable.where().field("itemid").eq(mItem.getId()).execute().get();
                    for (ItemComment comment : result) {
                        Account account = mAccountTable.lookUp(comment.getUserId()).get();
                        comment.setAccount(account);
                        comments.add(comment);
                    }
                    return comments;
                } catch (Exception exception) {
                    Log.e("CommentListController", exception.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object result) {
                // If there are results, copy them into the array and notify the adapter.
                // This must be done on the UI thread.
                if (result != null) {
                    ArrayList<ItemComment> comments = (ArrayList<ItemComment>)result;
                    for (ItemComment comment : comments) {
                        mComments.add(comment);
                    }
                    mItem.setNumComments(comments.size());
                    for (RecyclerView.Adapter adapter : mAdapters) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.execute();
    }
}
