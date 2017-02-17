package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.controller.CommentListController;
import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityCommentsBinding;
import com.fbla.dulaney.fblayardsale.model.ItemComment;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.UUID;

public class Comments extends AppCompatActivity implements View.OnClickListener {

    ActivityCommentsBinding mBinding;
    MobileServiceTable<ItemComment> mCommentTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comments);
        mBinding.post.setOnClickListener(this);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        CommentsAdapter adapter = new CommentsAdapter(this, this);
        CommentListController.AttachAdapter(adapter);
        mBinding.list.setAdapter(adapter);
        setSupportActionBar(mBinding.myToolbar);

        mCommentTable = FblaLogon.getClient().getTable(ItemComment.class);

        Log.d("Comments", "onCreate");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.comments:
                this.finish();
                break;
            case R.id.post:
                if (!mBinding.newcomment.getText().toString().equals("")) {
                    addItem(v);
                }
                //this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        this.finish();
    }

    // Add a new item to the database.
    private void addItem(View view) {
        if (!FblaLogon.getLoggedOn()) return;

        // Create a new comment from the ItemComment model.
        final ItemComment comment = new ItemComment();
        comment.setId(UUID.randomUUID().toString());
        comment.setComment(mBinding.newcomment.getText().toString());
        comment.setUserId(FblaLogon.getUserId());
        comment.setItemId(CommentListController.getItem().getId());

        // Save the item to the database over the internet.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mCommentTable.insert(comment);
                    Log.d("Comments:insert", "Created comment " + comment.getComment());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            comment.setAccount(FblaLogon.getAccount());
                            CommentListController.addComment(comment);
                        }
                    });
                } catch (Exception e) {
                    Log.d("Comments:insert", e.toString());
                }
                return null;
            }
        };
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            mBinding.newcomment.setText("");
        }
    }

}
