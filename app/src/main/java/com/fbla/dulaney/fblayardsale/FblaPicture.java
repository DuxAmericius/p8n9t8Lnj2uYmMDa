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

import java.io.ByteArrayOutputStream;

/**
 * Created by josh on 2/15/2017.
 */

public class FblaPicture {
    private static LinearLayout mLayoutImage;

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

    public static String EncodeToBase64(Bitmap image) {
        if (image == null) return "";
        // http://stackoverflow.com/questions/9768611/encode-and-decode-bitmap-object-in-base64-string-in-android
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap DecodeFromBase64(String image) {
        if (image == null || image.equals("")) return null;
        byte[] decodedImage = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }
}
