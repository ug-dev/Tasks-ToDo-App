package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class login_activity extends AppCompatActivity {
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    TextView reset_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        firebaseAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.input_email);
        loginPassword = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.account_login_button);

        reset_button = findViewById(R.id.input_forgot_pwd);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), reset_activity.class));
            }
        });
        
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding Soft Keyboard
                loginPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                loginEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);

                //Check Internet Connectivity
                if (isNetworkAvailable()) {
                    if (!TextUtils.isEmpty(loginEmail.getText().toString().trim())
                            && !TextUtils.isEmpty(loginPassword.getText().toString().trim())) {

                        if (checkValidation(loginEmail.getText().toString().trim(),
                                loginPassword.getText().toString().trim())) {

                            loginEmailPasswordUser(loginEmail.getText().toString().trim(),
                                    loginPassword.getText().toString().trim());
                        } else {
                            Snackbar.make(v, "Invalid Email or Password!", Snackbar.LENGTH_SHORT)
                                    .setTextColor(getResources().getColor(R.color.black))
                                    .setBackgroundTint(getResources().getColor(R.color.white))
                                    .show();
                        }
                    } else {
                        Snackbar.make(v, "Please Enter Details!", Snackbar.LENGTH_SHORT)
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

    private void loginEmailPasswordUser(String email, String password) {
        loginButton.setText(R.string.logging_in);
        loginButton.setTextColor(getResources().getColor(R.color.text_color_2));
        loginButton.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            final String currentUserId = user.getUid();

                            //Find user in DataBase so we can get their Data..
                            collectionReference
                                    .whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {

                                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                    UserData userData = UserData.getInstance();
                                                    userData.setUserId(currentUserId);
                                                    userData.setUserName(snapshot.getString("userName"));
                                                    userData.setUserEmail(snapshot.getString("userEmail"));
                                                }

                                                DatabaseHandler handler = new DatabaseHandler(login_activity.this);
                                                ArrayList<String> arrayList = new ArrayList<>();
                                                arrayList.add("Theme Color 1");
                                                arrayList.add("Theme Color 2");
                                                arrayList.add("Theme Color 3");

                                                handler.addThemeColor(arrayList);

                                                ArrayList<String> items = new ArrayList<>();
                                                items.add("true");
                                                items.add("false");
                                                items.add("true");
                                                items.add("false");
                                                items.add("true");
                                                items.add("true");
                                                items.add("false");
                                                items.add("true");
                                                items.add("My Day");
                                                items.add("Delete");

                                                handler.addSettings(items);

                                                Util.USER_LOGGED = true;
                                                loginButton.setText(R.string.login_button);
                                                loginButton.setTextColor(getResources().getColor(R.color.text_color_1));

                                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginButton.setText(R.string.login_button);
                        loginButton.setEnabled(true);
                        loginButton.setTextColor(getResources().getColor(R.color.text_color_1));
                        Toast.makeText(getApplicationContext(), "Invalid Email or Password!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean checkValidation(String email, String password) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern)) {
            return false;
        } else return password.length() >= 6;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(login_activity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(login_activity.this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
