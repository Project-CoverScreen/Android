package fr.radio.image;

import android.util.Log;

import fr.radio.spotify.SpotifyInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Downloader {

    public byte[] downloadFile(SpotifyInfo spotifyInfo) {
        try {
            URL url = new URL(spotifyInfo.coverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("Downloader", "Erreur serveur: " + connection.getResponseCode());
                throw new RuntimeException("Erreur serveur: " + connection.getResponseCode());
            }

            try (InputStream input = new BufferedInputStream(connection.getInputStream());
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                Log.v("Cover_Downloader", "Téléchargement en cours");
                byte[] buffer = new byte[256];
                int count;

                while ((count = input.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, count);
                }

                Log.v("Downloader", "Téléchargement terminé");
                return byteArrayOutputStream.toByteArray();
            }
        } catch (MalformedURLException e) {
            Log.e("Downloader", "Erreur lors de la création de l'URL", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.e("Downloader", "Erreur de téléchargement", e);
            throw new RuntimeException(e);
        }
    }
}