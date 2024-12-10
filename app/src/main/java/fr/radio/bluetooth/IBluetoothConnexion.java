package fr.radio.bluetooth;

import android.content.Context;

public interface IBluetoothConnexion {

    boolean connect(String deviceAddress);
    boolean isConnected();
    boolean isPermission(Context context);
    boolean sendData(byte[] data);
    void disconnect();

    //Callbacks
    void onConnected();
    void onDisconnected();
    void onError(String message);
    void onDataSent();
}
