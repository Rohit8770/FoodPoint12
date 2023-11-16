package com.example.foodpoint12;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;

public class SignUpActivity extends AppCompatActivity {

    LinearLayout btnSignIn1;
    TextView btnSignUp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignIn1=findViewById(R.id.btnSignIn1);

        btnSignIn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(SignUpActivity.this, PhoneActivity.class);
                startActivity(i);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}