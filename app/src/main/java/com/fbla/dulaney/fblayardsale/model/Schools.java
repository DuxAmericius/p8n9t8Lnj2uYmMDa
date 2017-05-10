/* Schools.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for school information.
   This class is used by the Azure library to query and create data in the
   Schools database table.

   In order to make it easy for users to select their school, all public and
   private schools in the USA and its territories were downloaded from the
   National Center for Education Statistics and loaded into the Schools database.
   https://nces.ed.gov/ccd/pubschuniv.asp
   https://nces.ed.gov/surveys/pss/pssdata.asp

*/
package com.fbla.dulaney.fblayardsale.model;

import android.support.annotation.NonNull;

public class Schools implements Comparable<Schools> {
    // Database Columns
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique ID of the school, assigned by the National Center for Education Statistics
    @com.google.gson.annotations.SerializedName("zip")
    private String mZip; // Zip code of the school
    @com.google.gson.annotations.SerializedName("school")
    private String mSchool; // Name of the school
    @com.google.gson.annotations.SerializedName("address")
    private String mAddress; // Address of the school
    @com.google.gson.annotations.SerializedName("city")
    private String mCity; // City of the school
    @com.google.gson.annotations.SerializedName("stateText")
    private String mStateText; // State or Territory (full name, not abbreviated)
    @com.google.gson.annotations.SerializedName("lat")
    private double mLat; // Latitude
    @com.google.gson.annotations.SerializedName("long")
    private double mLong; // Longitude

    public Schools() {
        mId = "";
        mZip = "";
        mSchool = "";
        mAddress = "";
        mCity = "";
        mStateText = "";
        mLat = 0;
        mLong = 0;
    }

    @Override
    public String toString() {
        return getSchool();
    }

    //Getters and Setters
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }
    public String getZip() { return mZip; }
    public final void setZip(String zip) { mZip = zip; }
    public String getSchool() { return mSchool; }
    public final void setSchool(String school) { mSchool = school; }
    public String getAddress() { return mAddress; }
    public final void setAddress(String address) { mAddress = address; }
    public String getCity() { return mCity; }
    public final void setCity(String city) { mCity = city; }
    public String getStateText() { return mStateText; }
    public final void setStateText(String stateText) { mStateText = stateText; }
    public double getLat() { return mLat; }
    public final void setLat(double lat) { mLat = lat; }
    public double getLong() { return mLong; }
    public final void setLong(double lng) { mLong = lng; }

    // Separate full address for displaying with an item.
    public String getFullAddress() {
        return mAddress + ", " + mCity + ", " + mStateText;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Schools && ((Schools)o).mId == mId;
    }

    // Implements Comparable so we can sort them during a city/state search.
    @Override
    public int compareTo(@NonNull Schools o) {
        return this.getSchool().compareTo(o.getSchool());
    }
}
