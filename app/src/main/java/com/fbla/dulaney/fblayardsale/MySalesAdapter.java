package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.controller.CommentListController;
import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ListItemsBinding;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class MySalesAdapter extends RecyclerView.Adapter<MySalesAdapter.ViewHolder> implements View.OnClickListener {
    private View.OnClickListener mParentListener;
    ListItemsBinding mBinding;
    MySales mContext;

    public MySalesAdapter (MySales context, View.OnClickListener onClickListener) {
        mContext = context;
        mParentListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemsBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_items, parent, false);
        mBinding.sold.setOnClickListener(this);
        mBinding.comments.setOnClickListener(this);
        mBinding.layoutAddress.setVisibility(View.GONE);
        mBinding.layoutChapter.setVisibility(View.GONE);
        mBinding.layoutZipcode.setVisibility(View.GONE);
        View view = mBinding.getRoot();

        return new ViewHolder(view, mBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!FblaLogon.getLoggedOn()) return;
        SaleItem item = MySalesController.getItem(position);
        if (item != null) {
            mBinding = holder.getBinding();
            Log.d("MySalesAdapter", "onBindViewHolder");
            mBinding.comments.setTag(position);
            mBinding.name.setText(item.getName());
            mBinding.price.setText(String.format("$%.2f", item.getPrice()));
            mBinding.description.setText(item.getDescription());
            mBinding.sold.setTag(position);
            Bitmap image = item.getPicture();
            if (image != null) {
                FblaPicture.setLayoutImage(mBinding.layoutPicture);
                FblaPicture.LoadPictureOnView(mBinding.picture, image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return MySalesController.getItemCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comments:
                if (FblaLogon.getLoggedOn()) {
                    int position = (int)v.getTag();
                    SaleItem item = MySalesController.getItem(position);
                    CommentListController.setItem(item);
                    CommentListController.Refresh();
                    Log.d("MySalesAdapter", "Refreshed for " + position);
                    mParentListener.onClick(v);
                }
                break;
            case R.id.sold:
                final int position = (int)v.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Are You Sure?");
                final TextView info = new TextView(mContext);
                info.setText("By Pressing Confirm, The Item Will Be Deleted.");
                info.setPadding(30, 0, 0, 0);
                builder.setView(info);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MySalesAdapter", "delete");
                        deleteItem(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ListItemsBinding mBinding;

        public ViewHolder(View itemView, ListItemsBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public ListItemsBinding getBinding() {
            return mBinding;
        }
    }

    private void deleteItem(int position) {
        if (!FblaLogon.getLoggedOn()) return;

        final int pos = position;
        final SaleItem item = MySalesController.getItem(position);
        final MobileServiceTable<SaleItem> mSaleItemTable = FblaLogon.getClient().getTable(SaleItem.class);
        // Delete the comment from the database.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mSaleItemTable.delete(item);
                    Log.d("MySales:delete", "Deleted item " + item.getName());
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MySalesController.removeItem(pos);
                        }
                    });
                } catch (Exception e) {
                    Log.d("MySales:delete", e.toString());
                }
                return null;
            }
        };
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
}
