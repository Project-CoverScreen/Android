package com.example.radiolucas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Bin {

    private static final String TAG = "Bin";

    public void sendImage(String in, String out) {
        try {
            Bitmap image = BitmapFactory.decodeFile(in);
            if (image == null) {
                Log.e("Bin", "Failed to decode image file: " + in);
                return;
            }

            ByteBuffer buffer = ByteBuffer.allocate((16 + (240 * 240 * 3) + 4 + 16));
            CRC32 crc = new CRC32();

            buffer.put("PetitHeaderLoveU".getBytes());
            for (int y = 0; y < 240; y++) {
                for (int x = 0; x < 240; x++) {
                    int pixel = image.getPixel(x, y);
                    buffer.put((byte) Color.red(pixel));
                    buffer.put((byte) Color.green(pixel));
                    buffer.put((byte) Color.blue(pixel));
                    crc.update(Color.red(pixel) + Color.green(pixel) + Color.blue(pixel));
                }
            }

            int crcValue = (int) crc.getValue();
            buffer.put((byte) ((crcValue >> 24) & 0xFF));
            buffer.put((byte) ((crcValue >> 16) & 0xFF));
            buffer.put((byte) ((crcValue >> 8) & 0xFF));
            buffer.put((byte) (crcValue & 0xFF));

            buffer.put("PetitFootercFini".getBytes());
            Log.d(TAG, "CRC : " + crcValue);
            FileOutputStream fos = new FileOutputStream(out, false);
            fos.write(buffer.array());
            fos.close();

        } catch (IOException e) {
            Log.e("Bin", "Error processing image", e);
        }
    }
}