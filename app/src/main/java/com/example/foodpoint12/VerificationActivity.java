package com.example.foodpoint12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    LinearLayout b2;
    EditText[] otpEditTexts = new EditText[6]; // Array to hold OTP EditTexts
    ImageView btnBack1;
    String phonenumber;
    FirebaseAuth mAuth;
    String otpid;
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varification);

                // Initialize OTP EditTexts
                otpEditTexts[0] = findViewById(R.id.otpEditText1);
                otpEditTexts[1] = findViewById(R.id.otpEditText2);
                otpEditTexts[2] = findViewById(R.id.otpEditText3);
                otpEditTexts[3] = findViewById(R.id.otpEditText4);
                otpEditTexts[4] = findViewById(R.id.otpEditText5);
                otpEditTexts[5] = findViewById(R.id.otpEditText6);

                b2 = findViewById(R.id.b2);
                mAuth = FirebaseAuth.getInstance();
                phonenumber = getIntent().getStringExtra("mobile").toString();

                btnBack1 = findViewById(R.id.btnBack1);
                btnBack1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(VerificationActivity.this, SignUpActivity.class);
                        startActivity(i);
                    }
                });

                initiateotp();
        setupOtpEditTextListeners();

                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Concatenate the text from OTP EditTexts to form the OTP
                        StringBuilder otp = new StringBuilder();
                        for (EditText editText : otpEditTexts) {
                            String digit = editText.getText().toString().trim();
                            if (digit.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Blank Field can not be processed", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (digit.length() != 1) {
                                Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            otp.append(digit);
                        }

                        // Use the concatenated OTP for verification
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, otp.toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                });
            }

            private void initiateotp() {
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phonenumber)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this)                 // (optional) Activity for callback binding
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }

            private PhoneAuthProvider.OnVerificationStateChangedCallbacks
                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerificationActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String s,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    otpid = s;
                }
            };

            private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(VerificationActivity.this, ProfileActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(VerificationActivity.this, "Signin code error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }






    private void setupOtpEditTextListeners() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int currentIndex = i;
            final int nextIndex = (i == otpEditTexts.length - 1) ? -1 : i + 1; // Disable next for the last EditText

            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0 && nextIndex != -1) {
                        otpEditTexts[nextIndex].requestFocus();
                    }
                }
            });

            // Handle backspace to navigate to the previous EditText
            otpEditTexts[i].setOnKeyListener((view, keyCode, event) -> {
                if (keyCode == 67 && currentIndex > 0) {
                    otpEditTexts[currentIndex - 1].requestFocus();
                }
                return false;
            });
        }
    }



}
