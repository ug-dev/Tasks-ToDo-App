package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class InviteYourFriends extends AppCompatActivity {

    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_your_friends);

        shareButton = findViewById(R.id.share_button);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "I'm using Tasks to boost my productivity. " +
                        "Try this simple & efficient to-do app. Use this link: " +
                        "https://firebasestorage.googleapis.com/v0/b/tasks-411.appspot.com/o/Tasks.apk?alt=media&token=7cca8004-87bb-46d7-bdcd-76c061ce5325";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(intent);
            }
        });

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}