/* ItemComment.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database table for item comment information.
*/
package com.fbla.dulaney.fblayardsale.model;

public class ItemComment {
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
    Item Id
     */
    @com.google.gson.annotations.SerializedName("itemid")
    private String mItemId;

    /*
    Comment
     */
    @com.google.gson.annotations.SerializedName("comment")
    private String mComment;

    @com.google.gson.annotations.Expose(serialize = false)
    private Account mAccount;

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
