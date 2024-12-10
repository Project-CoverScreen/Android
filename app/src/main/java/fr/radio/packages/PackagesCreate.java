package fr.radio.packages;

import android.content.Context;
import android.util.Log;

import fr.radio.storage.SaveManager;
import fr.radio.storage.StorageManager;

public class PackagesCreate {

    private static final String TAG = "PackagesCreate";

    public static int line_number_count = 0;
    public static int chunk_size = 480;

    private final Context context;

    public PackagesCreate(Context context) {
        this.context = context;

    }

    public byte[] imageChunked(byte[] image) {

        byte[] chunk = new byte[chunk_size];
        for (int i = 0; i < 240; i ++) { // Pour chaque lignes
            for (int j = 0; j < chunk_size; j++) { // Pour chaque octes de la ligne
                chunk[j] = image[(line_number_count * chunk_size)+j];
            }
            Log.e(TAG, "imageChunked: " + chunk.length);
            PacketDrawImage packet = new PacketDrawImage();
            //packet.serialize(chunk);
            SaveManager saveManager = new SaveManager(this.context);
            saveManager.saveFile(packet.serialize(chunk), "chunked_image_" + line_number_count , ".bin", SaveManager.StorageLocation.BIN);

            line_number_count = chunk_size / 480;
        }
        Log.e(TAG, "imageChunked: " + chunk.length);
        return chunk;
    }



}
