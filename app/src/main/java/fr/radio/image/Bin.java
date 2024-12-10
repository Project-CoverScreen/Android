package fr.radio.image;

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
    private static final int TAILLE_IMAGE = 240;
    //private static final int CRC_SIZE = 4;

    private static final int TAILLE_BUFFER = (2 * TAILLE_IMAGE * TAILLE_IMAGE);

    public void sendImage(String entree, String sortie) {
        Bitmap image = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 1;

            image = BitmapFactory.decodeFile(entree, options);
            if (image == null) {
                Log.e(TAG, "Échec du décodage de l'image: " + entree);
                return;
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(TAILLE_BUFFER);
            //CRC32 crc = new CRC32();

            int[] pixels = new int[TAILLE_IMAGE * TAILLE_IMAGE];
            image.getPixels(pixels, 0, TAILLE_IMAGE, 0, 0, TAILLE_IMAGE, TAILLE_IMAGE);

            byte[] rgbBuffer = new byte[2];
            for (int pixel : pixels) {
                int red = (Color.red(pixel) >> 3) & 0x1F;     // 5 bits
                int green = (Color.green(pixel) >> 2) & 0x3F;  // 6 bits
                int blue = (Color.blue(pixel) >> 3) & 0x1F;    // 5 bits

                int rgb565 = (red << 11) | (green << 5) | blue;
                rgbBuffer[0] = (byte) (rgb565 >> 8);
                rgbBuffer[1] = (byte) rgb565;

                buffer.put(rgbBuffer);
                //crc.update(rgbBuffer);
            }

            //long valeurCRC = crc.getValue();
            //buffer.putInt((int)valeurCRC);

            buffer.flip();
            try (FileOutputStream fos = new FileOutputStream(sortie, false)) {
                fos.getChannel().write(buffer);
                fos.flush();
            }

        } catch (IOException e) {
            Log.e(TAG, "Erreur lors du traitement de l'image", e);
        } finally {
            if (image != null) {
                image.recycle();
            }
        }
    }
}