/* MySales.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This activity lists all of the items you have for sale. It allows
   you to delete any item, or look at their comments.
*/
package com.fbla.dulaney.fblayardsale;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityMysalesBinding;

public class MySales extends AppCompatActivity implements View.OnClickListener, FblaAzure.LogonResultListener {
    private ActivityMysalesBinding mBinding;
    private FblaAzure mAzure;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysales);
        Bundle b = getIntent().getExtras();
        String userId = b.getString("userId");
        String token = b.getString("token");
        if (userId == null || token == null) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mAzure = new FblaAzure(this);
        mAzure.setLogonListener(this);
        mAzure.doLogon(userId, token);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mysales);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mBinding.myToolbar);

        Log.d("MySales", "onCreate");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.comments:
                Intent i = new Intent(this, Comments.class);
                Bundle b = new Bundle();
                b.putString("userId", mAzure.getUserId());
                b.putString("token", mAzure.getToken());
                i.putExtras(b);
                this.startActivity(i);
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

    @Override
    public void onLogonComplete(Exception e) {
        MySalesAdapter adapter = new MySalesAdapter(this, this, mAzure);
        MySalesController.AttachAdapter(adapter);
        mBinding.list.setAdapter(adapter);
    }
}
