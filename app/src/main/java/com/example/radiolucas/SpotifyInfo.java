package com.example.radiolucas;

import android.content.Context;
import android.util.Log;

public class SpotifyInfo {

    public Context context;

    public String coverUri = "";      // type spotify:image:ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String coverName = "";      // type ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String coverUrl = "";       // type https://i.scdn.co/image/ab67616d0000b273f33c3b87535a0f89bda5f5be
    public byte[] coverData;

    public String albumName;
    public String artistName;
    public String trackName;

    public SpotifyInfo(String coverUri, String albumName, String artistName, String trackName ) {
        coverName(coverUri);
        coverDl(context);
        album(albumName);
        artist(artistName);
        track(trackName);
    }

    private void coverName(String Spotify_id) {
        this.coverUri = Spotify_id;
        Log.v("SpotifyInfo", "Spotify ID : " + coverUri);
        this.coverName = coverUri.replace("spotify:image:", "");
        Log.v("CoverInfo", "Cover Name : " + coverName);
        this.coverUrl = coverUri.replace("spotify:image:", "https://i.scdn.co/image/");
        Log.v("CoverInfo", "Cover URL : " + coverUrl);
    }

    private void coverDl(Context context) {
        Downloader downloader = new Downloader();
        coverData = downloader.downloadFile(this);
    }

    private void album(String albumName) {
        this.albumName = albumName;
    }

    private void artist(String artistName) {
        this.artistName = artistName;
    }

    private void track(String trackName) {
        this.trackName = trackName;
    }
}
