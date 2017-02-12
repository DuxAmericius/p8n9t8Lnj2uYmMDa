package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.databinding.ActivityCommentsBinding;

public class Comments extends AppCompatActivity implements View.OnClickListener {

    //Account mThis;
    ActivityCommentsBinding mBinding;
    String pressed;
    String mClicked;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comments);
        mBinding.comments.setOnClickListener(this);
        mBinding.post.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.comments:
                this.finish();
                break;
            case R.id.post:
                this.finish();
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

}
