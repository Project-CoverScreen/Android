package com.example.radiolucas.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.radiolucas.storage.SaveManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The Resize class provides functionality to resize images.
 */
public class Resize {
    private final Context context;
    public SaveManager saveManager;

    /**
     * Constructs a new Resize instance.
     *
     * @param context the context of the application
     */
    public Resize(Context context) {
        this.context = context;
    }

    /**
     * Resizes an image from the input file and saves it to the output file.
     *
     * @param in  the path to the input image file
     * @param out the path to the output image file
     */
    public void Image(String in, String out) {
        try {
            Bitmap originalImage = BitmapFactory.decodeFile(in);
            Bitmap resizedImage = resizeImage(originalImage, 240, 240);
            FileOutputStream outStream = new FileOutputStream(new File(out));
            resizedImage.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
            Log.v("Resize", "Image redimensionnée. ");
        } catch (IOException e) {
            Log.e("Resize", "Image non redimensionnée. " + e.getMessage());
        }
    }

    /**
     * Resizes the given bitmap to the specified width and height.
     *
     * @param originalImage the original bitmap to resize
     * @param width         the target width
     * @param height        the target height
     * @return the resized bitmap
     */
    private static Bitmap resizeImage(Bitmap originalImage, int width, int height) {
        Bitmap resizedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(resizedImage);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, null, new android.graphics.Rect(0, 0, width, height), paint);
        return resizedImage;
    }
}