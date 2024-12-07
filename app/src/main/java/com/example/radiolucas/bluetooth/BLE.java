package com.example.radiolucas.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BLE implements IBluetoothConnexion {

    private static final String TAG = "BLE";
    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9f");
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CHUNK_SIZE = 20;

    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private BluetoothGatt bluetoothGatt;
    private String targetDeviceAddress;
    private boolean isReconnecting = false;

    public BLE(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "Connecté au serveur GATT.");
                    gatt.discoverServices();
                    onConnected();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d(TAG, "Déconnecté du serveur GATT");
                    if (!isReconnecting && targetDeviceAddress != null) {
                        reconnect();
                    }
                    onDisconnected();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service != null) {
                    Log.d(TAG, "Service BLE trouvé");
                } else {
                    onError("Service BLE non trouvé");
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onDataSent();
            } else {
                onError("Erreur d'envoi des données: " + status);
            }
        }
    };

    @Override
    public boolean connect(String deviceAddress) {
        if (deviceAddress == null || deviceAddress.isEmpty()) {
            onError("Adresse du périphérique invalide");
            return false;
        }

        if (bluetoothGatt != null && isConnected()) {
            if (deviceAddress.equals(targetDeviceAddress)) {
                return true;
            }
            disconnect();
        }

        targetDeviceAddress = deviceAddress;
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        return true;
    }

    private void reconnect() {
        isReconnecting = true;
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        connect(targetDeviceAddress);
        isReconnecting = false;
    }

    @Override
    public boolean isConnected() {
        return bluetoothGatt != null && bluetoothGatt.getDevice() != null;
    }

    @Override
    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        targetDeviceAddress = null;
    }

    @Override
    public boolean sendData(byte[] data) {
        if (!isConnected() || data == null) return false;

        BluetoothGattService service = bluetoothGatt.getService(SERVICE_UUID);
        if (service == null) return false;

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic == null) return false;

        int offset = 0;
        while (offset < data.length) {
            int length = Math.min(CHUNK_SIZE, data.length - offset);
            byte[] chunk = Arrays.copyOfRange(data, offset, offset + length);

            characteristic.setValue(chunk);
            bluetoothGatt.writeCharacteristic(characteristic);

            offset += length;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.e(TAG, "Erreur lors de l'envoi des données", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPermission(Context context) {
        if (!(context instanceof AppCompatActivity)) {
            Log.e(TAG, "Context n'est pas une Activity");
            return false;
        }

        String[] permissions = {
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions((AppCompatActivity) context, permissions, PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onConnected() {
        Log.d(TAG + "onConnected", "Connexion établie");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG + "onDisconnected", "Connexion terminée");
    }

    @Override
    public void onError(String message) {
        Log.e(TAG + "onError", "Erreur: " + message);
    }

    @Override
    public void onDataSent() {
        Log.d(TAG + "onDataSent", "Données envoyées avec succès");
    }


}