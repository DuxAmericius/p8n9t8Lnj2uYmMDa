package com.fbla.dulaney.fblayardsale;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityMysalesBinding;

public class MySales extends AppCompatActivity implements View.OnClickListener {

    ActivityMysalesBinding mBinding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysales);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mysales);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        MySalesAdapter adapter = new MySalesAdapter(this, this);
        MySalesController.AttachAdapter(adapter);
        mBinding.list.setAdapter(adapter);
        setSupportActionBar(mBinding.myToolbar);

        Log.d("MySales", "onCreate");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.comments:
                this.startActivity(new Intent(this, Comments.class));
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
