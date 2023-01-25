package cda.flolb.firebasetest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cda.flolb.firebasetest.Country;
import cda.flolb.firebasetest.QuizModel;
import cda.flolb.firebasetest.R;
import cda.flolb.firebasetest.RestCountriesApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Fragment_Quiz extends Fragment {

    private ImageView flagImageView;
    private AppCompatButton[] buttons;
    private TextView timerTextView;
    private QuizModel mQuizModel;
    private CountDownTimer timer;
    private boolean timerRunning;
    private long timeLeftInMillis = 30000; // 30 seconds
    private String correctCountryName;
    private AppCompatButton nextQuestionBtn;
    private TextView questions;
    public int currentQuestion = 0;
    public int totalQuestions = 6;
    private String selectedOptionByUser = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__quiz, container, false);

        // Une classe modèle pour gérer les données du quiz
        mQuizModel = new QuizModel();

        flagImageView = view.findViewById(R.id.flag_image_view);

        // Les 4 boutons de réponse
        buttons = new AppCompatButton[4];
        buttons[0] = view.findViewById(R.id.option1);
        buttons[1] = view.findViewById(R.id.option2);
        buttons[2] = view.findViewById(R.id.option3);
        buttons[3] = view.findViewById(R.id.option4);

        // Le bouton pour passer à la question suivante
        nextQuestionBtn = view.findViewById(R.id.nextBtn);

        // Le compteur des 6 questions du quiz
        questions = view.findViewById(R.id.questions);

        // Le timer
        timerTextView = view.findViewById(R.id.timer);

        // On récupère les données de l'API avec Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QuizModel.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestCountriesApi restCountriesApi = retrofit.create(RestCountriesApi.class);

        // On récupère 4 noms de pays aléatoirement et un drapeau
        restCountriesApi.getCountryInfo().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful()) {
                    // On récupère les 4 pays
                    List<Country> countries = response.body();
                    // On mélange les pays
                    Collections.shuffle(countries);
                    // On récupère les 6 pays mélangés
                    mQuizModel.randomCountries = countries.subList(0, 6);
                    // On récupère l'url du drapeau du premier pays
                    String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
                    // On affiche les noms des pays dans les boutons
                    setCountryNamesOnButtons();
                    // On affiche l'image du drapeau
                    Glide.with(getContext()).load(flagUrl).into(flagImageView);
                    // On démarre le timer
                    startTimer();
                    // On met à jour le texte de la question avec le numéro de la question courante
                    nextQuestionBtn.setVisibility(View.INVISIBLE);

                    questions.setText("Question " + (currentQuestion + 1) + "/" + totalQuestions);

                    // Quand on clique sur un bouton de réponse on appelle la méthode checkAnswer(), qui va vérifier si la réponse est correcte ou non et incrémenter le score si c'est le cas
                    // On met en vert le bouton de la réponse selectionnée par l'utilisateur
                    // On attent que l'utilisateur clique sur le bouton "Question suivante" pour passer à la question suivante
                    for (AppCompatButton button : buttons) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedOptionByUser = button.getText().toString();
                                checkAnswer();
                                button.setBackgroundColor(Color.GREEN);
                                nextQuestionBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    // Quand on clique sur le bouton "Question suivante" on affiche en vert la bonne réponse et en rouge les mauvaises réponses
                    // On appelle la méthode nextQuestion() qui va passer à la question suivante et mettre à jour les boutons de réponse
                    // Quand on arrive à la dernière question on affiche le score et on redirige vers la Fragment_Home grâce à la méthode finshQuiz()
                    nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentQuestion == totalQuestions - 1) {
                                Toast.makeText(getContext(), "Votre score est de " + mQuizModel.score + " sur " + totalQuestions, Toast.LENGTH_SHORT).show();
                                finishQuiz();
                            } else {
                                for (AppCompatButton button : buttons) {
                                    if (button.getText().toString().equals(correctCountryName)) {
                                        button.setBackgroundColor(Color.GREEN);
                                    } else {
                                        button.setBackgroundColor(Color.RED);
                                    }
                                }
                                nextQuestion();
                            }
                        }
                    });
                } else {
                    // Affiche un message d'erreur si la réponse n'est pas réussie
                    Toast.makeText(getContext(), "Une erreur s'est produite lors de la récupération des données.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                Toast.makeText(getContext(), "Error getting data", Toast.LENGTH_SHORT).show();
            }

        });

        return view;

    }

    //------------------------------------ Méthodes pour le timer ------------------------------------

    // Méthode pour démarrer le timer
    private void startTimer() {
        timer = new CountDownTimer(QuizModel.TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mQuizModel.timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                mQuizModel.timerRunning = false;
                //revealAnswer();
            }
        }.start();
        mQuizModel.timerRunning = true;
    }

    //------------------------------------ Méthodes pour mettre à jour le timer ------------------------------------

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }


    //------------------------------------ Méthodes pour supprimer le timer ------------------------------------

    private void resetTimer() {
        // Arrêter le timer en cours
        timer.cancel();
        // Mettre à jour timeLeftInMillis avec la durée initiale
        timeLeftInMillis = 30000;
        // Mettre à jour le texte du timer avec la durée initiale
        updateTimerText();
        // Créer un nouveau timer avec la durée initiale
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                Toast.makeText(getContext(), "Temps écoulé!", Toast.LENGTH_SHORT).show();
            }
        }.start();
        timerRunning = true;
    }

    //------------------------------------ Méthodes supprimer les couleurs des boutons  ------------------------------------

    private void resetButtons() {
        for (AppCompatButton button : buttons) {
            button.setEnabled(true);
            button.setBackgroundColor(Color.parseColor("#FFFFFF"));
            button.setTextColor(Color.parseColor("#000000"));
        }
    }



    private void setCountryNamesOnButtons() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(mQuizModel.randomCountries.get(i).getTranslations().getFra().getCommon());
        }
    }


    //------------------------------------ Méthodes pour passer à la question suivante ------------------------------------

    // Tant que la question courante est inférieure au nombre total de questions on passe à la question suivante (6 questions) et on met à jour les boutons de réponse avec les noms des pays
    private void nextQuestion() {
        currentQuestion++;
        if (currentQuestion < totalQuestions) {
            resetButtons();
            setCountryNamesOnButtons();
            // On met à jour la nouvelle question
            String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
            // On affiche l'image du drapeau dans l'ImageView
            Glide.with(getContext()).load(flagUrl).into(flagImageView);

            // On incrémente le compteur de question
            questions.setText("Question " + (currentQuestion + 1) + " sur 6");

            // On rend invisible le bouton question suivante
            nextQuestionBtn.setVisibility(View.INVISIBLE);

            // On remets le timeer
            resetTimer();
        }
    }

    //------------------------------------ Méthodes pour terminer le quiz ------------------------------------

    // On arrête le timer et on redirige vers la Fragment_Home en supprimant la Fragment_Quiz du backstack et on affiche le score dans le Fragment_Home
    private void finishQuiz() {
        timer.cancel();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment_Home fragment_home = new Fragment_Home();
        fragmentTransaction.replace(R.id.fragment_container, fragment_home);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void checkAnswer() {
        if (selectedOptionByUser.equals(correctCountryName)) {
            mQuizModel.score++;
        }
    }

    public void updateButtons() {
        for (AppCompatButton button : buttons) {
            if (button.getText().toString().equals(correctCountryName)) {
                button.setBackgroundColor(Color.GREEN);
            }
        }
    }
}