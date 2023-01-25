package cda.flolb.firebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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

    // La vitesse du serpent (entre 1 et 1000)
    private static final int snakeSpeed = 800;

    // Les position x et y
    private int positionX, positionY;

    // Timer
    private Timer timer;

    // Canvas pour dessiner
    private Canvas canvas = null;

    // La couleur d'un point
    private Paint pointColor = null;


// -----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment__snake, container, false);

        // On récupère les éléments de la vue
        surfaceView = v.findViewById(R.id.surfaceView);
        score = v.findViewById(R.id.score);

        // La croix directionnelle
        final AppCompatImageButton topBtn = v.findViewById(R.id.topBtn);
        final AppCompatImageButton bottomBtn = v.findViewById(R.id.bottomBtn);
        final AppCompatImageButton leftBtn = v.findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = v.findViewById(R.id.rightBtn);

        // On ajoute un callback sur la surfaceView
        surfaceView.getHolder().addCallback(this);

        // On ajoute les écouteurs d'événements sur les boutons
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // si la valeur de movingPosition est différente de bottom
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

        // On affiche le score à 0
        this.score.setText("0");

        // On initialise la valeur du score à 0
        this.scoreValue = 0;

        // On initialise la direction du serpent
        this.movingPosition = "right";

        // On initialise la position du serpent
        int startPositionX = pointSize * defaultTalePoints;

        // Tant que la taille du serpent est inférieure à la taille par défaut
        for (int i = 0; i < defaultTalePoints; i++) {

            // On ajoute un point au serpent
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            // On décrémente la position du serpent
            startPositionX = startPositionX - (pointSize * 2);

        }

        // On ajoute un point de manière aléatoire
        addPoint();

        // On commence le mouvement du serpent
        moveSnake();

    }

// -----------------------------------------------------------------------------------------------

    private void addPoint() {

        // On récupère la taille de la surfaceView
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        // On génère une position aléatoire
        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if ((randomXPosition % 2) != 0) {

            // On ajoute 1 à la position aléatoire
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

                // On déplace le serpent
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

                // On vérifie si le serpent est mort
                if (gameOver(headPositionX, headPositionY)) {
                    // On arrête le timer
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

                        // On met à jour la position du serpent
                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

                    // On déverouille le canvas
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeSpeed, 1000 - snakeSpeed);
    }

// -----------------------------------------------------------------------------------------------

    /**
     * Méthode qui permet de faire grandir le serpent
     */

    private void growSnake() {
        //On crée un nouveau serpent
        SnakePoints snakePoints = new SnakePoints(snakePointsList.get(snakePointsList.size() - 1).getPositionX(), snakePointsList.get(snakePointsList.size() - 1).getPositionY());

        // On ajoute un point au serpent
        snakePointsList.add(snakePoints);

        // On incrémente le score
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

        // On vérifie si le serpent touche le bord de la surfaceView
        if (snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()) {
            gameOver = true;
        } else {
            // On vérifie si le serpent se touche lui-même
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

// -----------------------------------------------------------------------------------------------

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

}