/* AccountEdit.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This activity is used to display and edit account information. When
   a user first logs it, you are forwarded directly to this activity.

   The user's name is forced to be required by only enabling the save button
   when you put type something in the name field.

   You can search for a school using either just a zip code, or by selecting
   a state and type in a city. The city search is done using a "starts with"
   search, so you do not have to type in the whole name. You can change the
   school at any time. Doing so will "move" all of your items to that new school.
   You may also see a different set of "nearby" or local schools, in the local
   tab or on the map, who have items for sale.

   You can also change the search radius for schools in your local area, between
   either 5 miles or 10 miles.
*/
package com.fbla.dulaney.fblayardsale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityAccountBinding;
import com.fbla.dulaney.fblayardsale.model.Account;
import com.fbla.dulaney.fblayardsale.model.Schools;
import com.fbla.dulaney.fblayardsale.model.ZipCodes;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.Collections;

public class AccountEdit extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, FblaAzure.LogonResultListener {

    private ActivityAccountBinding mBinding;
    private ArrayAdapter<CharSequence> mStateAdapter;
    private ArrayAdapter<Schools> mSchoolAdapter;
    private ArrayList<Schools> mSchools;
    private FblaAzure mAzure;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mSchools = new ArrayList<Schools>(0);
        Bundle b = getIntent().getExtras();
        String userId = b.getString("userId");
        String token = b.getString("token");
        if (userId == null || token == null) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
            return;
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        clearSchools();
        mBinding.save.setEnabled(false);
        setSupportActionBar(mBinding.myToolbar);

        // Load the states onto the spinner from the resource file
        mStateAdapter = ArrayAdapter.createFromResource(this, R.array.states_list, android.R.layout.simple_spinner_item);
        mStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.state.setAdapter(mStateAdapter);

        // Bind the schools array to the spinner
        mSchoolAdapter = new ArrayAdapter<Schools>(this, android.R.layout.simple_spinner_item, mSchools);
        mSchoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.school.setAdapter(mSchoolAdapter);

        mBinding.zip.setOnKeyListener(this);
        mBinding.city.setOnKeyListener(this);
        mBinding.save.setOnClickListener(this);
        mBinding.cancel.setOnClickListener(this);
        mBinding.searchZip.setOnClickListener(this);
        mBinding.searchCityState.setOnClickListener(this);

