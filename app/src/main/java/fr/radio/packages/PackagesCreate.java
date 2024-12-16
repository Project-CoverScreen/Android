package fr.radio.packages;

import android.content.Context;

import fr.radio.storage.SaveManager;

public class PackagesCreate {

    private static final String TAG = "PackagesCreate";

    private static final int LINE_COUNT = 1;
    private static final int SCREEN_WIDTH = 240;
    private static final int SCREEN_HEIGHT = 240;

    private final Context context;

    public PackagesCreate(Context context) {
        this.context = context;

    }

    public byte[][] imageChunked(byte[] image) {
        byte[][] chunks = new byte[240][480];
        for (int i = 0; i < 240; i ++) { // Pour chaque lignes
            byte[] chunk = new byte[480];
            for (int j = 0; j < 480; j++) { // Pour chaque octes de la ligne
                chunk[j] = image[i* 480 +j];
            }
            PacketDrawImage packet = new PacketDrawImage();
            chunks[i] = packet.serialize(chunk);

            SaveManager saveManager = new SaveManager(context);
            saveManager.saveFile(packet.serialize(chunk),"kek" + i, "kek", SaveManager.StorageLocation.RESIZE );
        }
        return chunks;
    }



}
