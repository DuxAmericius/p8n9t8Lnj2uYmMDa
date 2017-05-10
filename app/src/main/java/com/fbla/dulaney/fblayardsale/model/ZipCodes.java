/* ZipCodes.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for all US zip codes.
   This class is used by the Azure library to query and create data in the
   ZipCodes database table.

   The ZipCodes database table is used to find schools. We can easily get a list
   of each school in a zip code, because the Schools table has the zip code.
   However, not everybody remembers the zip code for their school. The ZipCodes
   table was populated with the free ZipCode database containing all locations
   from this web site: http://federalgovernmentzipcodes.us

   The user selects a state, and types in the beginning of whatever city they
   want, and we can get a list of all zip codes that match.
*/
package com.fbla.dulaney.fblayardsale.model;

public class ZipCodes {
    // Database Columns
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique id assigned by the database
    @com.google.gson.annotations.SerializedName("zip")
    private String mZip; // Zip code
    @com.google.gson.annotations.SerializedName("zipType")
    private String mZipType; // Type of zip code (PO BOX, STANDARD, UNIQUE)
    @com.google.gson.annotations.SerializedName("city")
    private String mCity; // City
    @com.google.gson.annotations.SerializedName("state")
    private String mState; // State, abbreviation
    @com.google.gson.annotations.SerializedName("locationType")
    private String mLocationType; // Location Type (PRIMARY, ACCEPTABLE, NOT ACCEPTABLE)
    @com.google.gson.annotations.SerializedName("locationText")
    private String mLocationText; // Camel Case version of city and state
    @com.google.gson.annotations.SerializedName("lat")
    private double mLat; // Latitude
    @com.google.gson.annotations.SerializedName("long")
    private double mLong; // Longitude
    @com.google.gson.annotations.SerializedName("stateText")
    private String mStateText; // State, fully spelled out

    public ZipCodes() {
        mId = "";
        mZip = "";
        mZipType = "";
        mCity = "";
        mState = "";
        mLocationType = "";
        mLocationText = "";
        mLat = 0;
        mLong = 0;
        mStateText = "";
    }

    @Override
    public String toString() {
        return getZip();
    }

    //Getters and Setters
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }
    public String getZip() { return mZip; }
    public final void setZip(String zip) { mZip = zip; }
    public String getZipType() { return mZipType; }
    public final void setZipType(String zipType) { mZipType = zipType; }
    public String getCity() { return mCity; }
    public final void setCity(String city) { mCity = city; }
    public String getState() { return mState; }
    public final void setState(String state) { mState = state; }
    public String getLocationType() { return mLocationType; }
    public final void setLocationType(String locationType) { mLocationType = locationType; }
    public String getLocationText() { return mLocationText; }
    public final void setLocationText(String locationText) { mLocationText = locationText; }
    public double getLat() { return mLat; }
    public final void setLat(double lat) { mLat = lat; }
    public double getLong() { return mLong; }
    public final void setLong(double lng) { mLong = lng; }
    public String getStateText() { return mStateText; }
    public final void setStateText(String stateText) { mStateText = stateText; }

    @Override
    public boolean equals(Object o) {
        return o instanceof ZipCodes && ((ZipCodes)o).mId == mId;
    }
}
