package cda.flolb.firebasetest;

import static android.media.AudioManager.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_Snake extends Fragment implements SurfaceHolder.Callback{

// -----------------------------------------------------------------------------------------------

    // la liste des points du serpent / la longueur du serpent
    private final List<SnakePoints> snakePointsList = new ArrayList<>();

    private SurfaceView surfaceView;
    private TextView score;

    // Surface holder va nous permettre de dessiner sur la surfaceView le serpent
    private SurfaceHolder surfaceHolder;

    // La couleur de la surfaceHolder
    private int surfaceColor = Color.WHITE;

    // La valeur du mouvement du serpent (droite par defaut)
    private String movingPosition = "right";

    // La valeur du score
    private int scoreValue = 0;

    // La taille du serpent (des points)
    private static final int pointSize = 35;

    // La taille du serpent (nombre de points)
    private static final int defaultTalePoints = 3;

    // La couleur du serpent
    private static final int snakeColor = Color.GREEN;

    // La vitesse du serpent (entre 1 et 800)
    private static final int snakeSpeed = 800;

    // Les position x et y
    private int positionX, positionY;

    // Timer
    private Timer timer;

    // Canvas pour dessiner
    private Canvas canvas = null;

    // La couleur d'un point
    private Paint pointColor = null;

    // Le son
    private MediaPlayer mediaPlayer = null;


// -----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment__snake, container, false);

        // On r??cup??re les ??l??ments de la vue
        surfaceView = v.findViewById(R.id.surfaceView);
        score = v.findViewById(R.id.score);

        // La croix directionnelle
        final AppCompatImageButton topBtn = v.findViewById(R.id.topBtn);
        final AppCompatImageButton bottomBtn = v.findViewById(R.id.bottomBtn);
        final AppCompatImageButton leftBtn = v.findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = v.findViewById(R.id.rightBtn);

        // On ajoute un callback sur la surfaceView
        surfaceView.getHolder().addCallback(this);

        // On ajoute les ??couteurs d'??v??nements sur les boutons
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // si la valeur de movingPosition est diff??rente de bottom
                // on change la valeur de movingPosition en top
                if (!movingPosition.equals("bottom")) {
                    movingPosition = "top";
                }
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("right")) {
                    movingPosition = "left";
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("left")) {
                    movingPosition = "right";
                }
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!movingPosition.equals("top")) {
                    movingPosition = "bottom";
                }
            }
        });

        return v;
    }

// -----------------------------------------------------------------------------------------------

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.surfaceHolder = holder;
        initGame();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

// -----------------------------------------------------------------------------------------------

    private void initGame() {

        // On supprime les points du serpent
        this.snakePointsList.clear();

        // On affiche le score ?? 0
        this.score.setText("0");

        // On initialise la valeur du score ?? 0
        this.scoreValue = 0;

        // On initialise la direction du serpent
        this.movingPosition = "right";

        // On initialise la position du serpent
        int startPositionX = pointSize * defaultTalePoints;

        // Tant que la taille du serpent est inf??rieure ?? la taille par d??faut
        for (int i = 0; i < defaultTalePoints; i++) {

            // On ajoute un point au serpent
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            // On d??cr??mente la position du serpent
            startPositionX = startPositionX - (pointSize * 2);

        }

        // On ajoute un point de mani??re al??atoire
        addPoint();

        // On commence le mouvement du serpent
        moveSnake();

    }

