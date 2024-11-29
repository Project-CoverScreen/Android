package com.example.radiolucas.image;

import android.util.Log;

import com.example.radiolucas.spotify.SpotifyInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Downloader class provides functionality to download files from a given URL.
 */
public class Downloader {

    /**
     * Downloads a file from the URL specified in the CoverInfo object and returns it as a byte array.
     *
     * @param spotifyInfo the CoverInfo object containing the URL and file name
     * @return the downloaded file as a byte array
     * @throws RuntimeException if an error occurs during the download
     */
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
                byte[] buffer = new byte[4096];
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