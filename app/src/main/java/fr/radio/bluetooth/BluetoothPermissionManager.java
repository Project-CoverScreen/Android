package fr.radio.bluetooth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BluetoothPermissionManager {

    private static final String TAG = "BluetoothPermissionManager";

    // Liste des permissions nécessaires pour BLE
    private static final String[] BLE_PERMISSIONS = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    // Pour Android < SDK 31
    private static final String[] BLE_PERMISSIONS_LEGACY = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final AppCompatActivity activity;
    private final PermissionCallback callback;

    // Interface pour signaler les résultats
    public interface PermissionCallback {
        void onPermissionsGranted();

        void onPermissionsDenied();
    }

    public BluetoothPermissionManager(AppCompatActivity activity, PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    // Vérifie si toutes les permissions sont accordées
    public boolean arePermissionsGranted() {
        String[] permissionsToCheck = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? BLE_PERMISSIONS : BLE_PERMISSIONS_LEGACY;

        for (String permission : permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission manquante : " + permission);
                return false;
            }
        }
        return true;
    }

    // Demande les permissions manquantes
    public void requestPermissions() {
        if (arePermissionsGranted()) {
            Log.d(TAG, "Toutes les permissions sont déjà accordées.");
            callback.onPermissionsGranted();
            return;
        }

        // Lance la demande des permissions
        ActivityResultLauncher<String[]> permissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        Log.d(TAG, "Toutes les permissions BLE sont accordées.");
                        callback.onPermissionsGranted();
                    } else {
                        Log.e(TAG, "Certaines permissions BLE ont été refusées.");
                        callback.onPermissionsDenied();
                    }
                });

        // Demande les permissions appropriées selon la version Android
        String[] permissionsToRequest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? BLE_PERMISSIONS : BLE_PERMISSIONS_LEGACY;
        permissionLauncher.launch(permissionsToRequest);
    }
}
