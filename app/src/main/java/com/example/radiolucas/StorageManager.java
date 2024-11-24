package com.example.radiolucas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StorageManager {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final MainActivity activity;

    public StorageManager(MainActivity activity) {
        this.activity = activity;
    }

    // Créer un dossier pour l'application
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
        Log.e("StorageManager", "Dossier créé avec succès : " + folder.getAbsolutePath());
        return folder;
    }

    // Créer un fichier dans le dossier
    public boolean createFile(File folder, String fileName, String content) {
        try {
            File file = new File(folder, fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}