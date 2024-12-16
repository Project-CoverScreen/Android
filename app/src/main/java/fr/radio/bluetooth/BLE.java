package fr.radio.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class BLE {

    private static final String TAG = "BLEManager";

    // UUIDs pour le service et la caractéristique (remplacez-les par ceux de votre ESP32)
    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    public BluetoothGattCharacteristic characteristic;
    private Context context;

    public BLE(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        this.context = context;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void connectToDevice(String deviceAddress) {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter non initialisé.");
            return;
        }

        if (deviceAddress == null) {
            Log.e(TAG, "Adresse MAC invalide ou null.");
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            Log.e(TAG, "Appareil introuvable avec l'adresse : " + deviceAddress);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission BLUETOOTH_CONNECT manquante.");
            return;
        }
       /* BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();

                Log.d(TAG, "Appareil trouvé : " + device.getName() + " - " + device.getAddress());
            }
            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "Échec du scan BLE, code d'erreur : " + errorCode);
            }
        });*/

        Log.e("Scan ok", "ok");
        bluetoothGatt = device.connectGatt(context, true, gattCallback);
        Log.d(TAG, "Tentative de connexion à l'appareil : " + deviceAddress);
        if(bluetoothGatt == null) {
            Log.e(TAG, "Échec de la connexion à l'appareil : " + deviceAddress);
        }
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission BLUETOOTH_CONNECT manquante pour la déconnexion.");
                return;
            }
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            Log.d(TAG, "Déconnexion réussie.");
        } else {
            Log.e(TAG, "BluetoothGatt non initialisé, impossible de déconnecter.");
        }
    }

    public void sendData(byte[] data) {
        if (characteristic == null) {
            Log.e(TAG, "Caractéristique non initialisée. Vérifiez que la découverte des services a été réussie.");
            return;
        }

        if (bluetoothGatt == null) {
            Log.e(TAG, "GATT non initialisé. Vérifiez la connexion au périphérique.");
            return;
        }

        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0 &&
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
            Log.e(TAG, "La caractéristique ne supporte pas l'écriture.");
            return;
        }
        int chunkSize = 512; // BLE default MTU size for data payload
        int offset = 0;
        Log.e("Sending this amount of chunks", String.valueOf(data.length / chunkSize));
        while (offset < data.length) {
            // Determine the end index for this chunk
            int end = Math.min(offset + chunkSize, data.length);
            byte[] chunk = new byte[end - offset];

            // Copy data to the chunk
            System.arraycopy(data, offset, chunk, 0, chunk.length);
            for (int i = 0; i < chunk.length / 2; i++) {
                byte temp = chunk[i];
                chunk[i] = chunk[chunk.length - 1 - i];
                chunk[chunk.length - 1 - i] = temp;
            }
            // Set the chunk value and write it to the characteristic
            characteristic.setValue(chunk);
            boolean success = bluetoothGatt.writeCharacteristic(characteristic);

            if (success) {
                Log.d(TAG, "Chunk envoyé avec succès : " + chunk.length + " bytes.");
            } else {
                Log.e(TAG, "Échec de l'envoi du chunk.");
                break; // Stop sending further chunks if one fails
            }

            // Move to the next chunk
            offset += chunkSize;

            // Add a small delay to avoid flooding the BLE connection
           try {
                Thread.sleep(15); // Adjust delay as necessary
            } catch (InterruptedException e) {
                Log.e(TAG, "Interruption lors de l'envoi des données.", e);
            }
        }

        Log.d(TAG, "Envoi des données terminé.");
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("TEST!", "onConnectionStateChange: " + status + " -> " + newState);
            if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connecté à l'appareil BLE.");

                if(!gatt.discoverServices())
                {
                    Log.e(TAG, "Échec de la découverte des services.");
                }
                Log.d(TAG, "Caractéristique trouvée et prête.");

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission BLUETOOTH_CONNECT manquante lors de la connexion.");
                    return;
                }

            } else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Déconnecté de l'appareil BLE.");
            } else {
                Log.e(TAG, "Changement d'état de connexion inattendu : " + newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e(TAG, "Services découverts.");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services découverts avec succès.");

                BluetoothGattService service = bluetoothGatt.getService(SERVICE_UUID);
                if (service != null) {
                    Log.d(TAG, "Service trouvé.");

                    characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                    bluetoothGatt.requestMtu(512);
                    if (characteristic != null) {
                        Log.d(TAG, "Caractéristique trouvée et prête.");
                    } else {
                        Log.e(TAG, "Caractéristique introuvable. Vérifiez le UUID.");
                    }
                } else {
                    Log.e(TAG, "Service introuvable. Vérifiez le UUID.");
                }
            } else {
                Log.e(TAG, "Échec de la découverte des services, statut : " + status);
            }
        }
    };
}
