package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.controller.CommentListController;
import com.fbla.dulaney.fblayardsale.databinding.ListCommentsBinding;
import com.fbla.dulaney.fblayardsale.model.ItemComment;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> implements View.OnClickListener {
    private View.OnClickListener mParentListener;
    ListCommentsBinding mBinding;
    Comments mContext;

    public CommentsAdapter (Comments context, View.OnClickListener onClickListener) {
        mContext = context;
        mParentListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListCommentsBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_comments, parent, false);
        View view = mBinding.getRoot();
        mBinding.delete.setOnClickListener(this);

        Log.d("CommentsAdapter", "onCreateViewHolder");
        return new ViewHolder(view, mBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!FblaLogon.getLoggedOn()) return;
        ItemComment comment = CommentListController.getComment(position);
        if (comment != null) {
            mBinding = holder.getBinding();
            Log.d("CommentsAdapter", "onBindViewHolder");
            mBinding.comments.setText(comment.getComment());
            if (comment.getAccount() == null) mBinding.username.setText("{Unknown}");
            else mBinding.username.setText(comment.getAccount().getName());
            mBinding.delete.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return CommentListController.getCommentCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                final int position = (int)v.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Are You Sure?");
                final TextView info = new TextView(mContext);
                info.setText("By Pressing Confirm, The Comment Will Be Deleted.");
                info.setPadding(30, 0, 0, 0);
                builder.setView(info);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComment(position);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            default:
                break;
        }
    }

    private void deleteComment(int position) {
        if (!FblaLogon.getLoggedOn()) return;

        final int pos = position;
        final ItemComment comment = CommentListController.getComment(position);
        final MobileServiceTable<ItemComment> mCommentTable = FblaLogon.getClient().getTable(ItemComment.class);
        // Delete the comment from the database.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mCommentTable.delete(comment);
                    Log.d("Comments:delete", "Deleted comment " + comment.getComment());
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommentListController.removeComment(pos);
                        }
                    });
                } catch (Exception e) {
                    Log.d("Comments:delete", e.toString());
                }
                return null;
            }
        };
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ListCommentsBinding mBinding;

        public ViewHolder(View itemView, ListCommentsBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public ListCommentsBinding getBinding() {
            return mBinding;
        }
    }
}
