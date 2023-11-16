package com.example.foodpoint12;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {


    Button btnLogout;
    FirebaseAuth auth;
    TextView txt1,txt2;
    ImageView img,img2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth=FirebaseAuth.getInstance();
        btnLogout=findViewById(R.id.btnLogout);

        //google
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        img = findViewById(R.id.img);
        //facebook

        /*//facebook
        String name = getIntent().getStringExtra("name");
        fbtxt1.setText(name);*/

        //google

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();

                Intent i=new Intent(ProfileActivity.this,UserActivity.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });


            String userName = getIntent().getStringExtra("user_name");
            String userEmail = getIntent().getStringExtra("user_email");
            String userPhoto = getIntent().getStringExtra("user_photo");

            txt1.setText("User Name :" + userName);
            txt2.setText("User Email :" + userEmail);

            Glide
                    .with(this)
                    .load(userPhoto)
                    .into(img);
        }
       /* //google
        Intent intent1 = getIntent();
        if (intent1 != null) {

            String userName = intent.getStringExtra("user_name");
            String userEmail = intent.getStringExtra("user_email");
            String userPhoto = intent.getStringExtra("user_photo");

            fbtxt1.setText("User Name :" + userName);
            fbtxt2.setText("User Email :" + userEmail);

            Glide
                    .with(this)
                    .load(userPhoto)
                    .into(img2);
        }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


