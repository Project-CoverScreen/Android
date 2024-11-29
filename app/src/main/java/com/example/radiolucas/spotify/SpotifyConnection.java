package com.example.radiolucas.spotify;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.radiolucas.MainActivity;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The Spotify class provides functionality to connect to the Spotify app remote and authenticate the user.
 */
public class SpotifyConnection {

    private static final String CLIENT_ID = "fc99f5a7950c4d11b44dca56a05bffc6"; // Replace with your client ID
    private static final String REDIRECT_URI = "https://google.com/";
    private static final int REQUEST_CODE = 1337;
    private final MainActivity activity;
    public SpotifyAppRemote mSpotifyAppRemote;
    public String Uri;
    SpotifyInfo spotifyInfo;

    /**
     * Constructs a new Spotify instance.
     *
     * @param context the main activity of the application
     */
    public SpotifyConnection(MainActivity context) {
        this.activity = context;
    }

    /**
     * Connects to the Spotify app remote using the provided access token.
     *
     * @param accessToken the access token for Spotify
     */
    public void connectToSpotifyRemote(String accessToken) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(activity, connectionParams, new Connector.ConnectionListener() {

            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("SpotifyRemote", "Connected! Yay!");

                // Subscribe to player state and fetch asynchronously
                mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
                    fetchUriAsync()
                            .thenAccept(SpotifyInfo -> {
                                spotifyInfo = SpotifyInfo;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        activity.updateSongInformation(SpotifyInfo);
                                        Toast.makeText(activity, "Track : " + spotifyInfo.trackName, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.d("SpotifyRemote", "Cover URI : " + spotifyInfo.coverUrl);
                            })
                            .exceptionally(e -> {
                                Log.e("SpotifyRemote", "Error fetching URI asynchronously", e);
                                return null;
                            });
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("SpotifyRemote", "Failed to connect", throwable);
            }
        });
    }

    /**
     * Fetches the URI asynchronously using CompletableFuture.
     *
     * @return a CompletableFuture containing the URI
     */
    public CompletableFuture<SpotifyInfo> fetchUriAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PlayerState playerState = getPlayerStateWithTimeout();
                Track track = playerState.track;

                Log.v("SpotifyRemote", "Track Info: " + track.album.name + " - " + track.artist.name + " - " + track.name);

                return new SpotifyInfo(track);
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch player state", e);
            }
        });
    }

    /**
     * Retrieves the player state with a timeout.
     *
     * @return the player state
     * @throws Exception if an error occurs while retrieving the player state
     */
    @NonNull
    private PlayerState getPlayerStateWithTimeout() throws Exception {
        long timeout = TimeUnit.SECONDS.toMillis(20);
        long startTime = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - startTime > timeout) {
                throw new TimeoutException("Timed out waiting for player state");
            }

            PlayerState playerState = mSpotifyAppRemote.getPlayerApi().getPlayerState().await().getData();
            if (playerState != null) {
                return playerState;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new Exception("Thread interrupted while waiting for player state", e);
            }
        }
    }

    /**
     * Authenticates the user with Spotify.
     */
    public void authenticateSpotify() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID,
                AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"app-remote-control", "user-read-currently-playing"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }
}
