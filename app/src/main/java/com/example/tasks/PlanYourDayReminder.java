package com.example.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class PlanYourDayReminder extends AppCompatActivity {

    private Switch reminder_action_switch;
    private TextView reminder_time, reminder_am_pm;
    private CheckBox day_sun, day_mon, day_tue, day_wed, day_thu, day_fri, day_sat;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_your_day_reminder);

        final DatabaseHandler handler = new DatabaseHandler(PlanYourDayReminder.this);
        final List<String> items = handler.getAllSettings();

        constraintLayout = findViewById(R.id.reminder_layout);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Plan your day reminders");
        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        reminder_action_switch = findViewById(R.id.reminder_action_switch);
        reminder_time = findViewById(R.id.reminder_time_text);
        reminder_am_pm = findViewById(R.id.reminder_am_pm);
        day_sun = findViewById(R.id.reminder_day_sun);
        day_mon = findViewById(R.id.reminder_day_mon);
        day_tue = findViewById(R.id.reminder_day_tue);
        day_wed = findViewById(R.id.reminder_day_wed);
        day_thu = findViewById(R.id.reminder_day_thu);
        day_fri = findViewById(R.id.reminder_day_fri);
        day_sat = findViewById(R.id.reminder_day_sat);

        if (items.get(7).equals("true")) {
            constraintLayout.setVisibility(View.VISIBLE);
            reminder_action_switch.setChecked(true);
        }

        reminder_action_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminder_action_switch.isChecked()) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    items.set(7, "true");
                } else {
                    constraintLayout.setVisibility(View.GONE);
                    items.set(7, "false");
                }
                handler.updateSettings(items);
            }
        });
    }
}