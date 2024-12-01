package com.example.radiolucas.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Resize {
    private static final String TAG = "Resize";
    private static final int TARGET_SIZE = 240;
    private final Context context;

    public Resize(Context context) {
        this.context = context;
    }

    public void Image(String in, String out) {
        try {
            // Configure BitmapFactory options for memory efficiency
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(in, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, TARGET_SIZE, TARGET_SIZE);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inMutable = true;

            Bitmap originalImage = BitmapFactory.decodeFile(in, options);
            if (originalImage == null) {
                Log.e(TAG, "Failed to decode original image");
                return;
            }

            // Use Matrix for resizing
            Bitmap resizedImage = resizeImageWithMatrix(originalImage, TARGET_SIZE, TARGET_SIZE);
            originalImage.recycle(); // Release the original bitmap

            // Save with optimized settings
            File outFile = new File(out);
            try (FileOutputStream outStream = new FileOutputStream(outFile)) {
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                outStream.flush();
            }

            resizedImage.recycle(); // Release the resized bitmap
            Log.v(TAG, "Image resized successfully");

        } catch (IOException e) {
            Log.e(TAG, "Error resizing image: " + e.getMessage());
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap resizeImageWithMatrix(Bitmap originalImage, int width, int height) {
        float scaleWidth = ((float) width) / originalImage.getWidth();
        float scaleHeight = ((float) height) / originalImage.getHeight();
        float scale = Math.min(scaleWidth, scaleHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(originalImage, 0, 0,
                originalImage.getWidth(), originalImage.getHeight(),
                matrix, true);
    }
}