        // When you select a school, the city, state, and zip fields are updated with that school's info.
        mBinding.school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Schools school = (Schools)mBinding.school.getSelectedItem();
                mBinding.zip.setText(school.getZip());
                mBinding.city.setText(school.getCity());
                int spinnerPosition = mStateAdapter.getPosition(school.getStateText());
                mBinding.state.setSelection(spinnerPosition, false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                mBinding.zip.setText("");
                mBinding.city.setText("");
                mBinding.state.setSelection(0, false);

            }
        });

        mAzure = new FblaAzure(this);
        mAzure.setLogonListener(this);
        mAzure.doLogon(userId, token);

        int miles = mAzure.getSearchMiles(this);
        switch (miles) {
            case 5:
                mBinding.radius5.setChecked(true);
                break;
            case 10:
                mBinding.radius10.setChecked(true);
                break;
        }

        // Make sure Name is required.
        mBinding.name.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBinding.save.setEnabled(true);
                } else {
                    mBinding.save.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // Clear the popup keyboard if it's there.
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // Decide which button was pressed.
        switch (v.getId()) {
            case R.id.save:
                // The radius value is not saved in the database. It's stored on the phone.
                int miles = mAzure.getSearchMiles(this);
                switch (miles) {
                    case 5:
                        if (mBinding.radius10.isChecked()) {
                            mAzure.setSearchMiles(this, 10);
                        }
                        break;
                    case 10:
                        if (mBinding.radius5.isChecked()) {
                            mAzure.setSearchMiles(this, 5);
                        }
                        break;
                }
                // Everything else is saved to the database on the Account
                if (mAzure.getLoggedOn()) {
                    Account account = mAzure.getAccount();
                    account.setName(mBinding.name.getText().toString());
                    Object o = mBinding.school.getSelectedItem();
                    if (o == null || ((Schools)o).getId() == "FAKE") {
                        account.setSchool(null);
                    }
                    else {
                        Schools school = (Schools)o;
                        if (account.getSchoolId() == null || !account.getSchoolId().equals(school.getId())) {
                            account.setSchool(school);
                        }
                    }

                    // The actual command to save to Azure must be done asynchronously
                    AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Void doInBackground(Object... params) {
                            try {
                                FblaAzure azure = (FblaAzure)params[0];
                                MobileServiceTable<Account> mAccountTable = azure.getClient().getTable(Account.class);
                                Account account = azure.getAccount();
                                mAccountTable.update(account);
                                Log.d("AccountEdit:onClick", "AccountEdit Saved");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(Activity.RESULT_OK, new Intent());
                                        finish();
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("AccountEdit", e.toString());
                            }
                            return null;
                        }
                    }.execute(mAzure);
                }

                break;
            case R.id.cancel:
                // This just closes the activity, returning you to YardSaleMain.
                setResult(Activity.RESULT_CANCELED, new Intent());
                this.finish();
                break;
            case R.id.search_zip:
                // Executes a search for schools based on just the zip code.
                final String zipCode = mBinding.zip.getText().toString();
                if (zipCode.length() > 0) {
                    clearSchools();
                    searchSchools(zipCode);
                } else Log.d("AccountEdit:search", "FAIL");
                break;
            case R.id.search_city_state:
                // Executes a search for schools based on city and state.
                int statePosition = mBinding.state.getSelectedItemPosition();
                if (statePosition > 0) {
                    clearSchools();
                    String stateSearch = mStateAdapter.getItem(statePosition).toString();
                    String citySearch = mBinding.city.getText().toString();
                    Log.d("AccountEdit:search", citySearch+", "+stateSearch);
                    searchZip(citySearch, stateSearch);
                } else Log.d("AccountEdit:search", "FAIL");
            default:
                break;
        }
    }

    // This clears the schools spinner, but adds a fake "No School" entry
    private void clearSchools() {
        mSchools.clear();
        Schools fake = new Schools();
        fake.setId("FAKE");
        fake.setSchool("No School Selected");
        mSchools.add(fake);
    }

    // This removes the fake "No School" entry if it's there
    private void addSchool(Schools school) {
        if (mSchools.get(0).getId() == "FAKE") mSchools.remove(0);
        mSchools.add(school);
    }

    // This is the actual search using city and state. It actually has to do two queries.
    // First, we query the ZipCodes table for all of the zip codes matching the city and state.
    // Then we get all of the schools in each zip code. Finally, we sort that list and load
    // them into the array, which is bound to the spinner.
    private void searchZip(final String city, final String state) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    FblaAzure azure = (FblaAzure)params[0];
                    // Find matching zip codes for the city and state
                    final MobileServiceList<ZipCodes> zipCodes =
                            azure.getClient().getTable(ZipCodes.class).where()
                                    .field("stateText").eq(state)
                                    .and().startsWith("city", city)
                                    .orderBy("city", QueryOrder.Ascending).execute().get();
                    ArrayList<String> uniqueZips = new ArrayList<>();
                    // Remove duplicate zip codes (some cities have multiple versions of the same name)
                    for (ZipCodes zip : zipCodes) {
                        if (!uniqueZips.contains(zip.getZip())) uniqueZips.add(zip.getZip());
                    }
                    final ArrayList<Schools> allSchools = new ArrayList<>();
                    for (String z : uniqueZips) {
                        // Now get all of the schools in each zip code
                        final MobileServiceList<Schools> schools =
                                azure.getClient().getTable(Schools.class).where()
                                        .field("zip").eq(z)
                                        .orderBy("school", QueryOrder.Ascending).execute().get();
                        for (Schools school : schools) allSchools.add(school);
                    }
                    // Sort the list
                    Collections.sort(allSchools);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        // Put the schools into the array and notify the spinner that it's different.
                        for (Schools s : allSchools) {
                            addSchool(s);
                        }
                        mSchoolAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    Log.d("SearchZip", e.toString());
                }
                return null;
            }
        }.execute(mAzure);
    }

    // This is a very straight forward fetch of all schools in a given zip code.
    private void searchSchools(final String zip) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    FblaAzure azure = (FblaAzure)params[0];
                    final MobileServiceList<Schools> schools =
                            azure.getClient().getTable(Schools.class).where()
                                    .field("zip").eq(zip)
                                    .orderBy("school", QueryOrder.Ascending).execute().get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Schools s : schools) {
                                addSchool(s);
                                Log.d("SearchSchool", s.toString());
                            }
                            mSchoolAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    Log.d("SearchSchool", e.toString());
                }

                return null;
            }
        }.execute(mAzure);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED, new Intent());
        this.finish();
    }

    // This allows the user to press enter on the popup keyboard and
    // have it automatically execute the corresponding search (zip or city/state).
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    switch (v.getId()) {
                        case R.id.zip:
                            this.onClick(mBinding.searchZip);
                            break;
                        case R.id.city:
                            this.onClick(mBinding.searchCityState);
                            break;
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    @Override
    public void onLogonComplete(Exception e) {
        // Display your current school.
        Account account = mAzure.getAccount();
        mBinding.name.setText(account.getName());
        mBinding.save.setEnabled(account.getName().length() > 0);
        if (account.getSchool() != null) {
            Schools school = account.getSchool();
            addSchool(school);
            mSchoolAdapter.notifyDataSetChanged();
            mBinding.zip.setText(school.getZip());
            mBinding.city.setText(school.getCity());
            int spinnerPosition = mStateAdapter.getPosition(school.getStateText());
            mBinding.state.setSelection(spinnerPosition, false);        }
    }
}
