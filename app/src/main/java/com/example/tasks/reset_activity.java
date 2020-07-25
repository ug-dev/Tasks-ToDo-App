package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class reset_activity extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button resetButton;
    private EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot password?");

        progressBar = findViewById(R.id.reset_password_progressBar);
        inputEmail = findViewById(R.id.input_reset_email);
        resetButton = findViewById(R.id.reset_account_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Hiding Soft Keyboard
                inputEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);

                //Check Internet Connectivity
                if (isNetworkAvailable()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String email = inputEmail.getText().toString().trim();

                    if (!TextUtils.isEmpty(email)) {

                        if (checkValidation(email)) {
                            progressBar.setVisibility(View.VISIBLE);
                            resetButton.setVisibility(View.INVISIBLE);

                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                resetButton.setVisibility(View.VISIBLE);
                                                Snackbar.make(v, "Reset Password Link Sent!", Snackbar.LENGTH_LONG)
                                                        .setTextColor(getResources().getColor(R.color.black))
                                                        .setBackgroundTint(getResources().getColor(R.color.white))
                                                        .show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            resetButton.setVisibility(View.VISIBLE);
                                            Snackbar.make(v, ""+e.getMessage(), Snackbar.LENGTH_LONG)
                                                    .setTextColor(getResources().getColor(R.color.black))
                                                    .setBackgroundTint(getResources().getColor(R.color.white))
                                                    .show();
                                        }
                                    });
                        } else {
                            Snackbar.make(v, "Invalid email address!", Snackbar.LENGTH_SHORT)
                                    .setTextColor(getResources().getColor(R.color.black))
                                    .setBackgroundTint(getResources().getColor(R.color.white))
                                    .show();
                        }
                    } else {
                        Snackbar.make(v, "Please Enter Email!", Snackbar.LENGTH_SHORT)
                                .setTextColor(getResources().getColor(R.color.black))
                                .setBackgroundTint(getResources().getColor(R.color.white))
                                .show();
                    }
                } else {
                    Snackbar.make(v, "Check Your Internet!", Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.black))
                            .setBackgroundTint(getResources().getColor(R.color.white))
                            .show();
                }
            }
        });
    }

    public boolean checkValidation(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
