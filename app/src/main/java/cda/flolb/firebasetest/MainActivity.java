package cda.flolb.firebasetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {

        // On intialise le fragment à null
        Fragment selectedFragment = null;

        // On initialise le fragment en fonction de l'item sélectionné
        int itemId = item.getItemId();

        // Si l'item sélectionné est l'item 1
        if (itemId == R.id.home) {

            // On crée une nouvelle instance de Fragment_Home
            selectedFragment = new Fragment_Home();

        } else if (itemId == R.id.snake) {

            // On crée une nouvelle instance de Fragment_Snake
            selectedFragment = new Fragment_Snake();

        } else if (itemId == R.id.quiz) {

            // On crée une nouvelle instance de Fragment_Quiz
            selectedFragment = new Fragment_Quiz();
        }

        // Si le fragment n'est pas null
        if (selectedFragment != null) {

            // On remplace le fragment actuel par le fragment sélectionné
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        // On initialise le fragment par défaut
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Home()).commit();
    }
}



