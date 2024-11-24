package com.example.radiolucas.cover;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CoverSaveManager {
    private static final String TAG = "CoverSaveManager";
    private static final String BASE_DIR = "Cover";
    private static final String NATIVE_SUBDIR = "native";
    private static final String RESIZE_SUBDIR = "resize";

    private final Context context;

    public CoverSaveManager(Context context) {
        this.context = context;
    }

    // Créer l'arborescence complète des dossiers
    public void createCoverDirectories() {
        File baseDir = getBaseDirectory();
        File nativeDir = new File(baseDir, NATIVE_SUBDIR);
        File resizeDir = new File(baseDir, RESIZE_SUBDIR);

        createDirectory(baseDir);
        createDirectory(nativeDir);
        createDirectory(resizeDir);
    }

    // Méthode générique pour obtenir le dossier de base
    private File getBaseDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BASE_DIR);
    }

    // Créer un répertoire
    private boolean createDirectory(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                Log.d(TAG, "Répertoire créé : " + directory.getAbsolutePath());
                return true;
            } else {
                Log.e(TAG, "Échec de création du répertoire : " + directory.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    // MEC TON CODE CERST PAS POSSIBLE C PAS PSSIBLE C PAS POSSIBLE
    // Sauvegarder un fichier avec des options flexibles
    public File saveFile(byte[] data, String prefix, String extension, StorageLocation location) {
        try {
            // Sélectionner le bon dossier
            File targetDir = getTargetDirectory(location);

            // Générer un nom de fichier unique
            String fileName = prefix + extension;
            File targetFile = new File(targetDir, fileName);

            // Écrire les données
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(data);
                fos.close();
                Log.d(TAG, "Fichier sauvegardé : " + targetFile.getAbsolutePath());
                return targetFile;
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la sauvegarde du fichier", e);
            return null;
        }
    }

    // Obtenir le répertoire cible en fonction de la localisation
    private File getTargetDirectory(StorageLocation location) {
        File baseDir = getBaseDirectory();

        switch (location) {
            case NATIVE:
                return new File(baseDir, NATIVE_SUBDIR);
            case RESIZE:
                return new File(baseDir, RESIZE_SUBDIR);
            default:
                return baseDir;
        }
    }

    // Sauvegarder un fichier avec des paramètres par défaut
    public File saveFile(byte[] data) {
        return saveFile(data, "cover", ".jpeg", StorageLocation.BASE);
    }

    // Méthode utilitaire pour copier un fichier existant
    public File copyFile(File sourceFile, String prefix, String extension, StorageLocation location) {
        try {
            // Lire les données du fichier source
            byte[] fileData = java.nio.file.Files.readAllBytes(sourceFile.toPath());

            // Sauvegarder avec les nouveaux paramètres
            return saveFile(fileData, prefix, extension, location);
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la copie du fichier", e);
            return null;
        }
    }

    // Supprimer un fichier
    public boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    // Énumération des types de sous-dossiers
    public enum StorageLocation {
        BASE, NATIVE, RESIZE
    }
}