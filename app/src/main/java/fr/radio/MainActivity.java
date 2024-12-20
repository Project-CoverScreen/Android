package fr.radio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.io.File;

import fr.radio.bluetooth.BLE;
import fr.radio.bluetooth.BluetoothPermissionManager;
import fr.radio.image.Bin;
import fr.radio.image.Resize;
import fr.radio.packages.PackagesCreate;
import fr.radio.packages.PackagesDef;
import fr.radio.packages.PacketDrawImage;
import fr.radio.spotify.SpotifyConnection;
import fr.radio.spotify.SpotifyInfo;
import fr.radio.storage.SaveManager;
import fr.radio.utils.TimerLogger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1337;
    public String last_played = "";
    private SpotifyConnection spotifyConnection;
    private SpotifyInfo spotifyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothPermissionManager permissionManager = new BluetoothPermissionManager(this, new BluetoothPermissionManager.PermissionCallback() {
            @Override
            public void onPermissionsGranted() {
                Log.d(TAG, "Permissions BLE accordées. Démarrage du scan...");
            }

            @Override
            public void onPermissionsDenied() {
                Log.e(TAG, "Permissions BLE refusées. Le scan ne peut pas être effectué.");
            }
        });

        // Vérifie et demande les permissions
        if (!permissionManager.arePermissionsGranted()) {
            permissionManager.requestPermissions();
        } else {
            Log.d(TAG, "Permissions BLE déjà accordées.");
        }
        spotifyConnection = new SpotifyConnection(this);
        spotifyConnection.authenticateSpotify();

        SaveManager saveManager = new SaveManager(this);
        saveManager.createCoverDirectories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    Log.d(TAG, "Auth successful. Token received.");
                    spotifyConnection.connectToSpotifyRemote(response.getAccessToken());
                    break;

                case ERROR:
                    Log.e(TAG, "Auth error: " + response.getError());
                    break;

                default:
                    Log.w(TAG, "Unexpected response type: " + response.getType());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spotifyConnection.mSpotifyAppRemote != null && spotifyConnection.mSpotifyAppRemote.isConnected()) {
            SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);
        }
    }

    public void updateSongInformation(SpotifyInfo spotifyInfo) {
        this.spotifyInfo = spotifyInfo;
        TimerLogger timerLogger = new TimerLogger();
        timerLogger.start();

        SaveManager saveManager = new SaveManager(this);

        Log.v(TAG, "Téléchargement du fichier");
        saveManager.saveFile(this.spotifyInfo.coverData, this.spotifyInfo.coverName, ".jpg", SaveManager.StorageLocation.NATIVE);

        Resize resize = new Resize(this);
        resize.Image(saveManager.getCoverPath(SaveManager.StorageLocation.NATIVE, this.spotifyInfo), saveManager.getCoverPath(SaveManager.StorageLocation.RESIZE, this.spotifyInfo));

        Bin bin = new Bin();
        bin.sendImage(saveManager.getCoverPath(SaveManager.StorageLocation.RESIZE, this.spotifyInfo), saveManager.getCoverPath(SaveManager.StorageLocation.BIN, this.spotifyInfo));

        BluetoothSend(); // BLE fun ! :D (send image to BLE device)

        imageAfficher();
        texteAfficher();

        timerLogger.stop();
        timerLogger.logDuration("MainActivity 3");
    }

    public void imageAfficher() {
        ImageView coverOG = findViewById(R.id.Spotifycover);
        ImageView coverRE = findViewById(R.id.ResizeCover);
        Uri uriOG = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Cover/native/" + spotifyInfo.coverName + ".jpg"));
        Uri uriRE = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Cover/resize/" + spotifyInfo.coverName + ".jpg"));
        coverOG.setImageURI(uriOG);
        coverRE.setImageURI(uriRE);
    }

    public void texteAfficher() {
        TextView album = findViewById(R.id.albumName);
        album.setText(spotifyInfo.albumName);
        TextView artiste = findViewById(R.id.artisteName);
        artiste.setText(spotifyInfo.artistName);
        TextView song = findViewById(R.id.resizeName);
        song.setText(spotifyInfo.trackName);
    }

    public void BluetoothSend() {
        if (this.spotifyInfo != null) {
            Log.e("MainActivity", "BluetoothSend");
        } else {
            Log.e("MainActivity", "SpotifyInfo is null");
            return;
        }
        if (this.last_played.equals(this.spotifyInfo.trackName)) {
            Log.e("MainActivity", "Same song, not sending");
            return;
        }
        this.last_played = this.spotifyInfo.trackName;
        PackagesDef packet = new PacketDrawImage();
        SaveManager saveManager = new SaveManager(this);
        byte[] coverData = saveManager.readFile(saveManager.getCoverPath(SaveManager.StorageLocation.BIN, this.spotifyInfo));
        BLE ble = new BLE(this);
        ble.connectToDevice("A0:85:E3:EA:14:09");
        while (ble.characteristic == null) {
            try {
                Log.e("MainActivity", "Waiting for characteristic");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        byte[][] packets = test_packet();
        for (int i = 0; i < packets.length; i++) {
            ble.sendData(packets[i]);
        }
        ble.disconnect();
    }

    public byte[][] test_packet() {
        PackagesCreate packagesCreate = new PackagesCreate(this);
        SaveManager saveManager = new SaveManager(this);
        byte[] coverData = saveManager.readFile(saveManager.getCoverPath(SaveManager.StorageLocation.BIN, this.spotifyInfo));

        byte[][] packets = packagesCreate.imageChunked(coverData);
        Log.e("MainActivity", "test_packet ok");
        return packets;
    }
}
