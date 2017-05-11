/* AddSales.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This activity is used to add a new sale item.

   Both Name and Price are required for an item. This is enforced by only enabling
   the Save button when both have data.

   This activity also interacts with the phone's photo gallery and camera in order
   to include a picture of the item.

   Pictures are saved to Azure storage using FblaPicture.
*/
package com.fbla.dulaney.fblayardsale;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fbla.dulaney.fblayardsale.controller.MySalesController;
import com.fbla.dulaney.fblayardsale.databinding.ActivityAddsalesBinding;

import java.io.InputStream;
import java.util.UUID;

import com.fbla.dulaney.fblayardsale.model.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class AddSales extends AppCompatActivity implements View.OnClickListener {
    ActivityAddsalesBinding mBinding;

    private MobileServiceTable<SaleItem> mSaleItemTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsales);

        if (!FblaLogon.getLoggedOn()) {
            Toast.makeText(this, "Unable to connect to Azure. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mSaleItemTable = FblaLogon.getClient().getTable(SaleItem.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_addsales);
        FblaPicture.setLayoutImage(mBinding.activityAddsales);
        setSupportActionBar(mBinding.myToolbar);
        mBinding.gallery.setOnClickListener(this);
        mBinding.camera.setOnClickListener(this);
        mBinding.back.setOnClickListener(this);
        mBinding.finish.setOnClickListener(this);
        mBinding.another.setOnClickListener(this);

        mBinding.finish.setEnabled(false);
        mBinding.another.setEnabled(false);

        // Make sure Name is required.
        mBinding.editname.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && mBinding.editprice.getText().length() > 0) {
                    mBinding.finish.setEnabled(true);
                    mBinding.another.setEnabled(true);
                } else {
                    mBinding.finish.setEnabled(false);
                    mBinding.another.setEnabled(false);
                }
            }
        });

        // Make sure Price is required.
        mBinding.editprice.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && mBinding.editname.getText().length() > 0) {
                    mBinding.finish.setEnabled(true);
                    mBinding.another.setEnabled(true);
                } else {
                    mBinding.finish.setEnabled(false);
                    mBinding.another.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:
                // Load a picture from the phone's gallery
                // Ask for permission first
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        }
                        return;
                    }
                }

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Log.d("CameraFragment", "Starting GALLERY Intent");
                this.startActivityForResult(i, 1);
                break;
            case R.id.camera:
                // Take a picture from the camera.
                // Ask for permission first
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            // Explain to the user why we need to read the contacts
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CAMERA}, 0);
                        }
                        return;
                    }
                }

                Intent j = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                this.startActivityForResult(j, 2);
                break;
            case R.id.another:
                // Save the current item, and reload the activity to be ready for another one.
                addItem(v);
                this.finish();
                this.startActivity(new Intent(this, AddSales.class));
                break;
            case R.id.finish:
                // Save the current item and return to YardSaleMain.
                addItem(v);
                this.finish();
                break;
            default:
                // Don't save anything, just return to YardSaleMain.
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
        if (!FblaLogon.getLoggedOn()) return;

        // Create a new item from the SaleItem model.
        final SaleItem item = new SaleItem();
        item.setId(UUID.randomUUID().toString());
        item.setName(mBinding.editname.getText().toString());
        item.setAccount(FblaLogon.getAccount());
        item.setDescription(mBinding.editdesc.getText().toString());
        String sPrice = mBinding.editprice.getText().toString();
        if (sPrice == null || sPrice.equals("")) item.setPrice(0);
        else item.setPrice(Float.parseFloat(mBinding.editprice.getText().toString()));
        Bitmap b = FblaPicture.GetPictureFromView(mBinding.picture);
        if (b != null) {
            item.setPicture(b);
        }
        MySalesController.addItem(item);

        // Save the item to the database over the internet.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mSaleItemTable.insert(item);
                    Log.d("AddSales:insert", "Created item " + item.getName());
                    if (item.getHasPicture()) {
                        Bitmap picture = item.getPicture();
                        FblaPicture.UploadImage(item.getId(), picture);
                        // For some strange reason, uploading the picture destroys it on the item. So put it back.
                        item.setPicture(picture);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } catch (Exception e) {
                    Log.d("AddSales:insert", e.toString());
                }
                return null;
            }
        };
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Results can come from the Camera, Gallery, or the Comments activity.
        if (resultCode == android.app.Activity.RESULT_OK) {
            if (requestCode == 2 && data != null) { // From Camera
                Log.d("AddSales", "Result from Camera");

                try {
                    Bundle extras = data.getExtras();
                    Bitmap image = (Bitmap) extras.get("data");
                    image = FblaPicture.ResizePicture(this.getApplicationContext(), image);
                    FblaPicture.LoadPictureOnView(mBinding.picture, image);
                } catch (Exception ex) {
                    Log.e("AddSales:camera", ex.getMessage());
                }
            } else if (requestCode == 1 && data != null) // Gallery
            {
                // Gallery
                Log.d("AddSales", "Result from Gallery");

                try {
                    Uri pickedImage = data.getData();
                    InputStream stream = getContentResolver().openInputStream(pickedImage);
                    Bitmap image = BitmapFactory.decodeStream(stream);
                    image = FblaPicture.ResizePicture(this.getApplicationContext(), image);
                    FblaPicture.LoadPictureOnView(mBinding.picture, image);
                } catch (Exception ex) {
                    Log.e("AddSales:gallery", ex.getMessage());
                }
            }
        }
    } // onActivityResult
}
