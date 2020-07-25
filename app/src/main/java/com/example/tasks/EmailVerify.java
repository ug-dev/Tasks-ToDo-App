package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EmailVerify extends AppCompatActivity {
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), TaskLists.class));
                finish();
            } else {
                FirebaseAuth.getInstance().signOut();
            }
        }
    }
}
