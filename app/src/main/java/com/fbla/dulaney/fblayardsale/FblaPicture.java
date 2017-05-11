/* FblaPicture.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This class contains a bunch of helper methods that make it easy to
   manage pictures.

   It will automatically resize large pictures so they assume the scale of
   your phone. This saves space when storing those pictures in the database.

   It also has functions that convert Bitmaps to and from an encoded string.
   The encoded string is saved in an Azure database table.
*/
package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class FblaPicture {
    final private static String mStorageConnection = "DefaultEndpointsProtocol=https;AccountName=fbla;AccountKey=TjlylN1KDieodg23eAAgq0bV6rvLpxUM3PAAGGkjWp5Jf8XshGhr87agsbWMrYyEwgMTQ4MhoeK7L4kxdv9Agg==;EndpointSuffix=core.windows.net";
    private static LinearLayout mLayoutImage;
    private static CloudBlobContainer mContainer = null;
    final private static Object containerLock = new Object();

    public static void setLayoutImage(LinearLayout layout)
    {
        mLayoutImage = layout;
    }
    public static int getImageHeight()
    {
        if (mLayoutImage.getHeight() > 0)
            return mLayoutImage.getHeight() / 2;
        else return 0;
    }

    // Returns dimensions of phone in pixels
    public static Point GetSize(Context c)
    {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            display.getSize(size);
        }
        else // Old Version
        {
            size.set(display.getWidth(), display.getHeight());
        }
        return size;
    }

    // Loads a bitmap picture onto the ImageView item on the layout.
    public static void LoadPictureOnView(ImageView view, Bitmap original) {
        int vh = getImageHeight();
        view.setMinimumHeight(vh);
        view.setMaxHeight(vh);
        view.setImageBitmap(original);
    }

    public static Bitmap GetPictureFromView(ImageView view) {
        Drawable d = view.getDrawable();
        if (d == null) return null;
        return ((BitmapDrawable)d).getBitmap();
    }

    // Resizes a picture selected from the gallery or taken by the camera so they are a common size.
    public static Bitmap ResizePicture(Context c, Bitmap original) {
        int w = original.getWidth();
        int h = original.getHeight();
        Point screen = GetSize(c);
        // Force everything to be 500 pixels long
        int screenL = 500;
        int originL = (w > h) ? w : h;
        int originS = (w > h) ? h : w;

        int newS = (int)((float)screenL * ((float)originS / (float)originL));
        if (w > h)
        {
            Log.d("Picture:ResizePicture", "Screen " + screen.x + "x" + screen.y + " From " + w + "x" + h + " to " + screenL + "x" + newS);
            return Bitmap.createScaledBitmap(original, screenL, newS, true);
        }
        else
        {
            Log.d("Picture:ResizePicture", "Screen " + screen.x + "x" + screen.y + " From " + w + "x" + h + " to " + newS + "x" + screenL);
            return Bitmap.createScaledBitmap(original, newS, screenL, true);
        }
    }

    // Uploads a picture to Azure storage. This must be run in the background.
    public static void UploadImage(String itemId, Bitmap image) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(b);
        synchronized (containerLock) {
            if (mContainer == null) {
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(mStorageConnection);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                mContainer = blobClient.getContainerReference("yardsale");
            }
            CloudBlockBlob imageBlob = mContainer.getBlockBlobReference(itemId);
            imageBlob.upload(bs, b.length);
            Log.d("UploadImage", "Uploaded " + itemId);
        }
    }

    // Downloads a picture from Azure storage. This must be run in the background.
    // If the image does not exist, it will return a null.
    public static Bitmap DownloadImage(String itemId) {
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            synchronized (containerLock) {
                if (mContainer == null) {
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(mStorageConnection);
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                    mContainer = blobClient.getContainerReference("yardsale");
                }
                CloudBlockBlob blob = mContainer.getBlockBlobReference(itemId);
                if (blob.exists()) {
                    blob.download(bs);
                } else {
                    return null;
                }
            }
            // Convert to bitmap
            byte[] b = bs.toByteArray();
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } catch (Exception ex) {
            Log.d("FblaPicture:Download", itemId + " - " + ex.toString());
        }
        return null;
    }

    public static void DeleteImage(String itemId) {
        try {
            synchronized (containerLock) {
                if (mContainer == null) {
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(mStorageConnection);
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                    mContainer = blobClient.getContainerReference("yardsale");
                }
                CloudBlockBlob blob = mContainer.getBlockBlobReference(itemId);
                if (blob.exists()) {
                    blob.delete();
                }
            }
        } catch (Exception ex) {
            Log.d("FblaPicture:Delete", itemId + " - " + ex.toString());
        }
    }
}
