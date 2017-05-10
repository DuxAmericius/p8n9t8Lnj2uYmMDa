/* SchoolDistance.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: Model of the Azure database that holds distances between schools.
   This class is used by the Azure library to query and create data in the
   SchoolDistance database table.

   The SchoolDistance table has been preloaded with the distance between every
   school within 10 miles of each other. The following SQL was used to do this,
   executed for each state/territory. When we tried to run everything at once,
   we decided to cancel after 5 hours and run it in chucks, per state. It took
   anywhere from 30 minutes to 6 minutes for each state/territory.

   The distance is calculated using the Haversine formula, with the SQL itself
   developed by Dayne Batten.
   http://daynebatten.com/2015/09/latitude-longitude-distance-sql/

   INSERT INTO SchoolDistance (fromid, toid, miles)
   SELECT f.id, t.id
        , 2 * 3961 * asin(sqrt(
   	       square(sin(radians((t.lat - f.lat) / 2))) +
	       cos(radians(f.lat)) * cos(radians(t.lat)) *
	       square(sin(radians((t.long - f.long) / 2)))
	      )) miles
   FROM Schools f
   INNER JOIN Schools t ON (t.id <> f.id AND t.lat <> 0 AND t.long <> 0)
   WHERE 2 * 3961 * asin(sqrt(
	       square(sin(radians((t.lat - f.lat) / 2))) +
	       cos(radians(f.lat)) * cos(radians(t.lat)) *
	       square(sin(radians((t.long - f.long) / 2)))
	      )) <= 10
   AND f.stateText = @state;

*/
package com.fbla.dulaney.fblayardsale.model;

public class SchoolDistance {
    @com.google.gson.annotations.SerializedName("id")
    private String mId; // Unique id assigned by the database. We don't use this.
    @com.google.gson.annotations.SerializedName("fromid")
    private String mFromId; // Foreign key to the user's school.
    @com.google.gson.annotations.SerializedName("toid")
    private String mToId; // Foreign key to the school that's nearby.
    @com.google.gson.annotations.SerializedName("miles")
    private float mMilesId; // Distance in miles between the schools.

    @Override
    public String toString() {
        return mId;
    }

    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }
    public String getFromId() { return mFromId; }
    public final void setFromId(String id) { mFromId = id; }
    public String getToId() { return mToId; }
    public final void setToId(String id) { mToId = id; }
    public float getMiles() { return mMilesId; }
    public final void setMiles(float miles) { mMilesId = miles; }

    @Override
    public boolean equals(Object o) {
        return o instanceof SchoolDistance && ((SchoolDistance)o).mId == mId;
    }
}
