package com.example.radiolucas;

import android.content.Context;
import android.util.Log;

import com.example.radiolucas.cover.CoverInfo;
import com.example.radiolucas.cover.CoverSaveManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

    public void downloadFile(CoverInfo coverInfo, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Créer une connexion
                    URL url = new URL(coverInfo.cover_url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // Vérifier le code de réponse
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e("Cover_Downloader", "Erreur serveur: " + connection.getResponseCode());
                        throw new RuntimeException("Erreur serveur: " + connection.getResponseCode());

                    }

                    // Préparer le téléchargement
                    int fileLength = connection.getContentLength();
                    InputStream input = new BufferedInputStream(connection.getInputStream());
                    Log.e("Cover_Downloader", "Téléchargement en cours : ");

                    // Utiliser ByteArrayOutputStream pour capturer tous les bytes
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Tampon pour la lecture
                    byte[] buffer = new byte[4096];
                    int count;

                    // Lire et stocker tous les bytes
                    while ((count = input.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, count);
                    }
                    // Convertir le flux en tableau de bytes
                    byte[] downloadedData = byteArrayOutputStream.toByteArray();

                    // Fermer les flux
                    byteArrayOutputStream.close();
                    input.close();

                    // Initialiser le gestionnaire de sauvegarde
                    CoverSaveManager coverSaveManager = new CoverSaveManager(context);

                    // Créer les répertoires si nécessaire
                    coverSaveManager.createCoverDirectories();

                    // Sauvegarder le fichier dans le dossier NATIVE
                    Log.e("Cover_Downloader", "Sauvegarde du fichier : " + coverInfo.cover_name + ".jpg");
                    File savedFile = coverSaveManager.saveFile(
                            downloadedData,
                            coverInfo.cover_name,
                            ".jpg",
                            CoverSaveManager.StorageLocation.NATIVE
                    );
                    // Log du résultat
                    if (savedFile != null) {
                        Log.e("Cover_Downloader", "Téléchargement terminé : " + savedFile.getAbsolutePath());
                    } else {
                        Log.e("Cover_Downloader", "Échec de sauvegarde du fichier");
                    }

                } catch (Exception e) {
                    Log.e("Cover_Downloader", "Erreur de téléchargement : " + e.getMessage(), e);
                }
            }
        }).start();
    }
}