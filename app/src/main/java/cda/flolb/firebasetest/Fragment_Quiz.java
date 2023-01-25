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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QuizModel.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestCountriesApi restCountriesApi = retrofit.create(RestCountriesApi.class);

        restCountriesApi.getCountryInfo().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful()) {
                    List<Country> countries = response.body();
                    Collections.shuffle(countries);
                    mQuizModel.randomCountries = countries.subList(0, 6);
                    setQuestion();
                    String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
                    Glide.with(getContext()).load(flagUrl).into(flagImageView);
                    nextQuestionBtn.setVisibility(View.INVISIBLE);

                    for (AppCompatButton button : buttons) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedOptionByUser = button.getText().toString();
                                checkAnswer();
                                revealAnswer();
                                nextQuestionBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextQuestion();
                        }
                    });
                } else {
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

    //----------------------------------------------------------------------------------------------

    private void setQuestion() {
        startTimer();
        correctCountryName = mQuizModel.randomCountries.get(currentQuestion).getTranslations().getFra().getCommon();
        List<String> capitalNames = new ArrayList<>();
        for (Country country : mQuizModel.randomCountries) {
            capitalNames.add(country.getTranslations().getFra().getCommon());
        }
        Collections.shuffle(capitalNames);
        for (int i = 0; i < 6; i++) {
            buttons[i].setText(capitalNames.get(i));
        }
        questions.setText("Question " + (currentQuestion + 1) + "/" + totalQuestions);
    }

    //----------------------------------------------------------------------------------------------

    private void startTimer() {
        if (!timerRunning) {
            timer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    revealAnswer();
                    nextQuestionBtn.setVisibility(View.VISIBLE);
                }
            }.start();
            timerRunning = true;
        }
    }

    //----------------------------------------------------------------------------------------------

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }


    //----------------------------------------------------------------------------------------------

    private void resetTimer() {
        timer.cancel();
        timeLeftInMillis = 30000;
        updateTimerText();
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
                nextQuestionBtn.setVisibility(View.VISIBLE);
                revealAnswer();
            }
        }.start();
        timerRunning = true;
    }

    //----------------------------------------------------------------------------------------------

    private void resetButtons() {
        for (AppCompatButton button : buttons) {
            button.setEnabled(true);
            button.setBackgroundColor(Color.parseColor("#FFFFFF"));
            button.setTextColor(Color.parseColor("#000000"));
        }
    }

    //----------------------------------------------------------------------------------------------

    private void nextQuestion() {
        currentQuestion++;
        if (currentQuestion < totalQuestions) {
            resetButtons();
            resetTimer();
            nextQuestionBtn.setVisibility(View.INVISIBLE);
            questions.setText("Question :" + (currentQuestion + 1) + "/" + totalQuestions);
            String flagUrl = mQuizModel.randomCountries.get(currentQuestion).getFlags().get(1);
            Glide.with(getContext()).load(flagUrl).into(flagImageView);
            updateButtons();
        } else {
            finishQuiz();
        }
    }

    //----------------------------------------------------------------------------------------------

    private void updateButtons() {
        correctCountryName = mQuizModel.randomCountries.get(currentQuestion).getTranslations().getFra().getCommon();
        List<String> countryNames = new ArrayList<>();
        for (Country country : mQuizModel.randomCountries) {
            countryNames.add(country.getTranslations().getFra().getCommon());
        }
        Collections.shuffle(countryNames);
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
        if (selectedOptionByUser.equals(correctCountryName)) {
            mQuizModel.incrementScore();
        }
    }

    //----------------------------------------------------------------------------------------------

    public void revealAnswer() {
        for (AppCompatButton button : buttons) {
            if (button.getText().toString().equals(correctCountryName)) {
                button.setBackgroundResource(R.drawable.round_back_green);
                button.setTextColor(Color.parseColor("#ffffff"));
            }
            else {
                button.setBackgroundResource(R.drawable.round_back_red);
                button.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }
}