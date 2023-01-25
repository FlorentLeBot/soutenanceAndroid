package cda.flolb.firebasetest;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_Home extends Fragment  {

    TextView rightAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home, container, false);
        // Récupération des éléments de la vue
        rightAnswer = view.findViewById(R.id.rightAnswers);

        //TODO : Afficher Bonjour + nom de l'utilisateur

        displayScore();
        return view;
    }

    private void displayScore() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int score = bundle.getInt("score");
            rightAnswer.setText("Votre score est de : " + score + " / 6");
        }
    }
}