// -----------------------------------------------------------------------------------------------

    private void addPoint() {

        // On r??cup??re la taille de la surfaceView
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        // On g??n??re une position al??atoire
        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if ((randomXPosition % 2) != 0) {

            // On ajoute 1 ?? la position al??atoire
            randomXPosition = randomXPosition + 1;
        }

        if ((randomYPosition % 2) != 0) {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (randomXPosition * pointSize) + pointSize;
        positionY = (randomYPosition * pointSize) + pointSize;
    }

// -----------------------------------------------------------------------------------------------

    private void moveSnake() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                if (headPositionX == positionX && positionY == headPositionY) {
                    growSnake();

                    // On ajoute un point
                    addPoint();
                }

                // On d??place le serpent
                switch (movingPosition) {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;
                    case "top":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "bottom":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                // On v??rifie si le serpent est mort
                if (gameOver(headPositionX, headPositionY)) {
                    // On arr??te le timer
                    timer.purge();
                    timer.cancel();
                    // On affiche le message Game Over
                    AlertDialog.Builder builder = new AlertDialog.Builder(Fragment_Snake.this.getActivity());
                    builder.setMessage("Score : " + scoreValue);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Rejouer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            // On relance le jeu
                            initGame();
                        }
                    });

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });

                } else {

                    canvas = surfaceHolder.lockCanvas();

                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    canvas.drawCircle(snakePointsList.get(0).getPositionX(), snakePointsList.get(0).getPositionY(), pointSize, createPointColor());

                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                    for (int i = 1; i < snakePointsList.size(); i++) {
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);

                        canvas.drawCircle(snakePointsList.get(i).getPositionX(), snakePointsList.get(i).getPositionY(), pointSize, createPointColor());

                        // On met ?? jour la position du serpent
                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

                    // On d??verouille le canvas
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeSpeed, 1000 - snakeSpeed);
    }

// -----------------------------------------------------------------------------------------------

    /**
     * M??thode qui permet de faire grandir le serpent
     */

    private void growSnake() {
        //On cr??e un nouveau serpent
        SnakePoints snakePoints = new SnakePoints(snakePointsList.get(snakePointsList.size() - 1).getPositionX(), snakePointsList.get(snakePointsList.size() - 1).getPositionY());

        // On ajoute le son quand le serpent mange un point
        playSound();

        // On ajoute un point au serpent
        snakePointsList.add(snakePoints);

        // On incr??mente le score
        scoreValue++;

        // On affiche le score
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                score.setText(String.valueOf(scoreValue));
            }
        });
    }

// -----------------------------------------------------------------------------------------------

    private boolean gameOver(int headPositionX, int headPositionY) {
        boolean gameOver = false;

        // On v??rifie si le serpent touche le bord de la surfaceView
        if (snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()) {
            gameOver = true;
        } else {
            // On v??rifie si le serpent se touche lui-m??me
            for (int i = 1; i < snakePointsList.size(); i++) {
                if (headPositionX == snakePointsList.get(i).getPositionX() &&
                        headPositionY == snakePointsList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;
    }

// -------------------------------------------------------------------------------------------------

    private Paint createPointColor() {

        if (pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);

            // On active l'anti aliasing
            pointColor.setAntiAlias(true);

            return pointColor;
        }
        return pointColor;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();
        // On arr??te le timer
        timer.cancel();
        // On arr??te le mouvement du serpent
        movingPosition = "";
    }

    // ---------------------------------------------------------------------------------------------

    // On sort du fragment en cours quand on clique sur un autre fragment

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        utilis?? pour supprimer une valeur stock??e
        sous la cl?? "snake" dans les "pr??f??rences partag??es" (shared preferences)
        avec le nom "PREFERENCE". Le mode priv?? (MODE_PRIVATE) est sp??cifi?? pour garantir que
        les pr??f??rences ne peuvent ??tre accessibles qu'?? l'application elle-m??me. La m??thode "apply()"
        est appel??e pour appliquer la modification de mani??re asynchrone.
         */
        getContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().remove("snake").apply();
    }

    // ---------------------------------------------------------------------------------------------

    // On sort du fragment en cours quand on clique sur le bouton retour du t??l??phone

    @Override
    public void onStop() {
        super.onStop();
        // On arr??te le timer
        timer.cancel();
        // On arr??te le mouvement du serpent
        movingPosition = "";
    }

    // ---------------------------------------------------------------------------------------------

    // On ajoute un son quand le serpent mange la pomme avec la classe MediaPlayer

    private void playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.son);
            mediaPlayer.start();
        } else {
            mediaPlayer.start();
        }
    }
}