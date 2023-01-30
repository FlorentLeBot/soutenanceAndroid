package cda.flolb.firebasetest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        // On récupère les éléments de la vue
        mQuizModel = new QuizModel();

        flagImageView = view.findViewById(R.id.flag_image_view);

        buttons = new AppCompatButton[6];
        buttons[0] = view.findViewById(R.id.option1);
        buttons[1] = view.findViewById(R.id.option2);
        buttons[2] = view.findViewById(R.id.option3);
        buttons[3] = view.findViewById(R.id.option4);
        buttons[4] = view.findViewById(R.id.option5);
        buttons[5] = view.findViewById(R.id.option6);

        nextQuestionBtn = view.findViewById(R.id.nextBtn);

        questions = view.findViewById(R.id.questions);

        timerTextView = view.findViewById(R.id.timer);

        // On récupère les données de l'API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QuizModel.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestCountriesApi restCountriesApi = retrofit.create(RestCountriesApi.class);

        try {
            restCountriesApi.getCountryInfo().enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    // Si la réponse est bonne
                    if (response.isSuccessful()) {
                        // On crée une liste de pays
                        List<Country> countries = response.body();
                        // On mélange la liste
                        Collections.shuffle(countries);
                        // On récupère les 6 premiers pays (subList() : permet de récupérer une partie d'une liste)
                        mQuizModel.randomCountries = countries.subList(0, 6);
                        // On commence le quiz
                        setQuestion();
                        String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
                        Glide.with(getContext()).load(flagUrl).into(flagImageView);
                        // On rend le bouton "Suivant" invisible
                        nextQuestionBtn.setVisibility(View.INVISIBLE);

                        // Quand on clique sur un bouton de réponse
                        for (AppCompatButton button : buttons) {
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // On récupère le texte du bouton
                                    selectedOptionByUser = button.getText().toString();
                                    // On vérifie si la réponse est correcte
                                    checkAnswer();
                                    // On révèle la bonne réponse
                                    revealAnswer();
                                    // On rend le bouton "Suivant" visible
                                    nextQuestionBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        // Quand on clique sur le bouton "Suivant"
                        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // On passe à la question suivante
                                nextQuestion();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Une erreur s'est produite lors de la récupération des données.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                // onFailure : permet de gérer les erreurs
                public void onFailure(Call<List<Country>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error getting data", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    //----------------------------------------------------------------------------------------------

    private void setQuestion() {
        // Démarrer le timer
        startTimer();
        // Récupérer le nom du pays correct
        correctCountryName = mQuizModel.randomCountries.get(currentQuestion).getTranslations().getFra().getCommon();
        // Créer une liste pour stocker les noms des pays
        List<String> countriesNames = new ArrayList<>();
        // Ajouter tous les noms des pays dans la liste
        for (Country country : mQuizModel.randomCountries) {
            countriesNames.add(country.getTranslations().getFra().getCommon());
        }
        // Mélanger les noms des pays
        Collections.shuffle(countriesNames);
        // Ajouter les noms des pays mélangés aux boutons
        for (int i = 0; i < 6; i++) {
            buttons[i].setText(countriesNames.get(i));
        }
        // Mettre à jour le texte de la question
        questions.setText("Question " + (currentQuestion + 1) + "/" + totalQuestions);
    }


    //----------------------------------------------------------------------------------------------

    private void startTimer() {
        // Vérifier si le timer n'est pas déjà en cours d'exécution
        if (!timerRunning) {
            // Initialiser un nouveau timer avec la durée restante (timeLeftInMillis) et une intervalle de 1s
            timer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                // Mettre à jour le timer à chaque tick
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                // Gérer la fin du timer
                public void onFinish() {
                    revealAnswer();
                    nextQuestionBtn.setVisibility(View.VISIBLE);
                }
            }.start();
            // Mettre à jour l'état du timer
            timerRunning = true;
        }
    }

    //----------------------------------------------------------------------------------------------

    private void updateTimerText() {
        // Récupérer le temps restant en minutes et secondes
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        // Formater le temps restant pour l'affichage (ex : 01:00)
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        // Afficher le temps restant dans le TextView
        timerTextView.setText(timeLeftFormatted);
    }

    //----------------------------------------------------------------------------------------------

    private void resetTimer() {
        // Annuler le timer en cours
        timer.cancel();
        // Réinitialiser la durée restante
        timeLeftInMillis = 30000;
        // Mettre à jour l'affichage du temps restant
        updateTimerText();
        // Créer un nouveau timer avec la durée réinitialisée
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                // Afficher un message Toast lorsque le temps est écoulé
                Toast.makeText(getContext(), "Temps écoulé!", Toast.LENGTH_SHORT).show();
                // Afficher le bouton suivant
                nextQuestionBtn.setVisibility(View.VISIBLE);
                // Révéler la réponse
                revealAnswer();
            }
        }.start();
        timerRunning = true;
    }


    //----------------------------------------------------------------------------------------------

    private void resetButtons() {
        // Pour chaque bouton
        for (AppCompatButton button : buttons) {
            // Activer le bouton
            button.setEnabled(true);
            // Réinitialiser la couleur de fond en blanc
            button.setBackgroundColor(Color.parseColor("#FFFFFF"));
            // Réinitialiser la couleur du texte en noir
            button.setTextColor(Color.parseColor("#000000"));
        }
    }

    //----------------------------------------------------------------------------------------------

    private void nextQuestion() {
        // Incrémenter le numéro de question courante
        currentQuestion++;
        // Si il reste des questions
        if (currentQuestion < totalQuestions) {
            // Réinitialiser les boutons
            resetButtons();
            // Réinitialiser le timer
            resetTimer();
            // Cacher le bouton "question suivante"
            nextQuestionBtn.setVisibility(View.INVISIBLE);
            // Mettre à jour le texte de la question
            questions.setText("Question :" + (currentQuestion + 1) + "/" + totalQuestions);
            // Charger l'image du drapeau
            String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
            Glide.with(getContext()).load(flagUrl).into(flagImageView);
            // Mettre à jour les boutons avec les nouvelles réponses
            updateButtons();
        } else {
            // Finir le quiz
            finishQuiz();
        }
    }

    //----------------------------------------------------------------------------------------------

    private void updateButtons() {
        // Récupérer le nom du pays correct
        correctCountryName = mQuizModel.randomCountries.get(currentQuestion).getTranslations().getFra().getCommon();
        // Créer une liste pour stocker les noms des pays
        List<String> countryNames = new ArrayList<>();
        // Ajouter tous les noms des pays dans la liste
        for (Country country : mQuizModel.randomCountries) {
            countryNames.add(country.getTranslations().getFra().getCommon());
        }
        // Mélanger les noms des pays
        Collections.shuffle(countryNames);
        // Ajouter les noms des pays mélangés aux boutons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(countryNames.get(i));
        }
    }

    //----------------------------------------------------------------------------------------------

    private void finishQuiz() {
        timer.cancel();
        Fragment_Home fragment_home = new Fragment_Home();
        Bundle bundle = new Bundle();
        bundle.putInt("score", mQuizModel.score);
        fragment_home.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment_home);
        fragmentTransaction.commit();
    }

    //----------------------------------------------------------------------------------------------

    public void checkAnswer() {
        // Si le bouton cliqué contient le nom du pays correct
        if (selectedOptionByUser.equals(correctCountryName)) {
            // On incrémente le score
            mQuizModel.incrementScore();
        }
    }

    //----------------------------------------------------------------------------------------------

    public void revealAnswer() {
        // Parcourir tous les boutons de la liste "buttons"
        for (AppCompatButton button : buttons) {
            // Vérifier si le texte associé au bouton correspond au nom du pays correct
            if (button.getText().toString().equals(correctCountryName)) {
                // Mettre le bouton en fond vert et le texte en blanc
                button.setBackgroundResource(R.drawable.round_back_green);
                button.setTextColor(Color.parseColor("#ffffff"));
            } else {
                // Mettre le bouton en fond rouge et le texte en blanc
                button.setBackgroundResource(R.drawable.round_back_red);
                button.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    // On met en pause le timer lorsque l'utilisateur quitte l'application
    @Override
    public void onPause() {
        super.onPause();
        if (timerRunning) {
            timer.cancel();
        }
    }

    //----------------------------------------------------------------------------------------------

    // On relance le timer lorsque l'utilisateur revient dans l'application
    @Override
    public void onResume() {
        super.onResume();
        if (timerRunning) {
            timer.start();
        }
    }

    //----------------------------------------------------------------------------------------------

    // On sauvegarde le score lorsque l'utilisateur quitte l'application
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", mQuizModel.score);
    }

    //----------------------------------------------------------------------------------------------

    // On récupère le score lorsque l'utilisateur revient dans l'application
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mQuizModel.score = savedInstanceState.getInt("score");
        }
    }

    //----------------------------------------------------------------------------------------------

    // On détruit le timer lorsque l'utilisateur quitte l'application
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

}