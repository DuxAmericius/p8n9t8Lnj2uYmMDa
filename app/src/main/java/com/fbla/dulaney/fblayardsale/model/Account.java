/* Account.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for user account information.
   This class is used by the Azure library to query and create data in the
   Account database table.

   The link to the Schools table is also represented by holding a copy of
   the whole Schools object.
*/
package com.fbla.dulaney.fblayardsale.model;

public class Account {
    // Database Columns
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique id for the user, as provided by the Google+ logon
    @com.google.gson.annotations.SerializedName("name")
    private String mName; // Your username
    @com.google.gson.annotations.SerializedName("schoolid")
    private String mSchoolId; // Foreign key to the selected school.

    // Transient fields will not get queried or saved to the database
    @com.google.gson.annotations.Expose(serialize = false)
    private transient Schools mSchool;

    public Account() {
        mId = "";
        mName = "";
        mSchoolId = null;
        mSchool = null;
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
    public String getSchoolId() { return mSchoolId; }

    public Schools getSchool() { return mSchool; }
    public final void setSchool(Schools school) {
        mSchool = school;
        if (school == null) mSchoolId = null;
        else mSchoolId = school.getId();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Account && ((Account)o).mId == mId;
    }
}
