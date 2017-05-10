/* ItemComment.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for item comment information.
   This class is used by the Azure library to query and create data in the
   ItemComment database table.

   The link to the Account table is also represented by holding a copy of
   the whole Account object.
*/
package com.fbla.dulaney.fblayardsale.model;

public class ItemComment {
    // Database Columns
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique id assigned by the database.
    @com.google.gson.annotations.SerializedName("userid")
    private String mUserId; // Foreign key to the Account
    @com.google.gson.annotations.SerializedName("itemid")
    private String mItemId; // Foreign key to the SaleItem
    @com.google.gson.annotations.SerializedName("comment")
    private String mComment; // This is the actual comment text

    // Transient fields will not get queried or saved to the database
    @com.google.gson.annotations.Expose(serialize = false)
    private transient Account mAccount; // Needed to display the username

    public ItemComment() {
        mAccount = null;
        mId = "";
        mUserId = "";
        mItemId = "";
        mComment = "";
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
    public String getItemId() { return mItemId; }
    public final void setItemId(String itemId) { mItemId = itemId; }
    public String getComment() { return mComment; }
    public final void setComment(String comment) { mComment = comment; }
    public Account getAccount() { return mAccount; }
    public final void setAccount(Account account) { mAccount = account; }

    @Override
    public boolean equals(Object o) {
        return o instanceof ItemComment && ((ItemComment)o).mId == mId;
    }
}
