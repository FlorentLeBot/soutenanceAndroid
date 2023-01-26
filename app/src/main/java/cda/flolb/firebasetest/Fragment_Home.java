package cda.flolb.firebasetest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment_Home extends Fragment  {

    TextView rightAnswer;
    TextView userNames;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fir-project-a4f02-default-rtdb.firebaseio.com/");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home, container, false);
        // Récupération des éléments de la vue
        rightAnswer = view.findViewById(R.id.rightAnswers);
        userNames = view.findViewById(R.id.userName);
        // On récupére le champs
        // Afficher le nom de l'utilisateur en récupérant la valeur de la clé "fullname" dans la base de données realtime de Firebase
        databaseReference.child("users").child(Login.phoneNo).child("fullname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNames.setText("Bonjour " + snapshot.getValue(String.class));
            }

            @Override
            // Méthode appelée en cas d'erreur
            public void onCancelled(@NonNull DatabaseError error) {
                userNames.setText("Erreur");
            }
        });

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
