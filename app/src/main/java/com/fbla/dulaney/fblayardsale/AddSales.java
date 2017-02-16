/* AddSales.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This activity is used to add a new sale item.
*/
package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fbla.dulaney.fblayardsale.databinding.ActivityAddsalesBinding;
import java.net.MalformedURLException;
import java.util.UUID;

import com.fbla.dulaney.fblayardsale.model.*;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import static java.lang.Float.parseFloat;

public class AddSales extends AppCompatActivity implements View.OnClickListener {
    ActivityAddsalesBinding mBinding;
    String pressed;
    String mClicked;

    private MobileServiceTable<SaleItem> mSaleItemTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsales);
        //mThis = this;

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_addsales);
        mBinding.back.setOnClickListener(this);
        mBinding.finish.setOnClickListener(this);
        mBinding.another.setOnClickListener(this);

        // Connect to Azure and authenticate the user.
        Data.Initialize(this);
        // Get the table object for the SaleItem model.
        mSaleItemTable = Data.getClient().getTable(SaleItem.class);

        //TextView type = (TextView)findViewById(R.id.account);
/*
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
//            SharedPreferences pnone = getSharedPreferences(pressed, Context.MODE_PRIVATE);
            //          SharedPreferences.Editor enone = pnone.edit();
            //        enone.putString("click", "");
            //      enone.commit();
            this.finish();
        }
*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.another:
                this.finish();
                this.startActivity(new Intent(this, AddSales.class));
                break;
            case R.id.finish:
                addItem(v);
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

    // Add a new item to the database.
    private void addItem(View view) {
        if (Data.getClient() == null) return;

        // Create a new item from the SaleItem model.
        final SaleItem item = new SaleItem();
        item.setId(UUID.randomUUID().toString());
        item.setName(mBinding.editname.getText().toString());
        item.setUserId(Data.getUserId());
        item.setDescription(mBinding.editdesc.getText().toString());
        item.setPrice(Float.parseFloat(mBinding.editprice.getText().toString()));

        // Save the item to the database over the internet.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mSaleItemTable.insert(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Do stuff on UI here
                        }
                    });
                } catch (Exception e) {
                    Log.d("AddItem", e.toString());
                }
                return null;
            }
        };
        Data.runAsyncTask(task);
    }
}
