package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyDay extends AppCompatActivity implements UserTasksRecyclerViewAdapter.OnTaskListener,
        View.OnClickListener {
    AlertDialog.Builder changeThemeBuilder;
    AlertDialog changeThemeDialog;
    ImageButton colorBtn_1, colorBtn_2, colorBtn_3, colorBtn_4,
            colorBtn_5, colorBtn_6, colorBtn_7;

    private FloatingActionButton my_day_addFab;

    private TableRow dueDateTag, reminderTag, repeatTag;
    private CardView addTaskCard;
    private ImageView addTaskButton;
    private EditText addTaskName;
    private Chip dueDateChip, reminderChip, repeatChip;

    private String Today;
    private String Tomorrow;
    private String NextWeek;
    private String DueDateString = "";
    private String ReminderString = "";
    private String RepeatString = "";

    private String DueToday = "Due Today";
    private String DueTomorrow = "Due Tomorrow";
    private String DueNextWeek;
    private String DueDatePickString;
    private String RemindAtString = "Remind me at";
    private String RemindTomorrowString;
    private String RemindNextWeekString;
    private String ReminderDateTimePickString;
    private String ReminderDatePickString;
    private ArrayList<String> dayList = new ArrayList<>();

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText day_input;
    private Spinner day_spinner;
    private CheckBox day_sun, day_mon, day_tue, day_wed, day_thu, day_fri, day_sat;
    private TextView day_cancel, day_done;
    private TableRow custom_days, custom_days2;
    private String value;
    private String SpinnerString;

    private boolean flag;
    private boolean popupFlag;
    private boolean DatePickFlag;
    private boolean DateTimePickFlag;
    private boolean CustomPickFlag;
    private boolean currentSpinnerInput;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private UserData userData = UserData.getInstance();
    private final String currentUserEmail = userData.getUserEmail();
    private DocumentReference documentReference;
    private DocumentReference documentRef;

    private RecyclerView recyclerView;
    private UserTasksRecyclerViewAdapter userTasksRecyclerViewAdapter;

    private Snackbar snackbar;
    private Snackbar snackBar;
    private SoundPool soundPool;
    private int sound;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_day);


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        sound = soundPool.load(this, R.raw.task_completed, 1);

        recyclerView = findViewById(R.id.my_day_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyDay.this));

        snackbar = Snackbar.make(recyclerView, "Task removed from My Day", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.white))
                .setDuration(10000);

        snackBar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.white))
                .setDuration(10000);

        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();

        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                MyDay.this, this, dbHandler.getThemeColor(1));
        userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Declaring Date in Subtitle..
        Calendar calendar;
        SimpleDateFormat simpleDateFormat;

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM");
        Today = simpleDateFormat.format(calendar.getTime());
        getSupportActionBar().setSubtitle(Today);

        simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        Today = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Tomorrow = simpleDateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 7);
        NextWeek = simpleDateFormat.format(calendar.getTime());

        addTaskCard = findViewById(R.id.my_day_addTaskCard);
        addTaskName = findViewById(R.id.task_title);
        my_day_addFab = findViewById(R.id.my_day_addFab);
        my_day_addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                snackBar.dismiss();
                createTaskPopup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);

        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();

        switch (dbHandler.getThemeColor(1)) {
            case "Theme Color 1":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_1)));
                break;
            case "Theme Color 2":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_2)));
                break;
            case "Theme Color 3":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_3)));
                break;
            case "Theme Color 4":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_4)));
                break;
            case "Theme Color 5":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_5)));
                break;
            case "Theme Color 6":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_6)));
                break;
            case "Theme Color 7":
                my_day_addFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_7)));
                break;
        }

        //Setup Adapter
        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                MyDay.this, this, dbHandler.getThemeColor(1));
        userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);
    }

    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void createTaskPopup() {
        popupFlag = true;

        addTaskCard.setVisibility(View.VISIBLE);
        my_day_addFab.setVisibility(View.INVISIBLE);
        dueDateTag = findViewById(R.id.my_day_dueDateButton);
        reminderTag = findViewById(R.id.my_day_reminderButton);
        repeatTag = findViewById(R.id.my_day_repeatButton);
        dueDateChip = findViewById(R.id.my_day_dueDateChip);
        reminderChip = findViewById(R.id.my_day_reminderChip);
        repeatChip = findViewById(R.id.my_day_repeatChip);

        addTaskButton = findViewById(R.id.task_submit_button);

        addTaskName.getText().clear();
        showSoftKeyboard(addTaskName);

        dueDateTag.setVisibility(View.VISIBLE);
        dueDateChip.setVisibility(View.GONE);

        reminderTag.setVisibility(View.VISIBLE);
        reminderChip.setVisibility(View.GONE);

        repeatTag.setVisibility(View.VISIBLE);
        repeatChip.setVisibility(View.GONE);

        dueDateTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MyDay.this, dueDateTag);
                popupMenu.getMenuInflater().inflate(R.menu.menu_due_date, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.dueDateTodayAction:
                                DueDateString = Today;
                                dueDateChip.setText(DueToday);
                                break;
                            case R.id.dueDateTomorrowAction:
                                DueDateString = Tomorrow;
                                dueDateChip.setText(DueTomorrow);
                                break;
                            case R.id.dueDateNextWeekAction:
                                DueDateString = NextWeek;
                                DueNextWeek = "Due " + NextWeek;
                                dueDateChip.setText(DueNextWeek);
                                break;
                            case R.id.dueDatePickAction:
                                DatePickFlag = true;

                                DatePickerDialog datePickerDialog = new DatePickerDialog(MyDay.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        Calendar Calender = Calendar.getInstance();
                                        Calender.set(Calendar.YEAR, year);
                                        Calender.set(Calendar.MONTH, month);
                                        Calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        DueDateString = DateFormat.format("EEE, d MMM yyyy",
                                                Calender).toString();

                                        DueDatePickString = "Due " + DateFormat.format("EEE, d MMM yyyy",
                                                Calender).toString();
                                        dueDateChip.setText(DueDatePickString);

                                        dueDateTag.setVisibility(View.GONE);
                                        dueDateChip.setVisibility(View.VISIBLE);

                                        DatePickFlag = false;
                                    }
                                }, Calendar.getInstance().get(Calendar.YEAR),
                                        Calendar.getInstance().get(Calendar.MONTH),
                                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance()
                                        .getTimeInMillis());
                                datePickerDialog.show();
                                break;
                        }

                        if (!DatePickFlag) {
                            dueDateTag.setVisibility(View.GONE);
                            dueDateChip.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        dueDateChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DueDateString = "";

                dueDateTag.setVisibility(View.VISIBLE);
                dueDateChip.setVisibility(View.GONE);
            }
        });

        reminderTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MyDay.this, reminderTag);
                popupMenu.getMenuInflater().inflate(R.menu.menu_reminder, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.reminderTomorrowAction:
                                ReminderString = "9:00 AM " + Tomorrow;
                                RemindTomorrowString = RemindAtString + " 9:00 AM Tomorrow";
                                        reminderChip.setText(RemindTomorrowString);
                                break;
                            case R.id.reminderNextWeekAction:
                                ReminderString = "9:00 AM " + NextWeek;
                                RemindNextWeekString = RemindAtString + " 9:00 AM " + NextWeek;
                                        reminderChip.setText(RemindNextWeekString);
                                break;
                            case R.id.reminderPickAction:
                                DateTimePickFlag = true;

                                DatePickerDialog datePickerDialog = new
                                        DatePickerDialog(MyDay.this,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view,
                                                                  int year,
                                                                  int month,
                                                                  int dayOfMonth) {
                                                Calendar Calender = Calendar.getInstance();
                                                Calender.set(Calendar.YEAR, year);
                                                Calender.set(Calendar.MONTH, month);
                                                Calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                                ReminderDatePickString = DateFormat
                                                        .format("EEE, d MMM yyyy",
                                                                Calender).toString();

                                                TimePickerDialog timePickerDialog =
                                                        new TimePickerDialog(MyDay.this,
                                                        new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker view,
                                                                                  int hourOfDay,
                                                                                  int minute) {
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                calendar.set(Calendar.MINUTE, minute);

                                                                ReminderDateTimePickString = RemindAtString + " " +
                                                                        DateFormat.format("h:mm a", calendar)
                                                                                .toString() + " " +
                                                                        ReminderDatePickString;

                                                                ReminderString = DateFormat.format("h:mm a",
                                                                        calendar).toString()
                                                                        + " " +
                                                                        ReminderDatePickString;

                                                                reminderChip.setText(ReminderDateTimePickString);
                                                                reminderTag.setVisibility(View.GONE);
                                                                reminderChip.setVisibility(View.VISIBLE);

                                                                DateTimePickFlag = false;
                                                            }
                                                        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                                                        Calendar.getInstance().get(Calendar.MINUTE),
                                                        false);
                                                timePickerDialog.show();
                                            }
                                        }, Calendar.getInstance().get(Calendar.YEAR),
                                        Calendar.getInstance().get(Calendar.MONTH),
                                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance()
                                        .getTimeInMillis());
                                datePickerDialog.show();
                                break;
                        }

                        if (!DateTimePickFlag) {
                            reminderTag.setVisibility(View.GONE);
                            reminderChip.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        reminderChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderString = "";

                reminderTag.setVisibility(View.VISIBLE);
                reminderChip.setVisibility(View.GONE);
            }
        });

        repeatTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MyDay.this, repeatTag);
                popupMenu.getMenuInflater().inflate(R.menu.menu_repeat, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.repeatDailyAction:
                                RepeatString = "Daily";
                                repeatChip.setText(RepeatString);
                                break;
                            case R.id.repeatWeekdaysAction:
                                RepeatString = "Weekly Weekdays";
                                repeatChip.setText(RepeatString);
                                break;
                            case R.id.repeatWeeklyAction:
                                RepeatString = "Weekly";
                                repeatChip.setText(RepeatString);
                                break;
                            case R.id.repeatMonthlyAction:
                                RepeatString = "Monthly";
                                repeatChip.setText(RepeatString);
                                break;
                            case R.id.repeatYearlyAction:
                                RepeatString = "Yearly";
                                repeatChip.setText(RepeatString);
                                break;
                            case R.id.repeatCustomAction:
                                CustomPickFlag = true;
                                createCustomPopup();
                                break;
                        }

                        if (!CustomPickFlag) {
                            repeatTag.setVisibility(View.GONE);
                            repeatChip.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        repeatChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatString = "";

                repeatTag.setVisibility(View.VISIBLE);
                repeatChip.setVisibility(View.GONE);
            }
        });

        addTaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(addTaskName.getText().toString().trim())) {
                    addTaskButton.setColorFilter(getResources().getColor(R.color.white));
                    flag = true;
                } else {
                    addTaskButton.setColorFilter(getResources().getColor(R.color.background_color_5));
                    flag = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    InputMethodManager imm = (InputMethodManager)getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(addTaskName.getWindowToken(), 0);

                    String Task_Title = addTaskName.getText().toString().trim();

                    flag = false;
                    popupFlag =false;
                    addTaskCard.setVisibility(View.INVISIBLE);
                    my_day_addFab.setVisibility(View.VISIBLE);
                    addTaskName.getText().clear();

                    dueDateTag.setVisibility(View.VISIBLE);
                    dueDateChip.setVisibility(View.GONE);

                    reminderTag.setVisibility(View.VISIBLE);
                    reminderChip.setVisibility(View.GONE);

                    repeatTag.setVisibility(View.VISIBLE);
                    repeatChip.setVisibility(View.GONE);

                    //Add Information in Firebase..
                    addTaskToDatabase(Task_Title, DueDateString, ReminderString, RepeatString);
                }
            }
        });
    }

    private void addTaskToDatabase(String task_Title,
                                   String dueDateString,
                                   String reminderString,
                                   String repeatString) {
        String isImportant = "false";
        String isMyDay = "true";
        String isCompleted = "false";
        String CreatedTime = String.valueOf(new Date());

        //Adding data in SQLite Database
        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);

        TaskModel taskModel = new TaskModel();
        taskModel.setTask_title(task_Title);
        taskModel.setTask_due_date(dueDateString);
        taskModel.setTask_repeat(repeatString);
        if (dayList.size() == 0) {
            taskModel.setTask_repeat_days("0");
        } else {
            taskModel.setTask_repeat_days(String.valueOf(dayList));
        }
        taskModel.setTask_reminder(reminderString);
        taskModel.setTask_is_important(isImportant);
        taskModel.setTask_is_my_day(isMyDay);
        taskModel.setTask_is_completed(isCompleted);
        taskModel.setTask_created_time(CreatedTime);

        dbHandler.addTask(taskModel);

        taskModel.setTasks_id(dbHandler.getLastTaskId());
        dbHandler.addMyDayTask(taskModel, "Tasks");

        ListData listData = ListData.getInstance();
        ArrayList<String> tempTasksid = listData.getTasksid();

        if (tempTasksid.size() == 0) {
            tempTasksid = new ArrayList<>();
        }

        tempTasksid.add(String.valueOf(dbHandler.getLastTaskId()));
        listData.setTasksid(tempTasksid);

        Map<String, String> idObj = new HashMap<>();
        idObj.put("User Tasks ID", String.valueOf(listData.getUserTasksid()));
        idObj.put("Tasks ID", String.valueOf(listData.getTasksid()));

        documentRef = db.collection("List Id").document(currentUserEmail);

        documentRef.delete();
        documentRef.set(idObj);

        documentReference = db.collection("userLists/"+ currentUserEmail +"/Tasks")
                .document(String.valueOf(dbHandler.getLastTaskId()));

        Map<String, String> taskObj = new HashMap<>();
        taskObj.put("Title", task_Title);
        taskObj.put("Due Date", dueDateString);
        taskObj.put("Repeat", repeatString);
        if (dayList.size() == 0) {
            taskObj.put("Repeat Days", "0");
        } else {
            taskObj.put("Repeat Days", String.valueOf(dayList));
        }
        taskObj.put("Reminder", reminderString);
        taskObj.put("is Important", isImportant);
        taskObj.put("is My Day", isMyDay);
        taskObj.put("is Completed", isCompleted);
        taskObj.put("Created Time", CreatedTime);

        DueDateString = "";
        ReminderString = "";
        RepeatString = "";

        documentReference.set(taskObj);

        onStart();
    }

    private void createCustomPopup() {
        builder = new AlertDialog.Builder(MyDay.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.custom_repeat_popup, null);

        custom_days = view.findViewById(R.id.custom_days);
        custom_days2 = view.findViewById(R.id.custom_days2);

        day_input = view.findViewById(R.id.day_input);
        value = day_input.getText().toString().trim();
        day_spinner = view.findViewById(R.id.day_spinner);
        day_sun = view.findViewById(R.id.day_sun);
        day_mon = view.findViewById(R.id.day_mon);
        day_tue = view.findViewById(R.id.day_tue);
        day_wed = view.findViewById(R.id.day_wed);
        day_thu = view.findViewById(R.id.day_thu);
        day_fri = view.findViewById(R.id.day_fri);
        day_sat = view.findViewById(R.id.day_sat);

        day_cancel = view.findViewById(R.id.custom_cancel_button);
        day_done = view.findViewById(R.id.custom_done_button);

        day_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                value = day_input.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(MyDay.this,
                R.array.days, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(arrayAdapter);

        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.WHITE);
                SpinnerString = parent.getItemAtPosition(position).toString();

                if (SpinnerString.equals("weeks")) {
                    currentSpinnerInput = true;
                }

                if (currentSpinnerInput) {
                    custom_days.setVisibility(View.VISIBLE);
                    custom_days2.setVisibility(View.VISIBLE);
                    currentSpinnerInput = false;
                } else {
                    custom_days.setVisibility(View.GONE);
                    custom_days2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        day_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!day_input.getText().toString().trim().equals("0") &&
                        !day_input.getText().toString().trim().equals("00") &&
                        !day_input.getText().toString().trim().equals("000") &&
                        !TextUtils.isEmpty(day_input.getText().toString().trim())) {
                    switch (SpinnerString) {
                        case "days":
                            if (value.equals("1")) {
                                RepeatString = "Daily";
                            } else {
                                RepeatString = "Every "+ value + " days";
                            }

                            break;
                        case "weeks":
                            if (value.equals("1")) {
                                RepeatString = "Weekly";
                            } else {
                                RepeatString = "Every "+ value +" weeks";
                            }
                            if (day_sun.isChecked())
                                dayList.add("Sunday");
                            if (day_mon.isChecked())
                                dayList.add("Monday");
                            if (day_tue.isChecked())
                                dayList.add("Tuesday");
                            if (day_wed.isChecked())
                                dayList.add("Wednesday");
                            if (day_thu.isChecked())
                                dayList.add("Thursday");
                            if (day_fri.isChecked())
                                dayList.add("Friday");
                            if (day_sat.isChecked())
                                dayList.add("Saturday");
                            break;
                        case "months":
                            if (value.equals("1")) {
                                RepeatString = "Monthly";
                            } else {
                                RepeatString = "Every "+ value + " months";
                            }

                            break;
                        case "years":
                            if (value.equals("1")) {
                                RepeatString = "Yearly";
                            } else {
                                RepeatString = "Every "+ value + " years";
                            }

                            break;
                    }

                    CustomPickFlag = false;
                    repeatChip.setText(RepeatString);
                    repeatTag.setVisibility(View.GONE);
                    repeatChip.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        });

        day_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (popupFlag) {
            addTaskCard.setVisibility(View.INVISIBLE);
            my_day_addFab.setVisibility(View.VISIBLE);
            popupFlag = false;
        } else {
            if (Util.IS_MY_DAY) {
                Util.IS_MY_DAY = false;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_day, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Util.IS_MY_DAY) {
                    Util.IS_MY_DAY = false;
                }
                break;
            case R.id.action_sort_by:
                //Sorting..
                AlertDialog.Builder builder = new AlertDialog.Builder(MyDay.this);

                builder.setTitle("Sort by");

                String[] names = {"Creation date", "Important", "Alphabetically"};
                int checkedItem = 0; // cow
                builder.setSingleChoiceItems(names, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortingUserList(which, dialog);
                    }
                });
                AlertDialog d = builder.create();
                d.show();
                break;
            case R.id.action_theme:
                //Change Theme..
                changeThemeBuilder = new AlertDialog.Builder(MyDay.this);
                @SuppressLint("InflateParams") final View view = getLayoutInflater()
                        .inflate(R.layout.change_theme_popup, null);

                colorBtn_1 = view.findViewById(R.id.color_btn_1);
                colorBtn_2 = view.findViewById(R.id.color_btn_2);
                colorBtn_3 = view.findViewById(R.id.color_btn_3);
                colorBtn_4 = view.findViewById(R.id.color_btn_4);
                colorBtn_5 = view.findViewById(R.id.color_btn_5);
                colorBtn_6 = view.findViewById(R.id.color_btn_6);
                colorBtn_7 = view.findViewById(R.id.color_btn_7);

                colorBtn_1.setOnClickListener(this);
                colorBtn_2.setOnClickListener(this);
                colorBtn_3.setOnClickListener(this);
                colorBtn_4.setOnClickListener(this);
                colorBtn_5.setOnClickListener(this);
                colorBtn_6.setOnClickListener(this);
                colorBtn_7.setOnClickListener(this);

                changeThemeBuilder.setView(view);
                changeThemeDialog = changeThemeBuilder.create();
                changeThemeDialog.show();
                break;
            case R.id.action_completed_tasks:
                //Hide Tasks..
                hideCompletedMyDay();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideCompletedMyDay() {
        DatabaseHandler handler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels = handler.hideCompletedMyDay();

        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                MyDay.this, this, handler.getThemeColor(1));
        userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);
    }

    private void sortingUserList(int which, DialogInterface d) {
        DatabaseHandler handler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels;
        // setup the alert builder
        switch (which) {
            case 0:
                //Creation Date
                onStart();
                break;
            case 1:
                //Importance
                taskModels = handler.getAllMyDayImportance();

                userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                        MyDay.this, this, handler.getThemeColor(1));
                userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
                recyclerView.setAdapter(userTasksRecyclerViewAdapter);
                break;
            case 2:
                //Alphabetically
                taskModels = handler.getAllMyDayAlpha();

                userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                        MyDay.this, this, handler.getThemeColor(1));
                userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
                recyclerView.setAdapter(userTasksRecyclerViewAdapter);
                break;
        }

        d.dismiss();
    }

    @Override
    public void onTaskClick(int position) {
        addTaskCard.setVisibility(View.INVISIBLE);
        my_day_addFab.setVisibility(View.VISIBLE);

        final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<String> items = dbHandler.getMyDayTaskList();

        List<TaskModel> tempTaskModels = dbHandler.getAllMyDayTask();
        final TaskModel tempTaskModel = tempTaskModels.get(position);

        if (items.get(position).equals("Tasks")) {
            Util.IS_TASKS = true;
            Intent intent = new Intent(getApplicationContext(), TaskDetail.class);
            intent.putExtra("themeColor", dbHandler.getThemeColor(1));
            List<TaskModel> taskModels = dbHandler.getAllTasks();

            int pos = 0;
            while (pos != taskModels.size()) {
                TaskModel taskModel = taskModels.get(pos);

                if (taskModel.getTasks_id() == tempTaskModel.getTasks_id()) {
                    intent.putExtra("tasksKeyPosition", pos);
                }

                pos++;
            }
            //Navigate to new Activity
            startActivity(intent);
        } else {
            Util.IS_TASKS = false;
            Intent intent = new Intent(getApplicationContext(), TaskDetail.class);
            intent.putExtra("themeColor", dbHandler.getThemeColor(1));
            List<TaskModel> taskModels = dbHandler.getAllUserTasks(items.get(position));

            int pos = 0;
            while (pos != taskModels.size()) {
                TaskModel taskModel = taskModels.get(pos);

                if (taskModel.getTasks_id() == tempTaskModel.getTasks_id()) {
                    intent.putExtra("keyPosition", pos);
                }
                pos++;
            }

            ListData listData = ListData.getInstance();
            ArrayList<String> arrayList = listData.getUserList();

            int ListPos = 0;
            while (ListPos != arrayList.size()) {
                if (items.get(position).equals(arrayList.get(ListPos))) {
                    intent.putExtra("listPosition", ListPos);
                }
                ListPos++;
            }

            //Navigate to new Activity
            startActivity(intent);
        }
    }

    @Override
    public void onCheckboxUnCheck(int position, TextView task_name,
                                  ImageButton checkbox_uncheck, final ImageButton checkbox_check) {

        final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<String> settings = dbHandler.getAllSettings();

        //Playing task completed sound
        if (settings.get(1).equals("true")) {
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }

        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();
        List<String> items = dbHandler.getMyDayTaskList();
        final TaskModel taskModel = taskModels.get(position);

        task_name.setTextColor(getResources()
                .getColor(R.color.background_color_5));
        checkbox_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(MyDay.this,
                        R.anim.fade_animation);
                checkbox_check.startAnimation(animation);
                checkbox_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        switch (dbHandler.getThemeColor(1)) {
            case "Theme Color 1":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_1));
                break;
            case "Theme Color 2":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_2));
                break;
            case "Theme Color 3":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_3));
                break;
            case "Theme Color 4":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_4));
                break;
            case "Theme Color 5":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_5));
                break;
            case "Theme Color 6":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_6));
                break;
            case "Theme Color 7":
                checkbox_check.setColorFilter(getResources().getColor(R.color.theme_color_7));
                break;
        }

        if (items.get(position).equals("Tasks")) {
            taskModel.setTask_is_completed("true");
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
            id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/Tasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Completed", "true");
            docRef.update(updateTotalObj);
        } else {
            taskModel.setTask_is_completed("true");
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            id = dbHandler.getMyDayTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            dbHandler.updateUserTask(taskModel, items.get(position));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/UserTasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Completed", "true");
            docRef.update(updateTotalObj);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;

            if (Util.IS_MY_DAY) {
                Util.IS_MY_DAY = false;
            }
        }
    }

    @Override
    public void onCheckboxCheck(int position, TextView task_name,
                                ImageButton checkbox_check, ImageButton checkbox_uncheck) {
        final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();
        List<String> items = dbHandler.getMyDayTaskList();
        final TaskModel taskModel = taskModels.get(position);

        task_name.setTextColor(getResources()
                .getColor(R.color.white));
        checkbox_uncheck.setVisibility(View.VISIBLE);
        checkbox_check.setVisibility(View.GONE);

        if (items.get(position).equals("Tasks")) {
            taskModel.setTask_is_completed("false");
            dbHandler.updateTask(taskModel);
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/Tasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Completed", "false");
            docRef.update(updateTotalObj);
        } else {
            taskModel.setTask_is_completed("false");
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            id = dbHandler.getMyDayTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            dbHandler.updateUserTask(taskModel, items.get(position));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/UserTasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Completed", "false");
            docRef.update(updateTotalObj);
        }
    }

    @Override
    public void onImportantUnCheck(int position, final ImageButton important_check, ImageButton important_uncheck) {
        final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();
        List<String> items = dbHandler.getMyDayTaskList();
        final TaskModel taskModel = taskModels.get(position);

        important_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(MyDay.this,
                        R.anim.fade_animation);
                important_check.startAnimation(animation);
                important_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        switch (dbHandler.getThemeColor(1)) {
            case "Theme Color 1":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_1));
                break;
            case "Theme Color 2":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_2));
                break;
            case "Theme Color 3":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_3));
                break;
            case "Theme Color 4":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_4));
                break;
            case "Theme Color 5":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_5));
                break;
            case "Theme Color 6":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_6));
                break;
            case "Theme Color 7":
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_7));
                break;
        }

        if (items.get(position).equals("Tasks")) {
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            taskModel.setTask_is_important("true");
            dbHandler.updateTask(taskModel);
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            dbHandler.addImportantTask(taskModel, items.get(position));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/Tasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Important", "true");
            docRef.update(updateTotalObj);
        } else {
            taskModel.setTask_is_important("true");
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            dbHandler.updateUserTask(taskModel, items.get(position));
            dbHandler.addImportantTask(taskModel, items.get(position));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/UserTasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Important", "true");
            docRef.update(updateTotalObj);
        }
    }

    @Override
    public void onImportantCheck(int position, ImageButton important_check, ImageButton important_uncheck) {
        final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
        List<TaskModel> taskModels = dbHandler.getAllMyDayTask();
        List<String> items = dbHandler.getMyDayTaskList();
        final TaskModel taskModel = taskModels.get(position);

        important_uncheck.setVisibility(View.VISIBLE);
        important_check.setVisibility(View.GONE);

        if (items.get(position).equals("Tasks")) {
            taskModel.setTask_is_important("false");
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
            id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/Tasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Important", "false");
            docRef.update(updateTotalObj);
        } else {
            taskModel.setTask_is_important("false");
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(taskModel.getTasks_id()),
                    items.get(position));
            if (id.size() != 0) {
                dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
            }

            dbHandler.updateUserTask(taskModel, items.get(position));
            id = dbHandler.getMyDayTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    items.get(position));
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/UserTasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is Important", "false");
            docRef.update(updateTotalObj);
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            addTaskCard.setVisibility(View.INVISIBLE);
            my_day_addFab.setVisibility(View.VISIBLE);

            final int rowPosition = viewHolder.getAdapterPosition();
            final DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);
            List<TaskModel> allItems = dbHandler.getAllMyDayTask();
            final List<String> allLists = dbHandler.getMyDayTaskList();
            final TaskModel taskModel = allItems.get(rowPosition);

            InputMethodManager imm = (InputMethodManager)getSystemService
                    (Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(addTaskName.getWindowToken(), 0);

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    if (taskModel.getTask_is_my_day().equals("true")) {
                        if (allLists.get(rowPosition).equals("Tasks")) {
                            taskModel.setTask_is_my_day("false");
                            dbHandler.updateTask(taskModel);
                            List<String> id = dbHandler.getImportantTaskId(
                                    String.valueOf(taskModel.getTasks_id()),
                                    allLists.get(rowPosition));
                            if (id.size() != 0) {
                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                            }

                            id = dbHandler.getMyDayTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    "Tasks");
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));

                            UserData userData = UserData.getInstance();
                            final String currentUserEmail = userData.getUserEmail();
                            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                    + "/Tasks")
                                    .document(String.valueOf(taskModel.getTasks_id()));

                            Map<String, Object> updateTotalObj = new HashMap<>();
                            updateTotalObj.put("is My Day", "false");
                            docRef.update(updateTotalObj);
                            final String tempId = id.get(0);

                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addRemovedMyDayTask(taskModel, "Tasks", tempId);
                                }
                            }).show();
                        } else {
                            taskModel.setTask_is_my_day("false");
                            dbHandler.updateUserTask(taskModel, allLists.get(rowPosition));
                            List<String> id = dbHandler.getImportantTaskId(
                                    String.valueOf(taskModel.getTasks_id()),
                                    allLists.get(rowPosition));
                            if (id.size() != 0) {
                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                            }

                            id = dbHandler.getMyDayTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    allLists.get(rowPosition));
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));

                            UserData userData = UserData.getInstance();
                            final String currentUserEmail = userData.getUserEmail();
                            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                    + "/UserTasks")
                                    .document(String.valueOf(taskModel.getTasks_id()));

                            Map<String, Object> updateTotalObj = new HashMap<>();
                            updateTotalObj.put("is My Day", "false");
                            docRef.update(updateTotalObj);

                            final String tempId = id.get(0);

                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addRemovedMyDayTask(taskModel, allLists.get(rowPosition), tempId);
                                }
                            }).show();
                        }

                        onStart();
                    }
                    break;
                case ItemTouchHelper.LEFT:
                    if (allLists.get(rowPosition).equals("Tasks")) {
                        dbHandler.deleteTask(taskModel);
                        List<String> id = dbHandler.getImportantTaskId(
                                String.valueOf(taskModel.getTasks_id()),
                                "Tasks");
                        String finalTempId2 = "";
                        if (id.size() != 0) {
                            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                            finalTempId2 = id.get(0);
                        }

                        id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/Tasks")
                                .document(String.valueOf(taskModel.getTasks_id()));
                        docRef.delete();

                        ListData listData = ListData.getInstance();
                        ArrayList<String> tempUserTasksId = listData.getTasksid();
                        tempUserTasksId.remove(String.valueOf(taskModel.getTasks_id()));

                        listData.setTasksid(tempUserTasksId);

                        Map<String, Object> idObj = new HashMap<>();
                        idObj.put("Tasks ID", String.valueOf(tempUserTasksId));

                        DocumentReference documentReference = db.collection("List Id")
                                .document(currentUserEmail);
                        documentReference.update(idObj);

                        final String finalTempId = id.get(0);
                        final String finalTempId1 = finalTempId2;
                        snackBar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDeletedTaskToDatabase(taskModel, finalTempId, finalTempId1, true,
                                        allLists, rowPosition);
                            }
                        }).show();

                        onStart();
                    } else {
                        ListData listData = ListData.getInstance();
                        final ArrayList<String> arrayListName = listData.getUserList();
                        final ArrayList<String> arrayListTotal = listData.getUserListTotal();
                        final ArrayList<String> arrayListColor = listData.getUserListColor();

                        int cnt = 0;
                        int ListPosition = 0;
                        while (cnt != arrayListName.size()) {
                            if (allLists.get(rowPosition).equals(arrayListName.get(cnt))) {
                                ListPosition = cnt;
                            }
                            cnt++;
                        }

                        dbHandler.deleteUserTask(taskModel);
                        List<String> id = dbHandler.getImportantTaskId(
                                String.valueOf(taskModel.getTasks_id()),
                                allLists.get(rowPosition));
                        String finalTempId2 = "";
                        if (id.size() != 0) {
                            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                            finalTempId2 = id.get(0);
                        }

                        id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(ListPosition));
                        dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));
                        docRef.delete();

                        ArrayList<String> tempUserTasksid = listData.getUserTasksid();
                        tempUserTasksid.remove(String.valueOf(taskModel.getTasks_id()));

                        listData.setUserTasksid(tempUserTasksid);

                        Map<String, Object> idObj = new HashMap<>();
                        idObj.put("User Tasks ID", String.valueOf(tempUserTasksid));

                        DocumentReference documentReference = db.collection("List Id")
                                .document(currentUserEmail);
                        documentReference.update(idObj);

                        arrayListTotal.set(ListPosition, String.valueOf(
                                dbHandler.getUserTasksCount(arrayListName.get(ListPosition))));

                        ListModel listModel = new ListModel();
                        listModel.setList_id(ListPosition);
                        listModel.setList_name(arrayListName.get(ListPosition));
                        listModel.setList_color(arrayListColor.get(ListPosition));
                        listModel.setList_total(arrayListTotal.get(ListPosition));
                        dbHandler.updateList(listModel);

                        //Updating Total in Database
                        DocumentReference document = db.collection("userLists")
                                .document(currentUserEmail);

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("List Total", String.valueOf(listData.getUserListTotal()));

                        document.update(updateTotalObj);

                        final String finalTempId = id.get(0);
                        final String finalTempId1 = finalTempId2;
                        snackBar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDeletedTaskToDatabase(taskModel, finalTempId, finalTempId1,
                                        false, allLists, rowPosition);
                            }
                        }).show();

                        onStart();
                    }
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState,
                    isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_trash_white)
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.swipe_red))
                    .addSwipeRightActionIcon(R.drawable.ic_sun_white)
                    .addSwipeRightBackgroundColor(getResources().getColor(R.color.swipe_blue))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void addRemovedMyDayTask(TaskModel taskModel, String s, String lastTempId) {
        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);

        if (s.equals("Tasks")) {
            taskModel.setTask_is_my_day("true");
            dbHandler.updateTask(taskModel);
            dbHandler.addRemovedMyDayTask(taskModel, "Tasks", Integer.parseInt(lastTempId));
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    s);

            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/Tasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is My Day", "true");
            docRef.update(updateTotalObj);
        } else {
            taskModel.setTask_is_my_day("true");
            dbHandler.updateUserTask(taskModel, s);
            dbHandler.addRemovedMyDayTask(taskModel, s, Integer.parseInt(lastTempId));
            List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    s);

            if (id.size() != 0) {
                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
            }

            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                    + "/UserTasks")
                    .document(String.valueOf(taskModel.getTasks_id()));

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("is My Day", "true");
            docRef.update(updateTotalObj);
        }

        onStart();
    }

    private void addDeletedTaskToDatabase(TaskModel deletedTaskModel, String finalTempId, String finalTempId2,
                                          boolean b,
                                          List<String> allLists,
                                          int rowPosition) {
        String task_Title = deletedTaskModel.getTask_title();
        String dueDateString = deletedTaskModel.getTask_due_date();
        String reminderString = deletedTaskModel.getTask_reminder();
        String repeatString = deletedTaskModel.getTask_repeat();
        String deletedRepeatDays = deletedTaskModel.getTask_repeat_days();
        String isImportant = deletedTaskModel.getTask_is_important();
        String isMyDay = deletedTaskModel.getTask_is_my_day();
        String isCompleted = deletedTaskModel.getTask_is_completed();
        String CreatedTime = deletedTaskModel.getTask_created_time();

        //Adding data in SQLite Database
        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);

        TaskModel taskModel = new TaskModel();
        taskModel.setTask_title(task_Title);
        taskModel.setTask_due_date(dueDateString);
        taskModel.setTask_repeat(repeatString);
        taskModel.setTask_repeat_days(deletedRepeatDays);
        taskModel.setTask_reminder(reminderString);
        taskModel.setTask_is_important(isImportant);
        taskModel.setTask_is_my_day(isMyDay);
        taskModel.setTask_is_completed(isCompleted);
        taskModel.setTask_created_time(CreatedTime);

        if (b) {
            dbHandler.addTask(taskModel);
            deletedTaskModel.setTasks_id(dbHandler.getLastTaskId());
            dbHandler.addRemovedMyDayTask(deletedTaskModel,
                    "Tasks",
                    Integer.parseInt(finalTempId));

            if (!TextUtils.isEmpty(finalTempId2.trim())) {
                dbHandler.addRemovedImportantTask(deletedTaskModel,
                        "Tasks",
                        Integer.parseInt(finalTempId2));
            }

            ListData listData = ListData.getInstance();
            ArrayList<String> tempTasksId = listData.getTasksid();

            if (tempTasksId.size() == 0) {
                tempTasksId = new ArrayList<>();
            }

            tempTasksId.add(String.valueOf(dbHandler.getLastTaskId()));
            listData.setTasksid(tempTasksId);

            Map<String, String> idObj = new HashMap<>();
            idObj.put("User Tasks ID", String.valueOf(listData.getUserTasksid()));
            idObj.put("Tasks ID", String.valueOf(listData.getTasksid()));

            documentRef = db.collection("List Id").document(currentUserEmail);

            documentRef.delete();
            documentRef.set(idObj);

            documentReference = db.collection("userLists/"+ currentUserEmail +"/Tasks")
                    .document(String.valueOf(dbHandler.getLastTaskId()));

            Map<String, String> taskObj = new HashMap<>();
            taskObj.put("Title", task_Title);
            taskObj.put("Due Date", dueDateString);
            taskObj.put("Repeat", repeatString);
            taskObj.put("Repeat Days", deletedRepeatDays);
            taskObj.put("Reminder", reminderString);
            taskObj.put("is Important", isImportant);
            taskObj.put("is My Day", isMyDay);
            taskObj.put("is Completed", isCompleted);
            taskObj.put("Created Time", CreatedTime);

            DueDateString = "";
            ReminderString = "";
            RepeatString = "";

            documentReference.set(taskObj);

            onStart();
        } else {
            ListData listData = ListData.getInstance();
            final ArrayList<String> arrayListName = listData.getUserList();
            final ArrayList<String> arrayListTotal = listData.getUserListTotal();
            final ArrayList<String> arrayListColor = listData.getUserListColor();
            final ArrayList<String> tempUserTasksid = listData.getUserTasksid();

            int cnt = 0;
            int ListPosition = 0;
            while (cnt != arrayListName.size()) {
                if (allLists.get(rowPosition).equals(arrayListName.get(cnt))) {
                    ListPosition = cnt;
                }
                cnt++;
            }

            dbHandler.addUserTask(taskModel, arrayListName.get(ListPosition));
            deletedTaskModel.setTasks_id(dbHandler.getLastUserTaskId());
            dbHandler.addRemovedMyDayTask(deletedTaskModel,
                    arrayListName.get(ListPosition),
                    Integer.parseInt(finalTempId));
            if (!TextUtils.isEmpty(finalTempId2.trim())) {
                dbHandler.addRemovedImportantTask(deletedTaskModel,
                        arrayListName.get(ListPosition),
                        Integer.parseInt(finalTempId2));
            }

            tempUserTasksid.add(String.valueOf(dbHandler.getLastUserTaskId()));
            listData.setUserTasksid(tempUserTasksid);

            Map<String, Object> idObj = new HashMap<>();
            idObj.put("User Tasks ID", String.valueOf(listData.getUserTasksid()));
            idObj.put("Tasks ID", String.valueOf(listData.getTasksid()));

            documentRef = db.collection("List Id").document(currentUserEmail);

            documentRef.delete();
            documentRef.set(idObj);

            documentReference = db.collection("userLists/"+ currentUserEmail +"/UserTasks")
                    .document(String.valueOf(dbHandler.getLastUserTaskId()));

            Map<String, Object> taskObj = new HashMap<>();
            taskObj.put("Title", task_Title);
            taskObj.put("UserTasksList Name", arrayListName.get(ListPosition));
            taskObj.put("Due Date", dueDateString);
            taskObj.put("Repeat", repeatString);
            taskObj.put("Repeat Days", deletedRepeatDays);
            taskObj.put("Reminder", reminderString);
            taskObj.put("is Important", isImportant);
            taskObj.put("is My Day", isMyDay);
            taskObj.put("is Completed", isCompleted);
            taskObj.put("Created Time", CreatedTime);

            documentReference.set(taskObj);

            DueDateString = "";
            ReminderString = "";
            RepeatString = "";

            //Setup Adapter
            onStart();

            //Adding task count
            arrayListTotal.set(ListPosition, String.valueOf(
                    dbHandler.getUserTasksCount(arrayListName.get(ListPosition))));

            ListModel listModel = new ListModel();
            listModel.setList_id(ListPosition);
            listModel.setList_name(arrayListName.get(ListPosition));
            listModel.setList_color(arrayListColor.get(ListPosition));
            listModel.setList_total(arrayListTotal.get(ListPosition));
            dbHandler.updateList(listModel);

            //Updating Total in Database
            UserData userData = UserData.getInstance();
            final String currentUserEmail = userData.getUserEmail();
            DocumentReference docRef = db.collection("userLists")
                    .document(currentUserEmail);

            Map<String, Object> updateTotalObj = new HashMap<>();
            updateTotalObj.put("List Total", String.valueOf(listData.getUserListTotal()));

            docRef.update(updateTotalObj);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_btn_1:
                changeTheme("Theme Color 1");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_2:
                changeTheme("Theme Color 2");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_3:
                changeTheme("Theme Color 3");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_4:
                changeTheme("Theme Color 4");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_5:
                changeTheme("Theme Color 5");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_6:
                changeTheme("Theme Color 6");
                changeThemeDialog.dismiss();
                break;
            case R.id.color_btn_7:
                changeTheme("Theme Color 7");
                changeThemeDialog.dismiss();
                break;
        }
    }

    private void changeTheme(String c) {
        DatabaseHandler dbHandler = new DatabaseHandler(MyDay.this);

        dbHandler.updateMyDayThemeColor(c);

        //Updating Total in Database
        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists")
                .document(currentUserEmail);

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("MyDay Theme Color", c);

        docRef.update(updateTotalObj);

        onStart();
    }
}
