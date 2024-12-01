package com.example.radiolucas.storage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.radiolucas.spotify.SpotifyInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The SaveManager class provides functionality to manage saving files to the device.
 */
public class SaveManager {
    private static final String TAG = "CoverSaveManager";
    private static final String BASE_DIR = "Cover";
    private static final String NATIVE_SUBDIR = "native";
    private static final String RESIZE_SUBDIR = "resize";
    private static final String BIN_SUBDIR = "resize";

    private final Context context;

    /**
     * Constructs a new SaveManager instance.
     *
     * @param context the context of the application
     */
    public SaveManager(Context context) {
        this.context = context;
    }

    /**
     * Creates the directory structure for saving cover files.
     */
    public void createCoverDirectories() {
        File baseDir = getBaseDirectory();
        File nativeDir = new File(baseDir, NATIVE_SUBDIR);
        File resizeDir = new File(baseDir, RESIZE_SUBDIR);
        File binDir = new File(baseDir, BIN_SUBDIR);

        createDirectory(baseDir);
        createDirectory(nativeDir);
        createDirectory(resizeDir);
        createDirectory(binDir);
    }

    /**
     * Gets the base directory for saving cover files.
     *
     * @return the base directory file
     */
    private File getBaseDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BASE_DIR);
    }

    /**
     * Creates a directory if it does not already exist.
     *
     * @param directory the directory to create
     * @return true if the directory was created or already exists, false otherwise
     */
    private boolean createDirectory(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                Log.d(TAG, "Directory created: " + directory.getAbsolutePath());
                return true;
            } else {
                Log.e(TAG, "Failed to create directory: " + directory.getAbsolutePath());
                return false;
            }
        }
        return true;
    }

    /**
     * Saves a file with flexible options.
     *
     * @param data      the data to save
     * @param prefix    the prefix for the file name
     * @param extension the file extension
     * @param location  the storage location
     * @return the saved file, or null if an error occurred
     */
    public File saveFile(byte[] data, String prefix, String extension, StorageLocation location) {
        try {
            File targetDir = getTargetDirectory(location);
            String fileName = prefix + extension;
            File targetFile = new File(targetDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(data);
                fos.close();
                Log.d(TAG, "File saved: " + targetFile.getAbsolutePath());
                return targetFile;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving file", e);
            return null;
        }
    }

    public byte [] readFile(String path) {
        try {
            File file = new File(path);
            return java.nio.file.Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
            return null;
        }
    }

    /**
     * Gets the target directory based on the storage location.
     *
     * @param location the storage location
     * @return the target directory file
     */
    private File getTargetDirectory(StorageLocation location) {
        File baseDir = getBaseDirectory();

        switch (location) {
            case NATIVE:
                return new File(baseDir, NATIVE_SUBDIR);
            case RESIZE:
                return new File(baseDir, RESIZE_SUBDIR);
            case BIN:
                return new File(baseDir, BIN_SUBDIR);
            default:
                return baseDir;
        }
    }

    /**
     * Saves a file with default parameters.
     *
     * @param data the data to save
     * @return the saved file, or null if an error occurred
     */
    public File saveFile(byte[] data) {
        return saveFile(data, "cover", ".jpeg", StorageLocation.BASE);
    }

    /**
     * Copies an existing file to a new location with new parameters.
     *
     * @param sourceFile the source file to copy
     * @param prefix     the prefix for the new file name
     * @param extension  the file extension for the new file
     * @param location   the storage location for the new file
     * @return the copied file, or null if an error occurred
     */
    public File copyFile(File sourceFile, String prefix, String extension, StorageLocation location) {
        try {
            byte[] fileData = java.nio.file.Files.readAllBytes(sourceFile.toPath());
            return saveFile(fileData, prefix, extension, location);
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
            return null;
        }
    }

    public boolean checkFileExists(String coverPath) {
        File file = new File(coverPath);
        Log.e(TAG, "File exists: " + file.exists());
        return file.exists();
    }

    /**
     * Deletes a file.
     *
     * @param file the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Enumeration of storage locations.
     */
    public enum StorageLocation {
        BASE, NATIVE, RESIZE, BIN
    }

    public String getCoverPath(StorageLocation location, SpotifyInfo spotifyInfo) {
        switch (location) {
            case NATIVE:
                return "/storage/emulated/0/Download/Cover/native/" + spotifyInfo.coverName + ".jpg";
            case RESIZE:
                return "/storage/emulated/0/Download/Cover/resize/" + spotifyInfo.coverName + ".jpg";
            case BIN:
                return "/storage/emulated/0/Download/Cover/bin/" + spotifyInfo.coverName + ".bin";
            default:
                return null;
        }
    }
}