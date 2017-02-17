package com.fbla.dulaney.fblayardsale.model;

/**
 * Created by josh on 2/14/2017.

 */

public class Account {
    /*
    User Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /*
    User Name
     */
    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    /*
    State
     */
    @com.google.gson.annotations.SerializedName("state")
    private String mState;

    /*
    Region
     */
    @com.google.gson.annotations.SerializedName("region")
    private String mRegion;

    /*
    FBLA Chapter
     */
    @com.google.gson.annotations.SerializedName("chapter")
    private String mChapter;

    /*
    Address
     */
    @com.google.gson.annotations.SerializedName("address")
    private String mAddress;

    /*
    Zip Code
     */
    @com.google.gson.annotations.SerializedName("zipcode")
    private String mZipCode;

    public Account() {
        mId = "";
        mName = "";
        mAddress = "";
        mChapter = "";
        mRegion = "";
        mState = "";
        mZipCode = "";
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
    public String getState() { return mState; }
    public final void setState(String state) { mState = state; }
    public String getRegion() { return mRegion; }
    public final void setRegion(String region) { mRegion = region; }
    public String getChapter() { return mChapter; }
    public final void setChapter(String chapter) { mChapter = chapter; }
    public String getAddress() { return mAddress; }
    public final void setAddress(String address) { mAddress = address; }
    public String getZipCode() { return mZipCode; }
    public final void setZipCode(String zipCode) { mZipCode = zipCode; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Account && ((Account)o).mId == mId;
    }
}
