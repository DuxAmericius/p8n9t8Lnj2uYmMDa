/* SaleItem.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for sale item information.
   This class is used by the Azure library to query and create data in the
   SaleItem database table.

   We store the number of comments and a link to the Account object
   so that additional details can be displayed.
*/
package com.fbla.dulaney.fblayardsale.model;

import android.graphics.Bitmap;
import com.fbla.dulaney.fblayardsale.FblaPicture;

public class SaleItem {
    // Database Columns
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique value created as a random UUID.
    @com.google.gson.annotations.SerializedName("userid")
    private String mUserId; // Foreign key to the Account.
    @com.google.gson.annotations.SerializedName("name")
    private String mName; // Name of the item.
    @com.google.gson.annotations.SerializedName("description")
    private String mDescription; // Description of the item.
    @com.google.gson.annotations.SerializedName("price")
    private float mPrice; // Price of the item.
    @com.google.gson.annotations.SerializedName("hasPicture")
    private boolean mHasPicture; // If a picture has been added or not.

    // Transient fields will not get queried or saved to the database
    @com.google.gson.annotations.Expose(serialize = false)
    private transient Bitmap mPicture;
    @com.google.gson.annotations.Expose(serialize = false)
    private transient int mNumComments; // Number of comments
    @com.google.gson.annotations.Expose(serialize = false)
    private transient Account mAccount;

    public SaleItem() {
        mAccount = null;
        mName = "";
        mId = "";
        mUserId = null;
        mDescription = "";
        mPicture = null;
        mPrice = 0;
        mNumComments = 0;
        mHasPicture = false;
    }

    @Override
    public String toString() {
        return getId();
    }

    // Getters and Setters
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }
    public String getName() { return mName; }
    public final void setName(String name) { mName = name; }
    public String getDescription() { return mDescription; }
    public final void setDescription(String description) { mDescription = description; }
    public float getPrice() { return mPrice; }
    public final void setPrice(float price) { mPrice = price; }
    public boolean getHasPicture() { return mHasPicture; }
    public final void setHasPicture(boolean hasPicture) { mHasPicture = hasPicture; }
    public Bitmap getPicture() { return mPicture; }
    public final void setPicture(Bitmap image) {
        mPicture = image;
        mHasPicture = (image != null);
    }
    public Account getAccount() { return mAccount; }
    // Setting the Account will automatically set the database foreign key, too.
    public final void setAccount(Account account) {
        mAccount = account;
        mUserId = account.getId();
    }
    public int getNumComments() { return mNumComments; }
    public final void setNumComments(int numComments) {
        mNumComments = numComments;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SaleItem && ((SaleItem)o).mId == mId;
    }
}
