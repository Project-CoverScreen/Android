package com.example.radiolucas.cover;

public class CoverInfo {

    public String spotify_id = "";      // type spotify:image:ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String cover_name = "";  // type ab67616d0000b273f33c3b87535a0f89bda5f5be
    public String cover_url = "";       // type https://i.scdn.co/image/ab67616d0000b273f33c3b87535a0f89bda5f5be

    public CoverInfo(String Spotify_id) {
        this.spotify_id = Spotify_id;
        //Log.e("CoverInfo", "Spotify ID : " + spotify_id);
        this.cover_name = spotify_id.replace("spotify:image:", "");
        //Log.e("CoverInfo", "Cover Name : " + cover_name);
        cover_url = spotify_id.replace("spotify:image:", "https://i.scdn.co/image/");
        //Log.e("CoverInfo", "Cover URL : " + cover_url);
    }

    public String getCover_url() {
        return cover_url;
    }

    public String getCover_name() {
        return cover_name;
    }

    public String Merde(String merde) {
        return merde.replace("spotify:image:", "https://i.scdn.co/image/");
    }
}
