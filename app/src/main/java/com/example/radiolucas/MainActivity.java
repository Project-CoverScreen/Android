package com.example.radiolucas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.radiolucas.cover.CoverInfo;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String TAG = "MainActivity";
    public String UriSpotify;

    private Spotify spotify;
    private SaveManager coverSaveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des classes nécessaires
        spotify = new Spotify(this);
        coverSaveManager = new SaveManager(this);

        // Authentification Spotify
        spotify.authenticateSpotify();

        // Préparation des répertoires pour les couvertures
        coverSaveManager.createCoverDirectories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            // Gestion de la réponse de l'authentification Spotify
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    Log.d(TAG, "Auth successful. Token received.");
                    spotify.connectToSpotifyRemote(response.getAccessToken());
                    break;

                case ERROR:
                    Log.e(TAG, "Auth error: " + response.getError());
                    break;

                default:
                    Log.w(TAG, "Unexpected response type: " + response.getType());
            }
            Log.e(TAG, "Auth response: " + UriSpotify);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Déconnexion de Spotify pour éviter les fuites mémoire
        if (spotify.mSpotifyAppRemote != null && spotify.mSpotifyAppRemote.isConnected()) {
            SpotifyAppRemote.disconnect(spotify.mSpotifyAppRemote);
        }
    }

    /**
     * Définit l'URI de la couverture et initialise CoverInfo.
     *
     * @param UriSpotify l'URI de la couverture Spotify
     */
    public String setCoverUri(String UriSpotify) {
        if (UriSpotify != null && !UriSpotify.isEmpty()) {
            Log.d(TAG, "Cover URI received: " + UriSpotify);
            CoverInfo coverInfo = new CoverInfo(UriSpotify); // Créer un objet CoverInfo avec l'URI
            return this.UriSpotify;
        } else {
            Log.w(TAG, "Cover URI is null or empty.");
            return "STTTTTTTTTTTTTTTTTTTTTTTTOOOOOOOOOOOOOOOOOOOOOOOOP";
        }
    }
}
