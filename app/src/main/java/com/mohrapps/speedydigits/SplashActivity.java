package com.mohrapps.speedydigits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SplashActivity";
    private static final int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private int REQUEST_ACHIEVEMENT = 2648;
    private int REQUEST_LEADERBOARD = 6248;
    public GoogleSignInResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Button listeners
        findViewById(R.id.signinbutton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });
        findViewById(R.id.signoutbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInClicked = false;
                mGoogleApiClient.disconnect();
                findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
                findViewById(R.id.signoutbutton).setVisibility(View.GONE);
            }
        });

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]
*/
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //  .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                // .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        //   SignInButton signInButton = (SignInButton) findViewById(R.id.signinbutton);
        //   signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
       /* OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        findViewById(R.id.signinbutton).setVisibility(View.GONE);
        findViewById(R.id.signoutbutton).setVisibility(View.VISIBLE);
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
        while (!connectionResult.isSuccess()) {
            findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
            findViewById(R.id.signoutbutton).setVisibility(View.GONE);
        }
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
 /*   public void signInClicked(View v){
        mSignInClicked=true;
        mGoogleApiClient.connect();
        //TODO:this doesn't work :(
    }
    public void signOutClicked(View v){
        mSignInClicked=false;
        mGoogleApiClient.disconnect();
        findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
        findViewById(R.id.signoutbutton).setVisibility(View.GONE);
    }*/
    // public GoogleApiClient getmGoogleApiClient(){
    //    return mGoogleApiClient;
    // }

    public boolean helpIsPressed = false;

    public void openHelp(View v) {
        Button start = (Button) findViewById(R.id.start);
        FrameLayout help = (FrameLayout) findViewById(R.id.helplayout);
        if (!helpIsPressed) {
            helpIsPressed = true;
            help.setVisibility(View.VISIBLE);
            start.setVisibility(View.GONE);
        } else {
            help.setVisibility(View.GONE);
            helpIsPressed = false;
            start.setVisibility(View.VISIBLE);
        }
    }

    public void closeHelp(View view) {
        Button start = (Button) findViewById(R.id.start);
        FrameLayout help = (FrameLayout) findViewById(R.id.helplayout);
        help.setVisibility(View.GONE);
        helpIsPressed = false;
        start.setVisibility(View.VISIBLE);
    }


    public void startGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        boolean clientIsConnected = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            clientIsConnected = true;
        }
        intent.putExtra("clientIsConnected", clientIsConnected);
        startActivity(intent);
    }

    public void openAchievements(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    REQUEST_ACHIEVEMENT);
        } else {
            findViewById(R.id.unavailable).setVisibility(View.VISIBLE);
            findViewById(R.id.splashLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.unavailable).setVisibility(View.GONE);
                }
            });
        }

    }


    public void openLeaderboard(View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    getString(R.string.leaderboard_speedy_digits_leaderboard)), REQUEST_LEADERBOARD);
        } else {
            findViewById(R.id.leadUnavailable).setVisibility(View.VISIBLE);
            findViewById(R.id.splashLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.leadUnavailable).setVisibility(View.GONE);
                }
            });
        }

    }
}









