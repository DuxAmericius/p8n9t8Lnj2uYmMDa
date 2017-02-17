/* LocalAdapter.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the recycler view adapter for the Local fragment.
*/
package com.fbla.dulaney.fblayardsale;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.controller.CommentListController;
import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.databinding.ListItemsBinding;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.SaleItem;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> implements View.OnClickListener {
    private View.OnClickListener mParentListener;
    private ListItemsBinding mBinding;

    public LocalAdapter (View.OnClickListener onClickListener) {
        mParentListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemsBinding mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.list_items, parent, false);
        mBinding.sold.setOnClickListener(this);
        mBinding.sold.setVisibility(View.GONE); //this will be variable based on account info
        mBinding.comments.setOnClickListener(this);
        View view = mBinding.getRoot();

        return new ViewHolder(view, mBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!FblaLogon.getLoggedOn()) return;
        SaleItem item = LocalController.getItem(position);
        if (item != null) {
            mBinding = holder.getBinding();
            Log.d("LocalAdapter", "onBindViewHolder");
            mBinding.comments.setTag(position);
            mBinding.name.setText(item.getName());
            mBinding.price.setText(String.format("$%.2f", item.getPrice()));
            mBinding.description.setText(item.getDescription());
            Account account = item.getAccount();
            if (account != null) {
                mBinding.address.setText(account.getAddress());
                mBinding.chapter.setText(account.getChapter());
                mBinding.zipcode.setText(account.getZipCode());
            }
            Bitmap image = item.getPicture();
            if (image != null) {
                FblaPicture.setLayoutImage(mBinding.layoutPicture);
                FblaPicture.LoadPictureOnView(mBinding.picture, image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return LocalController.getItemCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comments:
                if (FblaLogon.getLoggedOn()) {
                    int position = (int)v.getTag();
                    CommentListController.setItem(LocalController.getItem(position));
                    CommentListController.Refresh();
                    mParentListener.onClick(v);
                }
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
}
