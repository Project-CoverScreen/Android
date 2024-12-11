package fr.radio.storage;

import android.util.Log;

import fr.radio.MainActivity;

import java.io.File;


public class StorageManager {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final MainActivity activity;

    public StorageManager(MainActivity activity) {
        this.activity = activity;
    }

    public File createAppFolder(String folderName) {
        File folder;
        folder = new File(activity.getExternalFilesDir(null), folderName);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                Log.e("StorageManager", "Erreur lors de la création du dossier");
                return null;
            }
        }
        Log.v("StorageManager", "Dossier créé avec succès : " + folder.getAbsolutePath());
        return folder;
    }

    public boolean checkFileExists(String coverPath) {
        File file = new File(coverPath);
        return file.exists();
    }
}