package com.example.radiolucas.utils;

import android.util.Log;

public class TimerLogger {

    private static final String DEFAULT_TAG = "TimerLogger"; // Tag par défaut pour les logs

    private long startTime;
    private long endTime;

    /**
     * Démarre le chronomètre.
     */
    public void start() {
        startTime = System.nanoTime(); // Enregistrer l'heure de début en nanosecondes
    }

    /**
     * Termine le chronomètre.
     */
    public void stop() {
        endTime = System.nanoTime(); // Enregistrer l'heure de fin en nanosecondes
    }

    /**
     * Calcule et affiche le temps écoulé dans les logs.
     *
     * @param tag Le tag pour identifier le log.
     */
    public void logDuration(String tag) {
        if (startTime == 0 || endTime == 0) {
            Log.e(tag, "Le chronomètre n'a pas été correctement démarré ou arrêté.");
            return;
        }

        long duration = endTime - startTime; // Calculer le temps écoulé en nanosecondes
        double durationMs = duration / 1_000_000.0; // Convertir en millisecondes

        Log.d(tag, "Temps écoulé : " + durationMs + " ms");
    }

    /**
     * Calcule et affiche le temps écoulé avec le tag par défaut.
     */
    public void logDuration() {
        logDuration(DEFAULT_TAG);
    }
}
