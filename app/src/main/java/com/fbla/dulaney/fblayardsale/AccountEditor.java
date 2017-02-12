package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.databinding.ActivityAccounteditorBinding;

public class AccountEditor extends AppCompatActivity implements View.OnClickListener {

    //Account mThis;
    ActivityAccounteditorBinding mBinding;
    String pressed;
    String mClicked;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounteditor);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounteditor);
        mBinding.back.setOnClickListener(this);
        mBinding.enter.setOnClickListener(this);

        TextView type = (TextView)findViewById(R.id.accounttype);

        SharedPreferences sharedpreferences = getSharedPreferences(pressed, Context.MODE_PRIVATE);
        mClicked = sharedpreferences.getString("click", "button");
        Log.d("Comments", "onCreate for button clicked (" + mClicked + ")");
        if (mClicked.equals(getString(R.string.accountstate))) {
            type.setText("Enter State Name:");
        } else if (mClicked.equals(getString(R.string.accountregion))) {
            type.setText("Enter Region Number:");
        } else if (mClicked.equals(getString(R.string.accountchapter))) {
            type.setText("Enter Chapter Name:");
        } else if (mClicked.equals(getString(R.string.accountaddress))) {
            type.setText("Enter Address:");
        } else if (mClicked.equals(getString(R.string.accountzip))) {
            type.setText("Enter Zip:");
        } else {
            this.finish();
        }



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
        this.finish();
    }

}
