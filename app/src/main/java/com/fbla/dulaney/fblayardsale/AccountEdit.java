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
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityAccountBinding;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.List;

public class AccountEdit extends AppCompatActivity implements View.OnClickListener {

    ActivityAccountBinding mBinding;
    String pressed;
    ArrayAdapter<CharSequence> mStateAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (!FblaLogon.getLoggedOn()) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        mBinding.save.setOnClickListener(this);
        mBinding.cancel.setOnClickListener(this);

        // Load the states onto the spinner from the resource file
        mStateAdapter = ArrayAdapter.createFromResource(this, R.array.states_list, android.R.layout.simple_spinner_item);
        mStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.state.setAdapter(mStateAdapter);
        setSupportActionBar(mBinding.myToolbar);

        Account account = FblaLogon.getAccount();
        int spinnerPosition = mStateAdapter.getPosition(account.getState());
        mBinding.name.setText(account.getName());
        mBinding.state.setSelection(spinnerPosition);
        mBinding.address.setText(account.getAddress());
        mBinding.chapter.setText(account.getChapter());
        mBinding.region.setText(account.getRegion());
        mBinding.zip.setText(account.getZipCode());
    }

    @Override
    public void onClick(View v) {
        //SharedPreferences pref = getSharedPreferences(pressed, Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = pref.edit();
        switch (v.getId()) {

            case R.id.save:
                if (FblaLogon.getLoggedOn()) {
                    Account account = FblaLogon.getAccount();
                    account.setName(mBinding.name.getText().toString());
                    account.setAddress(mBinding.address.getText().toString());
                    account.setChapter(mBinding.chapter.getText().toString());
                    account.setRegion(mBinding.region.getText().toString());
                    // If zip changed, refresh the LocalController
                    if (!account.getZipCode().equals(mBinding.zip.getText().toString())) {
                        account.setZipCode(mBinding.zip.getText().toString());
                        LocalController.Refresh();
                    }
                    int statePosition = mBinding.state.getSelectedItemPosition();
                    account.setState(mStateAdapter.getItem(statePosition).toString());

                    // Save the item to the database over the internet.
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                MobileServiceTable<Account> mAccountTable = FblaLogon.getClient().getTable(Account.class);
                                mAccountTable.update(FblaLogon.getAccount());
                                Log.d("AccountEdit:onClick", "AccountEdit Saved");
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
                    task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
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
