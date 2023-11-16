package com.example.foodpoint12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText t1;
    LinearLayout b1;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        mAuth = FirebaseAuth.getInstance();
        t1 = findViewById(R.id.t1);
        b1 = findViewById(R.id.b1);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(t1);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneActivity.this, VerificationActivity.class);
                intent.putExtra("mobile", ccp.getFullNumberWithPlus().replace("", ""));
                startActivity(intent);
            }
        });
    }
}