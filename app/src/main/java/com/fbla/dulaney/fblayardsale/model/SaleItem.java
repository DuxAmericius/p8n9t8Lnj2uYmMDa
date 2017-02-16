package com.fbla.dulaney.fblayardsale.model;

/**
 * Created by josh on 2/12/2017.
 */

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

    public SaleItem() {

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

    @Override
    public boolean equals(Object o) {
        return o instanceof SaleItem && ((SaleItem)o).mId == mId;
    }
}
