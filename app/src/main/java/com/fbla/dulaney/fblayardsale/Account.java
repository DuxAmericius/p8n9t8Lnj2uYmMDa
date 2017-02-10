package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fbla.dulaney.fblayardsale.databinding.ActivityAccountBinding;

public class Account extends AppCompatActivity implements View.OnClickListener {

    //Account mThis;
    ActivityAccountBinding mBinding;
    String pressed;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        mBinding.state.setOnClickListener(this);
        mBinding.region.setOnClickListener(this);
        mBinding.chapter.setOnClickListener(this);
        mBinding.address.setOnClickListener(this);
        mBinding.zip.setOnClickListener(this);

        //SharedPreferences sharedpreferences = getSharedPreferences(pressed, Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.state:
                SharedPreferences sharedpreferences = getSharedPreferences(pressed, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("click", 0);
                editor.commit();
                this.startActivity(new Intent(this, AccountState.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        //this.startActivity(new Intent(this, YardSaleMain.class));
        this.finish();
    }


}
