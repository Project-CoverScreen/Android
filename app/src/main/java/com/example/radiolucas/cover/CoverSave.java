package com.example.radiolucas.cover;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CoverSave {

    public String dir_name = "Cover";
    public String file_name = "Cover.jpeg";

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void coverDir() {

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                dir_name);
        ;
        if (!directory.exists()) {
            boolean created = directory.mkdir(); // Crée le répertoire
            if (created) {
                Log.e("Repertoire", "Répertoire créé avec succès !");
            } else {
                Log.e("Repertoire", "Échec de la création du répertoire !");
            }
        }
    }

    public void coverSave() {
        File directory = new File(dir_name);
        File fichier = new File(directory, file_name);
        try (FileOutputStream fos = new FileOutputStream(fichier)) {
            fos.write("Contenu de mon fichier".getBytes());
            Log.e("Fichier", "Fichier enregistré avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
