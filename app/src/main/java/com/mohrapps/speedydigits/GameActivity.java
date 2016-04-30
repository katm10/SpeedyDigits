package com.mohrapps.speedydigits;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static double milliTilDone;
    private static final int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        final TextView gameOver = (TextView) findViewById(R.id.gameOverView);
        final Button homeButton = (Button) findViewById(R.id.homeButton);
        final FrameLayout congrats = (FrameLayout) findViewById(R.id.congrats);
        final TextView pointView = (TextView) findViewById(R.id.points);
        final TextView timerView = (TextView) findViewById(R.id.timerTextView);
        final TextView topPoints = (TextView) findViewById(R.id.topPoints);


        Button b1 = (Button) findViewById(R.id.button);
        Button b2 = (Button) findViewById(R.id.button2);
        Button b3 = (Button) findViewById(R.id.button3);
        Button b4 = (Button) findViewById(R.id.button4);
        Button b5 = (Button) findViewById(R.id.button5);
        Button b6 = (Button) findViewById(R.id.button6);
        Button b7 = (Button) findViewById(R.id.button7);
        Button b8 = (Button) findViewById(R.id.button8);
        Button b9 = (Button) findViewById(R.id.button9);
        Button b10 = (Button) findViewById(R.id.button10);
        Button b11 = (Button) findViewById(R.id.button11);
        Button b12 = (Button) findViewById(R.id.button12);
        //array with all the buttons
        final Button[] buttons = {
                b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12};

        Button[] click = buttonSet(4, buttons);


        //-------------------------------------------------
        CountDownTimer timer = new CountDownTimer(10000, 50) {

            public void onTick(long millisUntilFinished) {
                milliTilDone = (double) millisUntilFinished;
                timerView.setText(new DecimalFormat("#.#").format(milliTilDone / 1000));
            }

            public void onFinish() {
                for (int b = 0; b < 12; b++) {
                    buttons[b].setClickable(false);
                }
                gameOver.setVisibility(View.VISIBLE);
                gameOver.setText("You ran out of time!");
                gameOver.bringToFront();
                homeButton.setVisibility(View.VISIBLE);
            }
        }.start();
        //---------------------------------------------------

        checkClick(buttons, click, 0, 4, gameOver, homeButton, congrats, 1, pointView, topPoints, timerView, timer);
    }

    //~~~~~~~~~~~~~~~~~~~~~Start of all the crazy google api stuff~~~~~~~~~~~~~~~~~
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // findViewById(R.id.signinbutton).setVisibility(View.GONE);
        // findViewById(R.id.signoutbutton).setVisibility(View.VISIBLE);
        //   findViewById(R.id.disconnectbutton).setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
        }
        if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Error")) {
            mResolvingConnectionFailure = false;
        }
        /*while(!connectionResult.isSuccess()){
            findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
            findViewById(R.id.signoutbutton).setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, resultCode, requestCode, R.string.sign_in_failed);
            }
        }
    }

    //~~~~~~~~~~~~~~~~~end of crazy api stuff~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void goHome(View view) {
        Intent intent = new Intent(GameActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    private static boolean contains(ArrayList<Integer> array1, int num) {
        for (int i = 0; i < array1.size(); i++) {
            if (array1.get(i) == num) {
                return true;
            }
        }
        return false;
    }

    private static int random(int max, ArrayList<Integer> array) {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(max);

        if ((contains(array, randomInt))) {
            return random(max, array);
        } else {
            return randomInt;
        }
    }


    public static Button[] buttonSet(int count, Button[] buttons) {

        //resetting buttons
        for (int i = 0; i < 12; i++) {
            buttons[i].setText(null);
            buttons[i].getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
        //click stores the order in which the buttons must be pressed
        final Button[] click = new Button[count - 1];
        final ArrayList<Integer> list = new ArrayList<>();
        // this displays numbers from 1 to count-1 on random buttons and records those buttons in click[]
        for (
                int x = 1;
                x < count; x++)

        {
            int randomNum = random(12, list);
            buttons[randomNum].setText(Integer.toString(x));
            buttons[randomNum].getBackground().setColorFilter(Color.rgb(179, 255, 255), PorterDuff.Mode.MULTIPLY);
            click[x - 1] = buttons[randomNum];
            list.add(randomNum);

        }

        return click;
    }


    public void checkClick(final Button[] buttons, final Button[] click,
                           final int clickNum, final int count, final TextView gameOver,
                           final Button homeButton, final FrameLayout congrats, final int points,
                           final TextView pointView, final TextView topPoints, final TextView timerView,
                           final CountDownTimer timer) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switch (count) {
                case 9:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_you_are_doing_greight));
                    break;
                case 10:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_youre_doing_nine_fine));
                    break;
                case 11:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_youre_a_perfect_ten));
                    break;
                case 12:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_these_go_to_eleven));
                    break;
                case 13:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_you_beat_the_game));
                    break;
            }


        }


        if (click.length == clickNum) {
            pointView.setVisibility(View.VISIBLE);
            pointView.bringToFront();

            int newpoints = ((count - 1) * 100) + (((int) milliTilDone) / 100);

            pointView.setText("+" + Integer.toString(newpoints));
            final int nowPoints = newpoints + points;


            topPoints.setText("Points:" + (nowPoints));
            Button[] newClick = buttonSet(count + 1, buttons);
            timer.cancel();

            //timer is timer from previous level
            // ------------------------------------------------------------------------------------------
            //newTimer starts a new timer in top right

            CountDownTimer newTimer = new CountDownTimer(10000, 50) {

                public void onTick(long millisUntilFinished) {
                    milliTilDone = (double) millisUntilFinished;
                    timerView.setText(new DecimalFormat("#.#").format(milliTilDone / 1000));
                }

                public void onFinish() {
                    for (int b = 0; b < 12; b++) {
                        buttons[b].setClickable(false);
                    }
                    gameOver.setVisibility(View.VISIBLE);
                    gameOver.setText("You ran out of time! " + gameOver.getText() + " " + Integer.toString(nowPoints) + " points!");
                    gameOver.bringToFront();
                    homeButton.setVisibility(View.VISIBLE);
                }
            }.start();

            //-------------------------------------------------------------------------------------------

            checkClick(buttons, newClick, 0, count + 1, gameOver, homeButton, congrats, nowPoints, pointView, topPoints, timerView, newTimer);

        } else {
            for (int x = 0; x < 12; x++) {
                final int j = x;
                buttons[j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 12; i++) {
                            buttons[i].setText(null);
                        }
                        pointView.setVisibility(View.INVISIBLE);
                        if (buttons[j] == click[clickNum] && clickNum < 11) {
                            buttons[j].getBackground().setColorFilter(Color.rgb(128, 255, 128), PorterDuff.Mode.MULTIPLY);
                            checkClick(buttons, click, clickNum + 1, count, gameOver, homeButton, congrats, points, pointView, topPoints, timerView, timer);
                        } else if (buttons[j] == click[clickNum] && clickNum == 11) {
                            buttons[j].getBackground().setColorFilter(Color.rgb(128, 255, 128), PorterDuff.Mode.MULTIPLY);
                            congrats.setVisibility(View.VISIBLE);
                            congrats.bringToFront();
                            homeButton.setVisibility(View.VISIBLE);
                            timer.cancel();
                        } else {
                            buttons[j].getBackground().setColorFilter(Color.rgb(255, 77, 77), PorterDuff.Mode.MULTIPLY);
                            for (int b = 0; b < 12; b++) {
                                buttons[b].setClickable(false);
                            }
                            gameOver.setVisibility(View.VISIBLE);
                            gameOver.setText(gameOver.getText() + " " + Integer.toString(points) + " points!");
                            gameOver.bringToFront();
                            homeButton.setVisibility(View.VISIBLE);
                            timer.cancel();
                            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_speedy_digits_leaderboard), points);
                        }
                    }


                });

            }
        }
    }

}
