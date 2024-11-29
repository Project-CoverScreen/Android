package com.example.radiolucas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.radiolucas.bluetooth.SPPHandler;
import com.example.radiolucas.image.Bin;
import com.example.radiolucas.image.Resize;
import com.example.radiolucas.spotify.SpotifyConnection;
import com.example.radiolucas.spotify.SpotifyInfo;
import com.example.radiolucas.storage.SaveManager;
import com.example.radiolucas.utils.TimerLogger;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.io.File;

/**
 * MainActivity class handles the main operations of the application, including Spotify authentication and cover information updates.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String TAG = "MainActivity";

    public String UriSpotify;
    private SpotifyConnection spotifyConnection;
    private SpotifyInfo spotifyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spotifyConnection = new SpotifyConnection(this);
        spotifyConnection.authenticateSpotify();

        SaveManager saveManager = new SaveManager(this);
        saveManager.createCoverDirectories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            // Handle Spotify authentication response
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
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "App mise en pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "App reprise");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spotifyConnection.mSpotifyAppRemote != null && spotifyConnection.mSpotifyAppRemote.isConnected()) {
            SpotifyAppRemote.disconnect(spotifyConnection.mSpotifyAppRemote);
        }
    }

    /**
     * Updates the song information with the provided cover information.
     *
     * @param spotifyInfo the cover information to update
     */
    public void updateSongInformation(SpotifyInfo spotifyInfo) {
        this.spotifyInfo = spotifyInfo;
        TimerLogger timerLogger = new TimerLogger();
        // Update UI to show image
        timerLogger.start();

        SaveManager saveManager = new SaveManager(this);
        saveManager.saveFile(this.spotifyInfo.coverData, this.spotifyInfo.coverName, ".jpg", SaveManager.StorageLocation.NATIVE);

        Resize resize = new Resize(this);
        resize.Image(saveManager.getCoverPath(SaveManager.StorageLocation.NATIVE, this.spotifyInfo), saveManager.getCoverPath(SaveManager.StorageLocation.RESIZE, this.spotifyInfo));

        imageAfficher();
        texteAfficher();

        Bin bin = new Bin();
        bin.sendImage(saveManager.getCoverPath(SaveManager.StorageLocation.RESIZE, this.spotifyInfo), saveManager.getCoverPath(SaveManager.StorageLocation.BIN, this.spotifyInfo));

        timerLogger.stop();
        timerLogger.logDuration("MainActivity");
        byte[] coverData = saveManager.readFile(saveManager.getCoverPath(SaveManager.StorageLocation.BIN, this.spotifyInfo));
        SPPHandler sppHandler = new SPPHandler();
        sppHandler.connectToDevice(this);
        sppHandler.sendMessage(coverData);
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
}
