package cda.flolb.firebasetest;

import android.os.CountDownTimer;

import java.util.List;

public class QuizModel {
    public static final String API_URL = "https://restcountries.com/v3/";
    public static final int TIME_LIMIT = 30000;

    public int score = 0;
    public List<Country> randomCountries;
    public boolean timerRunning = false;
    public long timeLeftInMillis = TIME_LIMIT;
    public void incrementScore() {
        this.score++;
    }
}