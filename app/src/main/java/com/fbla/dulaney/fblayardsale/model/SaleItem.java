/* SaleItem.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for sale item information.
*/
package com.fbla.dulaney.fblayardsale.model;

import android.graphics.Bitmap;

import com.fbla.dulaney.fblayardsale.FblaPicture;

public class SaleItem {
    /*
    Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /*
    User Id
     */
    @com.google.gson.annotations.SerializedName("userid")
    private String mUserId;


    /*
    User Id
     */
    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    /*
    Item Description
     */
    @com.google.gson.annotations.SerializedName("description")
    private String mDescription;

    /*
    Item Price
     */
    @com.google.gson.annotations.SerializedName("price")
    private float mPrice;

    /*
    Picture (Base64 Encoded String)
     */
    @com.google.gson.annotations.SerializedName("picture")
    private String mPictureBase64;

    @com.google.gson.annotations.Expose(serialize = false)
    private Account mAccount;

    public SaleItem() {
        mAccount = null;
        mName = "";
        mId = "";
        mUserId = "";
        mDescription = "";
        mPictureBase64 = "";
        mPrice = 0;
    }

    @Override
    public String toString() {
        return getId();
    }

    // Getters and Setters
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }
    public String getUserId() { return mUserId; }
    public final void setUserId(String userId) { mUserId = userId; }
    public String getName() { return mName; }
    public final void setName(String name) { mName = name; }
    public String getDescription() { return mDescription; }
    public final void setDescription(String description) { mDescription = description; }
    public float getPrice() { return mPrice; }
    public final void setPrice(float price) { mPrice = price; }
    public Bitmap getPicture() {
        return FblaPicture.DecodeFromBase64(mPictureBase64);
    }
    public final void setPicture(Bitmap image) {
        mPictureBase64 = FblaPicture.EncodeToBase64(image);
    }
    public Account getAccount() { return mAccount; }
    public final void setAccount(Account account) { mAccount = account; }

    @Override
    public boolean equals(Object o) {
        return o instanceof SaleItem && ((SaleItem)o).mId == mId;
    }
}
