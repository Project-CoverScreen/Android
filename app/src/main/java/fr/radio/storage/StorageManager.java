package fr.radio.storage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.File;


public class StorageManager {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG 
            = "StorageManager";
    private final Activity activity;
    private static final String RADIO_FOLDER 
            = "/storage/emulated/0/Radio";
    public static final String READ_MEDIA_IMAGES
            = "android. permission. READ_MEDIA_IMAGES";

    public StorageManager(Activity activity) { //  /storage/emulated/0/Radio
        this.activity = activity;

        File storageRoot = activity.getExternalFilesDir(null);
        File Radio = new File(storageRoot, RADIO_FOLDER);

        if (!Radio.exists()) {
            Log.e(TAG, "StorageManager: Radio n'existe pas encore -> " + Radio.getAbsolutePath());
            Radio.mkdirs();
        }
        Log.e(TAG, "StorageManager: " + Radio.getAbsolutePath());
        Radio.mkdirs();

    }

    public void testmkdirs() {
        
    }

    public boolean hasStoragePermissions() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermissions() {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                },
                PERMISSION_REQUEST_CODE
        );
    }

    public static boolean checkFileExists(String coverPath) {
        File file = new File(coverPath);
        return file.exists();
    }
}