package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class Account extends AppCompatActivity {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private TextView save_button;
    private TextView cancel_button;
    private EditText user_name;

    private TextView account_user_title, account_user_subtitle;

    UserData userData;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userData = UserData.getInstance();

        progressBar = findViewById(R.id.account_progressBar);

        account_user_title = findViewById(R.id.account_user_title);
        account_user_subtitle = findViewById(R.id.account_user_subtitle);

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        CardView changeNameButton = findViewById(R.id.change_name_button);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                changeUserName();
            }
        });

        CardView changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isNetworkAvailable()) {
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(userData.getUserEmail())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);

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
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(v, "Maximum attempts reached! Please check inbox.",
                                            Snackbar.LENGTH_LONG)
                                            .setTextColor(getResources().getColor(R.color.white))
                                            .setBackgroundTint(getResources().getColor(R.color.reset))
                                            .show();
                                }
                            });
                } else {
                    Snackbar.make(v, "Check Your Internet!", Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.black))
                            .setBackgroundTint(getResources().getColor(R.color.white))
                            .show();
                }
            }
        });

        final CardView signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    AlertDialog.Builder b = new AlertDialog.Builder(Account.this);
                    b.setTitle("Log Out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    signOutUser();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Snackbar.make(v, "Check Your Internet!", Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.black))
                            .setBackgroundTint(getResources().getColor(R.color.white))
                            .show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        account_user_title.setText(userData.getUserName());
        account_user_subtitle.setText(userData.getUserEmail());
    }

    private void changeUserName() {
        final UserData userData = UserData.getInstance();

        builder = new AlertDialog.Builder(Account.this);
        @SuppressLint("InflateParams") final View view = getLayoutInflater()
                .inflate(R.layout.change_name, null);
        user_name = view.findViewById(R.id.user_name_title);
        user_name.setText(userData.getUserName());

        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(user_name.getText().toString().trim())) {
                    save_button.setTextColor(getResources().getColor(R.color.white));
                } else {
                    save_button.setTextColor(getResources().getColor(R.color.background_color_3));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        cancel_button = view.findViewById(R.id.cancel_name_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save_button = view.findViewById(R.id.save_name_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save_button.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    dialog.dismiss();
                    String User_name = user_name.getText().toString().trim();

                    userData.setUserName(User_name);

                    DatabaseHandler handler = new DatabaseHandler(getApplicationContext());
                    handler.updateUser(userData);

                    onStart();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();

        ArrayList<String> arrayListName = new ArrayList<>();
        ArrayList<String> arrayListColor = new ArrayList<>();
        ArrayList<String> arrayListTotal = new ArrayList<>();

        ListData listData = ListData.getInstance();
        listData.setUserList(arrayListName);
        listData.setUserListColor(arrayListColor);
        listData.setUserListTotal(arrayListTotal);

        UserData userData = UserData.getInstance();
        userData.setUserId("");
        userData.setUserName("");
        userData.setUserEmail("");

        //Delete SQLite Database
        DatabaseHandler db = new DatabaseHandler(Account.this);
        db.deleteDB(Account.this);

        finish();
        Util.EXIT_FLAG = true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
