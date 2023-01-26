package cda.flolb.firebasetest;

import android.os.CountDownTimer;

import java.util.List;

/**
 * Classe QuizModel qui contient les informations relatives à un quiz
 */
public class QuizModel {

    public static final String API_URL = "https://restcountries.com/v3/";
    public static final int TIME_LIMIT = 30000;

    public int score = 0;
    public List<Country> randomCountries;
    public boolean timerRunning = false;
    public long timeLeftInMillis = TIME_LIMIT;

    /**
     * Incrémente le score du quiz
     */
    public void incrementScore() {
        this.score++;
    }
}
