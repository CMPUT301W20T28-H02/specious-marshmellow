package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TicTacToeActivity extends AppCompatActivity {


    // 0 = goku, 1 = vegeta
    int activePlayer = 0;

    // keeps track of whether or not to allow additional moves, depending on whether
    // the game has been decided or not
    boolean gameIsActive = true;

    // 2 means empty cell
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    int[][] winningPositions
            = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);






    }

    public void dropIn(View view) {

        ImageView sprite = (ImageView) view;

        int tappedCell = Integer.parseInt(sprite.getTag().toString());

        // allow sprite to be dropped if cell is empty and game has not been decided
        if (gameState[tappedCell] == 2 && gameIsActive) {
            gameState[tappedCell] = activePlayer;

            sprite.setTranslationY(-1000f);

            if (activePlayer == 0) {
                sprite.setImageResource(R.mipmap.marshmellow_transparent_m);
                activePlayer = 1;
            } else {
                sprite.setImageResource(R.mipmap.marshmellow_transparent);
                activePlayer = 0;
            }

            sprite.animate().translationYBy(1000f).setDuration(300);

            // loops through winning positions nested array and checks to see if someone has won
            // by comparing the gameState array with each possible winning position
            for (int[] winningPosition : winningPositions) {
                if (gameState[winningPosition[0]] == gameState[winningPosition[1]]
                        && gameState[winningPosition[1]] == gameState[winningPosition[2]]
                        && gameState[winningPosition[0]] != 2) {

                    gameIsActive = false;

                    String winner = "Bold";

                    if (gameState[winningPosition[0]] == 0) {
                        winner = "Clear";
                    }

                    TextView winnerMessage = findViewById(R.id.winnerMessage);
                    winnerMessage.setText(winner + " has won!");
                    FrameLayout layout = findViewById(R.id.playAgainLayout);
                    layout.setTranslationX(-1000f);
                    layout.setVisibility(View.VISIBLE);
                    layout.animate().translationXBy(1000f).setDuration(300);

                    // Break is needed in order to stop for loop from checking other winning positions
                    // and possibly reporting a false draw
                    break;

                } else {

                    boolean gameIsOver = true;

                    for (int counterState : gameState) {


                        if (counterState == 2) gameIsOver = false;
                    }

                    if (gameIsOver) {

                        TextView winnerMessage = findViewById(R.id.winnerMessage);
                        winnerMessage.setText("It's a draw!");

                        FrameLayout layout = findViewById(R.id.playAgainLayout);
                        layout.setTranslationX(-1000f);


                        layout.setVisibility(View.VISIBLE);
                        layout.animate().translationXBy(1000f).setDuration(300);

                    }
                }

            }
        }
    }

    public void playAgain(View view){
        gameIsActive = true;

        FrameLayout layout = findViewById(R.id.playAgainLayout);
        layout.setVisibility(View.INVISIBLE);

        //reset turn to goku
        activePlayer = 0;

        // update gamestate array to signify that all cells are empty
        for (int i =0; i<gameState.length; i++){
            gameState[i] = 2;
        }

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // make sprite images temporary invisible
        for(int i =0; i<gridLayout.getChildCount(); i++){
            ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
        }


    }

    public void backToRide(View view){

        String username = getIntent().getStringExtra("username");
        Intent i = new Intent(getBaseContext(), RiderEndAndPay.class);
        i.putExtra("username", username);
        startActivity(i);

    }
}
