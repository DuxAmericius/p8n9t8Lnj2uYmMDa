package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.databinding.ActivityAccountstateBinding;

public class AccountState extends AppCompatActivity implements View.OnClickListener {

    //Account mThis;
    ActivityAccountstateBinding mBinding;
    String pressed;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountstate);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_accountstate);
        mBinding.back.setOnClickListener(this);
        mBinding.enter.setOnClickListener(this);

        SharedPreferences sharedpreferences = getSharedPreferences(pressed, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        sharedpreferences.getInt("click", 0);
        editor.commit();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.enter:
                this.finish();
                break;
            default:
                this.finish();
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
