package fr.radio.storage;

import android.util.Log;

import fr.radio.MainActivity;

import java.io.File;

/**
 * The StorageManager class provides functionality to manage storage operations such as creating folders and files.
 */
public class StorageManager {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final MainActivity activity;

    /**
     * Constructs a new StorageManager instance.
     *
     * @param activity the main activity of the application
     */
    public StorageManager(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * Creates a folder in the external files directory of the application.
     *
     * @param folderName the name of the folder to create
     * @return the created folder, or null if the folder could not be created/
     */
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