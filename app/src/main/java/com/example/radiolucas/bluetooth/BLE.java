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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BLE implements IBluetoothConnexion{

    private static final String TAG = "BLE";

    private static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9f");
    public static final int PERMISSION_REQUEST_CODE = 1;

    private BluetoothLeScanner scanner;
    private ScanCallback scanCallback;

    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private final Context context;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "Connected to GATT server.");
                onConnected();
            }
        }
    };


    public BLE(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        isPermission(this.context);
    }

    public void scanDevice() {
        scanner = bluetoothAdapter.getBluetoothLeScanner();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                scanner.stopScan(scanCallback);  // Utilisation de la référence
                connect(device);
            }
        };
        scanner.startScan(scanCallback);
    }

    private void connect(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    Log.e(TAG, "Connected to GATT server.");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(CHARACTERISTIC_UUID);
            }
        });
    }

    public boolean sendData(byte[] data) {
        if (bluetoothGatt == null) return false;

        BluetoothGattService service = bluetoothGatt.getService(SERVICE_UUID);
        if (service == null) return false;

        BluetoothGattCharacteristic characteristic =
                service.getCharacteristic(CHARACTERISTIC_UUID);
        if (characteristic == null) return false;

        int offset = 0;
        while (offset < data.length) {
            int length = Math.min(20, data.length - offset);
            byte[] chunk = Arrays.copyOfRange(data, offset, offset + length);

            int writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
            bluetoothGatt.writeCharacteristic(characteristic, chunk, writeType);

            offset += length;

            try {
                Thread.sleep(50); // Petit délai pour éviter la congestion
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "Data sent successfully.");
        return true;
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Log.e(TAG, "Disconnected from GATT server.");
        }
    }

    @Override
    public boolean connect(String deviceAddress) {
        return false;
    }

    @Override
    public boolean isConnected() {
        return bluetoothGatt != null && bluetoothGatt.getDevice() != null;
    }

    @Override
    public void onConnected() {
        Log.e(TAG, "Connected successfully to device");
        bluetoothGatt.discoverServices();
    }

    @Override
    public void onDisconnected() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        Log.e(TAG, "Disconnected from GATT server.");
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void onDataSent() {
    }

    @Override
    public boolean isPermission(Context context) {
        if (context instanceof AppCompatActivity) {
            String[] permissions = {
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            boolean needRequest = false;
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest = true;
                    break;
                }
            }
            if (needRequest) {
                ActivityCompat.requestPermissions((AppCompatActivity) context, permissions, PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.e(TAG, "Context n'est pas une Activity");
            return false;
        }
        return true;
    }
}
