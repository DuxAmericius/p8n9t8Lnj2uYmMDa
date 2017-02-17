/* Help.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This activity displays all of the help information using a
   recycler view.
*/
package com.fbla.dulaney.fblayardsale;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.fbla.dulaney.fblayardsale.databinding.ActivityHelpBinding;

public class Help extends AppCompatActivity implements View.OnClickListener
{
    ActivityHelpBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        mBinding.done.setOnClickListener(this);
        mBinding.listHelp.setLayoutManager(new LinearLayoutManager(this));
        mBinding.listHelp.setAdapter(new HelpAdapter());
        setSupportActionBar(mBinding.myToolbar);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.done:
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
