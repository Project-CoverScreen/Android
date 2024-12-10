package fr.radio.packages;

import android.content.Context;
import android.util.Log;
import fr.radio.bluetooth.BLE;

public class PacketSender {
    private static final String TAG = "PacketSender";
    private final BLE bleConnection;

    public PacketSender(Context context) {
        this.bleConnection = new BLE(context);
    }

    public boolean connect(String address) {
        return true;
    }

    public void disconnect() {
        bleConnection.disconnect();
    }

    public boolean isConnected() {
        return true;
    }

    public boolean sendPacket(PackagesDef packet) {
        if (!isConnected()) {
            Log.e(TAG, "Tentative d'envoi sans connexion");
            return false;
        }

        try {
            byte[] data = packet.serialize();
             bleConnection.sendData(data);
             return true;
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "Erreur de sérialisation du paquet de type: " + packet.type, e);
            return false;
        }
    }
}