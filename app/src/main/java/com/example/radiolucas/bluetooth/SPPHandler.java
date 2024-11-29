package com.example.radiolucas.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SPPHandler {
    private static final String TAG = "SPPSender";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private static final int LINE_COUNT = 8;


    private String deviceAddress = "C0:49:EF:2E:79:1A"; // Replace with your ESP32's Bluetooth MAC address
    public boolean connectToDevice(Context ctx) {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                Log.e("BT", "n1o");

                return false;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Log.e("BT", "n2o");

                return false;
            }

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("OKK", "mec les perms stp /op fraigneau");

                    ActivityCompat.requestPermissions((Activity) ctx,
                            new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN},
                            1);
                    return false;
                }
            }

            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e("OKK", "mec les perms stp /op fraigneau");

                return false;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);

            // Connect to the device
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            Log.e("OKK", "ggmec");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to device", e);
            return false;
        }
    }
    public boolean sendMessage(byte[] message) {
        try {
            if (bluetoothSocket == null) {
                Log.e("BT", "no");

                return false;
            }
            if(!bluetoothSocket.isConnected())
            {
                Log.e("AHAH", "noooo");
                return false;
            }
            //outputStream.write(message);

            outputStream.write(message, 0, 0x10);
            int off = 0x10;
            for (int x =0; x < 240; x += LINE_COUNT ) {
                byte[] coucou = new byte[1];
                inputStream.read(coucou);
                if (coucou[0] != 0x3A) {
                    Log.e("BT", "no");
                }
                Log.e("BT", "OK");
                outputStream.write(message, off, 240 * 3 * LINE_COUNT);
                off +=240 * 3 * LINE_COUNT;

                inputStream.read(coucou);
                if (coucou[0] != 0x3D) {
                    Log.e("BT", "pas bien enfaiteeeeeeeeuhhhh fdp ");
                }
            }

            outputStream.write(message, off, 0x10);




            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            return false;
        }
    }
}
