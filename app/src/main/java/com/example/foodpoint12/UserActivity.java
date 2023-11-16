package com.example.foodpoint12;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    LinearLayout googleAuth;
    LinearLayout facebookAuth;
    FirebaseAuth auth;
    TextView btnStart;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int Sign_In = 20;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    private Arrays Array;
    //  FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        facebookAuth=findViewById(R.id.facebookAuth);
        googleAuth = findViewById(R.id.googleAuth);
        btnStart=findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);



       // database = FirebaseDatabase.getInstance("https://foodpoint12-6fa5f-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();

        googleSignInClient =GoogleSignIn.getClient(this, gso);
        AppEventsLogger.activateApp(this.getApplication());

        facebookAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookSignIn();
            }
        });



        if (auth.getCurrentUser()!=null){
            FirebaseUser user = auth.getCurrentUser();
            Intent intent =new Intent(UserActivity.this,ProfileActivity.class);
            intent.putExtra("user_name", user.getDisplayName());
            intent.putExtra("user_email", user.getEmail());
           // intent.putExtra("user_photo", user.getPhotoUrl().toString());
            if (user.getPhotoUrl() != null) {
                intent.putExtra("user_photo", user.getPhotoUrl().toString());
            } else {
                intent.putExtra("user_photo", ""); // or any other default value or handling you prefer
            }

            startActivity(intent);
        }else {

            googleAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "GOOGLE LOGIN", Toast.LENGTH_SHORT).show();
                    if (auth.getCurrentUser() != null) {
                        auth.signOut();
                    }
                    clearGoogleAccountInfo();
                }
            });
        }
    }
    private void facebookSignIn(){
        LoginManager.getInstance().logInWithReadPermissions(this,Array.asList( "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //  Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }


    private void clearGoogleAccountInfo() {
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if (account!=null){
            googleSignInClient.revokeAccess()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                            }else{
                            }
                            googleSignIn();
                        }
                    });
        }else {
            googleSignIn();
        }
    }
    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, Sign_In);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Sign_In) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("email", user.getEmail());
                            map.put("name", user.getDisplayName());
                            map.put("profile", user.getPhotoUrl().toString());
                            database.getReference().child("users").child(user.getUid()).setValue(map);
                            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
                            intent.putExtra("user_name", user.getDisplayName());
                            intent.putExtra("user_email", user.getEmail());
                            intent.putExtra("user_photo", user.getPhotoUrl().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(UserActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
      //  Log.d("TAG", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("email", user.getEmail());
                            map.put("name", user.getDisplayName());
                            map.put("profile", user.getPhotoUrl().toString());
                            database.getReference().child("users").child(user.getUid()).setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> databaseTask) {
                                            if (databaseTask.isSuccessful()) {
                                                Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
                                                intent.putExtra("user_name", user.getDisplayName());
                                                intent.putExtra("user_email", user.getEmail());
                                                intent.putExtra("user_photo", user.getPhotoUrl().toString());
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                             //   Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                Toast.makeText(UserActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(UserActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }
}

