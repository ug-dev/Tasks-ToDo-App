package com.example.tasks;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class register_activity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    private EditText userEmail;
    private EditText userPassword;
    private EditText userName;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        userEmail = findViewById(R.id.email_acct);
        userPassword = findViewById(R.id.password_acct);
        userName = findViewById(R.id.name_acct);
        signUpButton = findViewById(R.id.acct_signup_button);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
            }
        };

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding Soft Keyboard
                userPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);
                userEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                userName.onEditorAction(EditorInfo.IME_ACTION_DONE);

                //Check Internet Connectivity
                if (isNetworkAvailable()) {
                    if (!TextUtils.isEmpty(userEmail.getText().toString())
                            && !TextUtils.isEmpty(userName.getText().toString())
                            && !TextUtils.isEmpty(userPassword.getText().toString())) {

                        String email = userEmail.getText().toString().trim();
                        String password = userPassword.getText().toString().trim();
                        String username = userName.getText().toString().trim();

                        if (checkValidation(email, password)) {
                            createUserEmailAccount(email, password, username);
                        } else {
                            Snackbar.make(v, "Enter Valid Details", Snackbar.LENGTH_SHORT)
                                    .setTextColor(getResources().getColor(R.color.black))
                                    .setBackgroundTint(getResources().getColor(R.color.white))
                                    .show();
                        }
                    } else {
                        Snackbar.make(v, "Please Enter Details", Snackbar.LENGTH_SHORT)
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

    private void createUserEmailAccount(final String email, final String password, final String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {

            signUpButton.setText(R.string.signing_up);
            signUpButton.setTextColor(getResources().getColor(R.color.text_color_2));
            signUpButton.setEnabled(false);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Send Verification Email to user
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //We take user to MainActivity
                                                    currentUser = firebaseAuth.getCurrentUser();
                                                    assert currentUser != null;
                                                    final String currentUserId = currentUser.getUid();

                                                    //Create a user Map so we can create a user in the UserCollection
                                                    Map<String, String> userObj = new HashMap<>();
                                                    userObj.put("userId", currentUserId);
                                                    userObj.put("userName", username);
                                                    userObj.put("userEmail", email);
                                                    userObj.put("userPassword", password);

                                                    //Save to Database
                                                    documentReference = db.collection("users").document(email);
                                                    documentReference.set(userObj)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    UserData userData = UserData.getInstance();
                                                                    userData.setUserId(currentUserId);
                                                                    userData.setUserEmail(email);
                                                                    userData.setUserName(username);

                                                                    Util.USER_LOGGED = true;

                                                                    DatabaseHandler handler = new DatabaseHandler
                                                                            (register_activity.this);
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

                                                                    Intent intent = new Intent(getApplicationContext(),
                                                                            EmailVerify.class);
                                                                    startActivity(intent);
                                                                    finish();

                                                                    signUpButton.setText(R.string.sign_up_button);
                                                                    signUpButton.setTextColor(getResources()
                                                                            .getColor(R.color.text_color_1));
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Email Already Used!",
                                    Toast.LENGTH_SHORT).show();
                            signUpButton.setText(R.string.sign_up_button);
                            signUpButton.setEnabled(true);
                            signUpButton.setTextColor(getResources()
                                    .getColor(R.color.text_color_1));
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
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
        startActivity(new Intent(register_activity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(register_activity.this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
