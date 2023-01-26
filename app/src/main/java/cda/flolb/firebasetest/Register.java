/**
 * Activité d'inscription pour une application Android utilisant Firebase.
 * Elle gère l'enregistrement d'un utilisateur en récupérant ses informations (nom complet, adresse e-mail, numéro de téléphone et mot de passe)
 * et en les stockant dans la base de données Firebase.
 *
 * @author Auteur
 */

package cda.flolb.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    /**
     * Objet de référence à la base de données Firebase
     */

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fir-project-a4f02-default-rtdb.firebaseio.com/");

    /**
     * Méthode appelée lorsque l'activité est créée. Elle définit le contenu de l'affichage à partir d'un fichier XML (activity_register)
     * et initialise les éléments de l'interface utilisateur (champs de saisie, boutons).
     *
     * @param savedInstanceState état de l'instance sauvegardée
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On associe la vue à l'activité
        setContentView(R.layout.activity_register);

        /**
         * Récupère les éléments de la vue (champs de saisie, boutons)
         */

        final EditText fullname = findViewById(R.id.fullname);
        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final EditText passwordConfirm = findViewById(R.id.conPassword);
        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNowBtn);

        /**
         * Ajoute un listener (écouteur d'évenement) sur le bouton d'inscription
         */
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Récupère les valeurs des champs
                final String fullnameValue = fullname.getText().toString();
                final String emailValue = email.getText().toString();
                final String phoneValue = phone.getText().toString();
                final String passwordValue = password.getText().toString();
                final String passwordConfirmValue = passwordConfirm.getText().toString();

                /**
                 * Vérifie que les champs ne sont pas vides
                 */

                if (fullnameValue.isEmpty() || emailValue.isEmpty() || phoneValue.isEmpty() || passwordValue.isEmpty() || passwordConfirmValue.isEmpty()) {

                    // Affiche un message d'erreur
                    Toast.makeText(Register.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }

                /**
                 * Vérifie que les mots de passe sont identiques
                 */

                else if (!passwordValue.equals(passwordConfirmValue)) {

                    // Affiche un message d'erreur
                    Toast.makeText(Register.this, "Les mots de passe ne sont pas identiques", Toast.LENGTH_SHORT).show();
                }

                // Envoie les données au serveur
                else {

                    /**
                     * Utilise la référence de la base de données pour vérifier si l'utilisateur existe déjà.
                     * Si l'utilisateur n'existe pas, les informations sont enregistrées dans la base de données.
                     * Sinon, un message d'erreur est affiché.
                     */

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // Vérifie que l'utilisateur n'existe pas déjà
                            if (snapshot.hasChild(phoneValue)) {

                                // Affiche un message d'erreur
                                Toast.makeText(Register.this, "L'utilisateur existe déjà", Toast.LENGTH_SHORT).show();
                            }

                            // Sinon on enregistre l'utilisateur dans la base de données
                            else {

                                // Utilise la méthode enfant() pour ajouter les informations de l'utilisateur dans la base de données
                                databaseReference.child("users").child(phoneValue).child("fullname").setValue(fullnameValue);
                                databaseReference.child("users").child(phoneValue).child("email").setValue(emailValue);
                                databaseReference.child("users").child(phoneValue).child("password").setValue(passwordValue);

                                // Affiche un message de confirmation
                                Toast.makeText(Register.this, "Inscription réussie", Toast.LENGTH_SHORT).show();

                                // Retourne à l'activité de connexion

                                finish();
                                startActivity(new Intent(Register.this, Login.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Affiche un message d'erreur
                            Toast.makeText(Register.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        /**
         * Ajoute un listener (écouteur d'évenement) sur le bouton de connexion
         */

        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retourne à l'activité de connexion
                finish();
            }
        });
    }
}


