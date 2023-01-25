/**
 * Activité de connexion pour une application Android utilisant Firebase.
 * Elle gère la connexion d'un utilisateur en vérifiant ses informations de connexion (numéro de téléphone et mot de passe) avec les données stockées dans la base de données Firebase.
 *
 * @author Auteur
 */

package cda.flolb.firebasetest;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    /**
     * Objet de référence à la base de données Firebase
     */

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fir-project-a4f02-default-rtdb.firebaseio.com/");

    /**
     * Méthode appelée lorsque l'activité est créée. Elle définit le contenu de l'affichage à partir d'un fichier XML (activity_login)
     * et initialise les éléments de l'interface utilisateur (champs de saisie, boutons).
     *
     * @param savedInstanceState état de l'instance sauvegardée
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Récupère les éléments de la vue
         */

        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final TextView registerNowBtn = findViewById(R.id.registerNowBtn);

        /**
         * Ajoute un listener (écouteur d'évenement) sur le bouton de connexion
         */

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Récupère les valeurs des champs
                 */

                final String phoneValue = phone.getText().toString();
                final String passwordValue = password.getText().toString();

                // On vérifie que les champs ne sont pas vides
                if (phoneValue.isEmpty() || passwordValue.isEmpty()) {

                    // On affiche un message d'erreur
                    Toast.makeText(Login.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {

                    /**
                     * Utilise la référence de la base de données pour vérifier les informations de connexion de l'utilisateur.
                     * Si les informations sont correctes, l'utilisateur est connecté et dirigé vers la prochaine activité de l'application.
                     * Sinon, un message d'erreur est affiché.
                     */

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // On vérifie que le numéro de téléphone existe dans la base de données
                            if (snapshot.hasChild(phoneValue)) {

                                final String getPassword = snapshot.child(phoneValue).child("password").getValue(String.class);

                                //TODO : Vérifier le mot de passe
                                // On vérifie que le mot de passe est correct
                                if (getPassword.equals(passwordValue)) {
                                    Toast.makeText(Login.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                                    /**
                                     * Crée un intent pour passer à l'activité suivante (MainActivity)
                                     */

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Mot de passe ou numéro de téléphone incorrect", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Login.this, "Mot de passe ou numéro de téléphone incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Méthode appelée en cas d'erreur
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        /**
         * Ajoute un listener (écouteur d'évenement) sur le bouton d'inscription
         */

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Ferme l'activité de connexion et ouvre l'activité d'inscription
                 */

                finish();
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }
}

