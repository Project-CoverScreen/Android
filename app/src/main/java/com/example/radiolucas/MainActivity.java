package com.example.radiolucas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

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
            Log.e(TAG, "Auth response received: " + response.getType());
        }
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
        Log.d(TAG, "Cover URI received: " + UriSpotify);
        this.spotifyInfo = spotifyInfo;
        // Update UI to show image
        SaveManager saveManager = new SaveManager(this);
        saveManager.saveFile(this.spotifyInfo.coverData, this.spotifyInfo.cover_name, ".jpg", SaveManager.StorageLocation.NATIVE);
    }
}
