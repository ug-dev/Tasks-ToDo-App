package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    private Switch general_switch_1,
            general_switch_2,
            general_switch_3,
            general_switch_4;

    private TextView swipe_right_info, swipe_left_info;

    private Switch noti_switch_1, noti_switch_2, noti_switch_3;

    private CardView play_sound_card,
            confirm_delete_card,
            add_tasks_card,
            move_starred_card,
            theme_card;

    private CardView mark_card, vibrate_card, day_remind_card, reminder_card, swipe_right, swipe_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swipe_right_info = findViewById(R.id.swipe_right_info);
        swipe_left_info = findViewById(R.id.swipe_left_info);

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        general_switch_1 = findViewById(R.id.general_switch_1);
        play_sound_card = findViewById(R.id.play_sound_card);
        play_sound_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(general_switch_1, 1);
            }
        });

        general_switch_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(general_switch_1, 1);
            }
        });

        general_switch_2 = findViewById(R.id.general_switch_2);
        confirm_delete_card = findViewById(R.id.confirm_delete_card);
        confirm_delete_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(general_switch_2, 2);
            }
        });

        general_switch_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(general_switch_2, 2);
            }
        });

        general_switch_3 = findViewById(R.id.general_switch_3);
        add_tasks_card = findViewById(R.id.add_tasks_card);
        add_tasks_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(general_switch_3, 3);
            }
        });

        general_switch_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(general_switch_3, 3);
            }
        });

        general_switch_4 = findViewById(R.id.general_switch_4);
        move_starred_card = findViewById(R.id.move_starred_card);
        move_starred_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(general_switch_4, 4);
            }
        });

        general_switch_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(general_switch_4, 4);
            }
        });

        noti_switch_1 = findViewById(R.id.noti_switch_1);
        mark_card = findViewById(R.id.mark_card);
        mark_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(noti_switch_1, 5);
            }
        });

        noti_switch_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(noti_switch_1, 5);
            }
        });

        noti_switch_2 = findViewById(R.id.noti_switch_2);
        vibrate_card = findViewById(R.id.vibrate_card);
        vibrate_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(noti_switch_2, 6);
            }
        });

        noti_switch_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(noti_switch_2, 6);
            }
        });

        noti_switch_3 = findViewById(R.id.noti_switch_3);
        reminder_card = findViewById(R.id.reminder_card);
        reminder_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviour(noti_switch_3, 8);
            }
        });

        noti_switch_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSwitchBehaviourExtra(noti_switch_3, 8);
            }
        });

        swipe_right = findViewById(R.id.swipe_right_card);
        swipe_left = findViewById(R.id.swipe_left_card);

        swipe_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                builder.setTitle("Swipe right");

                String[] names = {"My Day", "Complete", "Delete"};
                int checkedItem = 0;
                builder.setSingleChoiceItems(names, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeSwipeRightAction(which, dialog);
                    }
                });
                AlertDialog d = builder.create();
                d.show();
            }
        });

        swipe_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                builder.setTitle("Swipe left");

                String[] names = {"My Day", "Complete", "Delete"};
                int checkedItem = 0;
                builder.setSingleChoiceItems(names, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeSwipeLeftAction(which, dialog);
                    }
                });
                AlertDialog d = builder.create();
                d.show();
            }
        });

        day_remind_card = findViewById(R.id.day_remind_card);
        day_remind_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, PlanYourDayReminder.class));
            }
        });
    }

    private void changeSwipeRightAction(int which, DialogInterface dialog) {
        DatabaseHandler handler = new DatabaseHandler(Settings.this);
        List<String> items = handler.getAllSettings();

        switch (which) {
            case 0:
                items.set(9, "My Day");
                break;
            case 1:
                items.set(9, "Complete");
                break;
            case 2:
                items.set(9, "Delete");
                break;
        }

        handler.updateSettings(items);
        dialog.dismiss();

        onStart();
    }

    private void changeSwipeLeftAction(int which, DialogInterface dialog) {
        DatabaseHandler handler = new DatabaseHandler(Settings.this);
        List<String> items = handler.getAllSettings();

        switch (which) {
            case 0:
                items.set(10, "My Day");
                break;
            case 1:
                items.set(10, "Complete");
                break;
            case 2:
                items.set(10, "Delete");
                break;
        }

        handler.updateSettings(items);
        dialog.dismiss();

        onStart();
    }

    private void changeSwitchBehaviourExtra(Switch sw, int i) {
        DatabaseHandler handler = new DatabaseHandler(Settings.this);
        List<String> items = handler.getAllSettings();

        if (sw.isChecked()) {
            items.set(i, "true");
        } else {
            items.set(i, "false");
        }
        handler.updateSettings(items);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseHandler handler = new DatabaseHandler(Settings.this);
        List<String> items = handler.getAllSettings();
        checkSwitch(items.get(1), general_switch_1);
        checkSwitch(items.get(2), general_switch_2);
        checkSwitch(items.get(3), general_switch_3);
        checkSwitch(items.get(4), general_switch_4);
        checkSwitch(items.get(5), noti_switch_1);
        checkSwitch(items.get(6), noti_switch_2);
        checkSwitch(items.get(8), noti_switch_3);

        swipe_right_info.setText(items.get(9));
        swipe_left_info.setText(items.get(10));
    }

    private void checkSwitch(String s, Switch sw) {
        switch (s) {
            case "true":
                sw.setChecked(true);
                break;
            case "false":
                sw.setChecked(false);
                break;
        }
    }

    private void changeSwitchBehaviour(Switch sw, int i) {
        final DatabaseHandler handler = new DatabaseHandler(Settings.this);
        List<String> items = handler.getAllSettings();

        if (sw.isChecked()) {
            sw.setChecked(false);
            items.set(i, "false");
        } else {
            sw.setChecked(true);
            items.set(i, "true");
        }
        handler.updateSettings(items);
    }
}
