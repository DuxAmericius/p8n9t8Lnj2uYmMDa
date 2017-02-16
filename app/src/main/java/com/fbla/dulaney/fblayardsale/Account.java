package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.fbla.dulaney.fblayardsale.databinding.ActivityAccountBinding;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;

public class Account extends AppCompatActivity implements View.OnClickListener {

    ActivityAccountBinding mBinding;
    String pressed;
    ArrayAdapter<CharSequence> mStateAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        mBinding.save.setOnClickListener(this);
        mBinding.cancel.setOnClickListener(this);

        // Load the states onto the spinner from the resource file
        mStateAdapter = ArrayAdapter.createFromResource(this, R.array.states_list, android.R.layout.simple_spinner_item);
        mStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.state.setAdapter(mStateAdapter);

        com.fbla.dulaney.fblayardsale.model.Account account = Data.getAccount();
        int spinnerPosition = mStateAdapter.getPosition(account.getState());
        mBinding.state.setSelection(spinnerPosition);
        mBinding.address.setText(account.getAddress());
        mBinding.chapter.setText(account.getChapter());
        mBinding.region.setText(account.getRegion());
        mBinding.zip.setText(account.getZipCode());

        //SharedPreferences sharedpreferences = getSharedPreferences(pressed, Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences pref = getSharedPreferences(pressed, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        switch (v.getId()) {

            case R.id.save:
                com.fbla.dulaney.fblayardsale.model.Account account = Data.getAccount();
                account.setAddress(mBinding.address.getText().toString());
                account.setChapter(mBinding.chapter.getText().toString());
                account.setRegion(mBinding.region.getText().toString());
                account.setZipCode(mBinding.zip.getText().toString());
                int statePosition = mBinding.state.getSelectedItemPosition();
                account.setState(mStateAdapter.getItem(statePosition).toString());

                // Save the item to the database over the internet.
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            MobileServiceTable<com.fbla.dulaney.fblayardsale.model.Account> mAccountTable = Data.getClient().getTable(com.fbla.dulaney.fblayardsale.model.Account.class);
                            mAccountTable.update(Data.getAccount());
                            Log.d("Account:onClick", "Account Saved");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        } catch (Exception e) {
                            Log.d("AddItem", e.toString());
                        }
                        return null;
                    }
                };
                Data.runAsyncTask(task);

                break;
            case R.id.cancel:
                this.finish();
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
