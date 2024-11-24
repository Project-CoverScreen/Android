package com.example.radiolucas;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.radiolucas.cover.CoverInfo;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Spotify {

    private static final String CLIENT_ID = "fc99f5a7950c4d11b44dca56a05bffc6"; // Replace with your client ID
    private static final String REDIRECT_URI = "https://google.com/";
    private static final int REQUEST_CODE = 1337;
    private final MainActivity activity;
    public SpotifyAppRemote mSpotifyAppRemote;

    public Spotify(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToSpotifyRemote(String accessToken) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(activity, connectionParams, new Connector.ConnectionListener() {

            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.e("SpotifyRemote", "Connected! Yay!");

                mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Downloader coverDownloader = new Downloader();

                            try {

                                PlayerState updatedPlayerState = getPlayerStateWithTimeout();
                                assert updatedPlayerState.track.imageUri.raw != null;
                                Log.e("Currently Playing", updatedPlayerState.track.imageUri.raw);
                                CoverInfo Info = new CoverInfo(playerState.track.imageUri.raw);
                                assert playerState.track.imageUri.raw != null;
                                coverDownloader.downloadFile(Info, activity);

                            } catch (Exception e) {
                                Log.e("SpotifyRemote", "Error fetching player state", e);
                            }
                        }
                    }).start();
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("SpotifyRemote", "Failed to connect", throwable);
            }
        });
    }

    @NonNull
    private PlayerState getPlayerStateWithTimeout() throws Exception {
        long timeout = TimeUnit.SECONDS.toMillis(20); // 20 seconds timeout
        long startTime = System.currentTimeMillis();

        while (true) {
            // Check if the timeout has been reached
            if (System.currentTimeMillis() - startTime > timeout) {
                throw new TimeoutException("Timed out waiting for player state");
            }

            // Attempt to get the player state
            PlayerState playerState = mSpotifyAppRemote.getPlayerApi().getPlayerState().await().getData();
            if (playerState != null) {
                return playerState; // Return player state if successfully retrieved
            }

            // Add a small delay before retrying
            try {
                Thread.sleep(500); // Retry every 500 ms
            } catch (InterruptedException e) {
                throw new Exception("Thread interrupted while waiting for player state", e);
            }
        }
    }

    public void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID,
                AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"app-remote-control", "user-read-currently-playing"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }
}
