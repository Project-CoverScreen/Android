package com.example.radiolucas;

import android.content.Context;
import android.util.Log;

import com.spotify.protocol.types.Track;

public class SpotifyInfo {

    public Context context;
    public Track track;

    public String coverUri = "";      // type spotify:image:ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String coverName = "";      // type ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String coverUrl = "";       // type https://i.scdn.co/image/ab67616d0000b273f33c3b87535a0f89bda5f5be
    public byte[] coverData;

    public String albumName;
    public String artistName;
    public String trackName;

    public SpotifyInfo(Track track) {
        this.track = track;

        coverName(track.imageUri.raw);
        coverDl(context);
        album(track.album.name);
        artist(track.artist.name);
        track(track.name);
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
