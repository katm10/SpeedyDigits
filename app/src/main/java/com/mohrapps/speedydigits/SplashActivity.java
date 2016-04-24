package com.mohrapps.speedydigits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;

public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SplashActivity";
    private static final int RC_SIGN_IN = 9001;

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
        findViewById(R.id.signinbutton).setOnClickListener(this);
        findViewById(R.id.signoutbutton).setOnClickListener(this);
        findViewById(R.id.disconnectbutton).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
              //  .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
               // .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Games.API)
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
        SignInButton signInButton = (SignInButton) findViewById(R.id.signinbutton);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
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
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.signinbutton).setVisibility(View.GONE);
            findViewById(R.id.signoutbutton).setVisibility(View.VISIBLE);
            findViewById(R.id.disconnectbutton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
            findViewById(R.id.signoutbutton).setVisibility(View.GONE);
            findViewById(R.id.disconnectbutton).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinbutton:
                signIn();
                break;
            case R.id.signoutbutton:
                signOut();
                break;
            case R.id.disconnectbutton:
                revokeAccess();
                break;
        }
    }

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
        startActivity(intent);
    }

    public void openAchievements(View v) {
        if (mGoogleApiClient.getConnectionResult(Auth.GOOGLE_SIGN_IN_API).isSuccess()) {
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
        if (mGoogleApiClient.getConnectionResult(Auth.GOOGLE_SIGN_IN_API).isSuccess()) {
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









