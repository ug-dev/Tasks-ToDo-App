package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
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

public class UserTasksList extends AppCompatActivity implements UserTasksRecyclerViewAdapter.OnTaskListener,
        View.OnClickListener {

    private int alarmHourOfDay, alarmMinute, alarmSecond;

    AlertDialog.Builder changeThemeBuilder;
    AlertDialog changeThemeDialog;
    ImageButton colorBtn_1, colorBtn_2, colorBtn_3, colorBtn_4,
            colorBtn_5, colorBtn_6, colorBtn_7;

    private AlertDialog.Builder renameBuilder;
    private AlertDialog renameDialog;
    private TextView create_button;
    private TextView cancel_button;
    private TextView popup_title_text;
    private EditText list_title;

    private int ListPosition;

    private FloatingActionButton userAddFab;

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
    private boolean alarmFlag;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private UserData userData = UserData.getInstance();
    private final String currentUserEmail = userData.getUserEmail();
    private DocumentReference documentReference;
    private DocumentReference documentRef;

    private RecyclerView recyclerView;
    private UserTasksRecyclerViewAdapter userTasksRecyclerViewAdapter;

    private Snackbar snackbar;
    private SoundPool soundPool;
    private int sound;

    private ImageView not_found_image;
    private TextView not_found_text;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tasks_list);

        not_found_image = findViewById(R.id.not_found_image);
        not_found_text = findViewById(R.id.not_found_text);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        sound = soundPool.load(this, R.raw.task_completed, 1);

        recyclerView = findViewById(R.id.userListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserTasksList.this));

        snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.white))
                .setDuration(10000);

        DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);

        ListPosition = Util.LIST_POS;

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Calendar calendar;
        SimpleDateFormat simpleDateFormat;

        calendar = Calendar.getInstance();

        simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        Today = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Tomorrow = simpleDateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 7);
        NextWeek = simpleDateFormat.format(calendar.getTime());

        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();

        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));

        //Setup Adapter
        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                UserTasksList.this, this, arrayListColor.get(ListPosition));
        userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);

        addTaskName = findViewById(R.id.user_task_title);
        addTaskCard = findViewById(R.id.user_addTaskCard);
        userAddFab = findViewById(R.id.user_addTaskFab);

        userAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                createTaskPopup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        Objects.requireNonNull(getSupportActionBar()).setTitle(arrayListName.get(ListPosition));

        switch (arrayListColor.get(ListPosition)) {
            case "Theme Color 1":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_1)));
                break;
            case "Theme Color 2":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_2)));
                break;
            case "Theme Color 3":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_3)));
                break;
            case "Theme Color 4":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_4)));
                break;
            case "Theme Color 5":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_5)));
                break;
            case "Theme Color 6":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_6)));
                break;
            case "Theme Color 7":
                userAddFab.setBackgroundTintList(ColorStateList.valueOf(getResources()
                        .getColor(R.color.theme_color_7)));
                break;
        }

        DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));

        if (taskModels.size() == 0) {
            not_found_text.setVisibility(View.VISIBLE);
            not_found_image.setVisibility(View.VISIBLE);
        } else {
            not_found_text.setVisibility(View.GONE);
            not_found_image.setVisibility(View.GONE);
        }

        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                UserTasksList.this, this, arrayListColor.get(ListPosition));
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

        DueDateString = "";
        ReminderString = "";
        RepeatString = "";

        addTaskCard.setVisibility(View.VISIBLE);
        userAddFab.setVisibility(View.INVISIBLE);
        dueDateTag = findViewById(R.id.user_dueDateButton);
        reminderTag = findViewById(R.id.user_reminderButton);
        repeatTag = findViewById(R.id.user_repeatButton);
        dueDateChip = findViewById(R.id.user_dueDateChip);
        reminderChip = findViewById(R.id.user_reminderChip);
        repeatChip = findViewById(R.id.user_repeatChip);

        addTaskButton = findViewById(R.id.user_task_submit);

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
                PopupMenu popupMenu = new PopupMenu(UserTasksList.this, dueDateTag);
                popupMenu.getMenuInflater().inflate(R.menu.menu_due_date, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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

                                DatePickerDialog datePickerDialog = new DatePickerDialog(UserTasksList.this,
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
                PopupMenu popupMenu = new PopupMenu(UserTasksList.this, reminderTag);
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
                                        DatePickerDialog(UserTasksList.this,
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
                                                        new TimePickerDialog(UserTasksList.this,
                                                                new TimePickerDialog.OnTimeSetListener() {
                                                                    @Override
                                                                    public void onTimeSet(TimePicker view,
                                                                                          int hourOfDay,
                                                                                          int minute) {
                                                                        Calendar calendar = Calendar.getInstance();
                                                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                                        calendar.set(Calendar.MINUTE, minute);

                                                                        alarmHourOfDay = hourOfDay;
                                                                        alarmMinute = minute;
                                                                        alarmSecond = 0;

                                                                        alarmFlag = true;

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
                PopupMenu popupMenu = new PopupMenu(UserTasksList.this, repeatTag);
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
                    popupFlag = false;
                    addTaskCard.setVisibility(View.INVISIBLE);
                    userAddFab.setVisibility(View.VISIBLE);
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

    private void addTaskToDatabase(String task_title,
                                   String dueDateString,
                                   String reminderString,
                                   String repeatString) {

        String isImportant = "false";
        String isMyDay = "false";
        String isCompleted = "false";
        String CreatedTime = String.valueOf(new Date());

        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        ArrayList<String> arrayListTotal = listData.getUserListTotal();

        ArrayList<String> tempUserTasksid = listData.getUserTasksid();

        if (tempUserTasksid.size() == 0) {
            tempUserTasksid = new ArrayList<>();
        }

        //Adding data in SQLite Database
        DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);

        TaskModel taskModel = new TaskModel();
        taskModel.setTask_title(task_title);
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

        dbHandler.addUserTask(taskModel, arrayListName.get(ListPosition));

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
        taskObj.put("Title", task_title);
        taskObj.put("UserTasksList Name", arrayListName.get(ListPosition));
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

        //Setting up Alarm Manager..
        if (!TextUtils.isEmpty(reminderString.trim())) {
            if (alarmFlag) {
                createAlarm(task_title);
            }
        }
    }

    private void createAlarm(String TaskName) {
        alarmFlag = false;
        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmHourOfDay);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, alarmSecond);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        intent.putExtra("Task Name", TaskName);
        intent.putExtra("Task Id", dbHandler.getLastUserTaskId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                dbHandler.getLastUserTaskId(),
                intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id,
                intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    private void createCustomPopup() {
        builder = new AlertDialog.Builder(UserTasksList.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater()
                .inflate(R.layout.custom_repeat_popup, null);

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

        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(UserTasksList.this,
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
                                RepeatString = "Every " + value + " weeks";
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
            userAddFab.setVisibility(View.VISIBLE);

            popupFlag = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_tasks_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_rename_list:
                //Rename User List
                renameUserList();
                break;
            case R.id.user_sort_list:
                //Sorting User List
                AlertDialog.Builder builder = new AlertDialog.Builder(UserTasksList.this);

                builder.setTitle("Sort by");

                String[] names = {"Creation date", "Importance", "Added to My Day", "Alphabetically"};
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
            case R.id.user_change_theme:
                //Hide Tasks..
                changeThemeBuilder = new AlertDialog.Builder(UserTasksList.this);
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
            case R.id.user_completed_tasks_list:
                //Hide Completed tasks..
                hideCompletedTasks();
                break;
            case R.id.user_delete_list:
                //Delete UserList..
                DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
                List<String> settings = dbHandler.getAllSettings();

                if (settings.get(2).equals("true")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(UserTasksList.this);

                    b.setTitle("Are you sure?");
                    b.setMessage("List will be permanently deleted.");
                    b.setNegativeButton("Cancel", null);

                    b.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                            deleteUserList();
                        }
                    });

                    AlertDialog alertD = b.create();
                    alertD.show();
                } else {
                    onBackPressed();
                    deleteUserList();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteUserList() {
        DatabaseHandler handler = new DatabaseHandler(UserTasksList.this);
        ListData listData = ListData.getInstance();
        ArrayList<String> arrayList = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        ArrayList<String> arrayListTotal = listData.getUserListTotal();

        handler.deleteList(arrayList.get(ListPosition));
        handler.deleteMyDayList(arrayList.get(ListPosition));
        handler.deleteImportantList(arrayList.get(ListPosition));
        handler.deleteUserTaskList(arrayList.get(ListPosition));

        ArrayList<String> tempUserTasksid = handler.getAllUserTaskId(arrayList.get(ListPosition));

        arrayList.remove(ListPosition);
        arrayListColor.remove(ListPosition);
        arrayListTotal.remove(ListPosition);

        listData.setUserList(arrayList);
        listData.setUserListColor(arrayListColor);
        listData.setUserListTotal(arrayListTotal);
        listData.setUserTasksid(tempUserTasksid);

        Map<String, Object> idObj = new HashMap<>();
        idObj.put("User Tasks ID", String.valueOf(listData.getUserTasksid()));
        idObj.put("Tasks ID", String.valueOf(listData.getTasksid()));

        DocumentReference docIdRef = db.collection("List Id").document(currentUserEmail);

        docIdRef.delete();
        docIdRef.set(idObj);

        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists")
                .document(currentUserEmail);

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("List Name", String.valueOf(listData.getUserList()));
        updateTotalObj.put("List Color", String.valueOf(listData.getUserListColor()));
        updateTotalObj.put("List Total", String.valueOf(listData.getUserListTotal()));

        docRef.update(updateTotalObj);

        onBackPressed();
    }

    private void hideCompletedTasks() {
        DatabaseHandler handler = new DatabaseHandler(UserTasksList.this);
        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        List<TaskModel> taskModels = handler.hideCompletedUserTasks(arrayListName.get(ListPosition));

        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                UserTasksList.this, this, arrayListColor.get(ListPosition));
        userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);
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
        DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);

        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        ArrayList<String> arrayListTotal = listData.getUserListTotal();

        arrayListColor.set(ListPosition, c);
        listData.setUserListColor(arrayListColor);

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
        updateTotalObj.put("List Color", String.valueOf(listData.getUserListColor()));

        docRef.update(updateTotalObj);

        onStart();
    }

    private void sortingUserList(int which, DialogInterface d) {
        DatabaseHandler handler = new DatabaseHandler(UserTasksList.this);
        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        List<TaskModel> taskModels;
        // setup the alert builder
        switch (which) {
            case 0:
                //Creation Date
                onStart();
                break;
            case 1:
                //Importance
                taskModels = handler.getAllUserTasksImportance(arrayListName.get(ListPosition));

                userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                        UserTasksList.this, this, arrayListColor.get(ListPosition));
                userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
                recyclerView.setAdapter(userTasksRecyclerViewAdapter);
                break;
            case 2:
                //Due Date
                taskModels = handler.getAllUserTasksMyDay(arrayListName.get(ListPosition));

                userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                        UserTasksList.this, this, arrayListColor.get(ListPosition));
                userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
                recyclerView.setAdapter(userTasksRecyclerViewAdapter);
                break;
            case 3:
                taskModels = handler.getAllUserTasksAlpha(arrayListName.get(ListPosition));

                userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                        UserTasksList.this, this, arrayListColor.get(ListPosition));
                userTasksRecyclerViewAdapter.notifyItemChanged(userTasksRecyclerViewAdapter.getItemCount());
                recyclerView.setAdapter(userTasksRecyclerViewAdapter);
                break;
        }

        d.dismiss();
    }

    private void renameUserList() {
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();

        renameBuilder = new AlertDialog.Builder(UserTasksList.this);
        @SuppressLint("InflateParams") View view = getLayoutInflater()
                .inflate(R.layout.activity_create_list_popup, null);

        popup_title_text = view.findViewById(R.id.popup_title_text);
        popup_title_text.setText(R.string.rename_text);

        list_title = view.findViewById(R.id.list_title);
        list_title.setText(arrayListName.get(ListPosition));

        list_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(list_title.getText().toString().trim())) {
                    create_button.setTextColor(getResources().getColor(R.color.white));
                } else {
                    create_button.setTextColor(getResources().getColor(R.color.background_color_3));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        cancel_button = view.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });

        create_button = view.findViewById(R.id.create_button);
        create_button.setText(R.string.save_text);

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (create_button.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    renameDialog.dismiss();
                    String List_Title = list_title.getText().toString().trim();
                    String tempList = arrayListName.get(ListPosition);

                    ListData listData = ListData.getInstance();
                    final ArrayList<String> tempUserList = listData.getUserList();
                    final ArrayList<String> tempUserListColor = listData.getUserListColor();
                    final ArrayList<String> tempUserListTotal = listData.getUserListTotal();

                    //Checking if list is already available then add number after it..
                    if (tempUserList.contains(List_Title)) {
                        int cnt = 1;
                        while (true) {
                            if (tempUserList.contains(List_Title+" ("+cnt+")")) {
                                cnt++;
                            } else {
                                tempUserList.set(ListPosition, List_Title+" ("+cnt+")");
                                break;
                            }
                        }
                    } else {
                        tempUserList.set(ListPosition, List_Title);
                    }

                    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());

                    ListModel listModel = new ListModel();
                    listModel.setList_name(tempUserList.get(ListPosition));
                    listModel.setList_color(tempUserListColor.get(ListPosition));
                    listModel.setList_total(tempUserListTotal.get(ListPosition));

                    dbHandler.updateRenameList(listModel, tempList);
                    dbHandler.updateUserTaskList(tempList, tempUserList.get(ListPosition));
                    dbHandler.updateImportantList(tempList, tempUserList.get(ListPosition));
                    dbHandler.updateMyDayList(tempList, tempUserList.get(ListPosition));

                    listData.setUserList(tempUserList);
                    listData.setUserListColor(tempUserListColor);
                    listData.setUserListTotal(tempUserListTotal);

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    final String currentUserId = userData.getUserId();

                    DocumentReference document_reference= db.collection("userLists")
                            .document(currentUserEmail);

                    Map<String, String> listObj = new HashMap<>();
                    listObj.put("List Id", currentUserId);
                    listObj.put("List Name", String.valueOf(listData.getUserList()));
                    listObj.put("List Color", String.valueOf(listData.getUserListColor()));
                    listObj.put("List Total", String.valueOf(listData.getUserListTotal()));

                    document_reference.delete();
                    document_reference.set(listObj);

                    onStart();
                }
            }
        });

        renameBuilder.setView(view);
        renameDialog = renameBuilder.create();
        renameDialog.show();
    }

    @Override
    public void onTaskClick(int position) {
        addTaskCard.setVisibility(View.INVISIBLE);
        userAddFab.setVisibility(View.VISIBLE);
        Util.IS_TASKS = false;
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListColor = listData.getUserListColor();

        //Navigate to new Activity
        startActivity(new Intent(UserTasksList.this, TaskDetail.class)
                .putExtra("themeColor", arrayListColor.get(ListPosition))
                .putExtra("keyPosition", position)
                .putExtra("listPosition", ListPosition));
    }

    @Override
    public void onCheckboxUnCheck(int position, TextView task_name, ImageButton checkbox_uncheck,
                                  final ImageButton checkbox_check) {
        //Updating task detail
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();
        final ArrayList<String> arrayListColor = listData.getUserListColor();

        final DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
        List<String> settings = dbHandler.getAllSettings();

        //Playing task completed sound
        if (settings.get(1).equals("true")) {
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }


        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));
        final TaskModel taskModel = taskModels.get(position);

        task_name.setTextColor(getResources()
                .getColor(R.color.background_color_5));
        checkbox_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(UserTasksList.this,
                        R.anim.fade_animation);
                checkbox_check.startAnimation(animation);
                checkbox_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        switch (arrayListColor.get(ListPosition)) {
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

        taskModel.setTask_is_completed("true");
        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
        }

        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
        id = dbHandler.getImportantTaskId(String.valueOf(
                taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
        }

        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                + "/UserTasks")
                .document(String.valueOf(taskModel.getTasks_id()));

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("is Completed", "true");
        docRef.update(updateTotalObj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public void onCheckboxCheck(int position, TextView task_name, ImageButton checkbox_check,
                                ImageButton checkbox_uncheck) {
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();

        final DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));
        final TaskModel taskModel = taskModels.get(position);

        //Updating task detail
        task_name.setTextColor(getResources()
                .getColor(R.color.white));
        checkbox_uncheck.setVisibility(View.VISIBLE);
        checkbox_check.setVisibility(View.GONE);

        taskModel.setTask_is_completed("false");
        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
        }

        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
        id = dbHandler.getImportantTaskId(String.valueOf(
                taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
        }

        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                + "/UserTasks")
                .document(String.valueOf(taskModel.getTasks_id()));

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("is Completed", "false");
        docRef.update(updateTotalObj);
    }

    @Override
    public void onImportantUnCheck(int position, final ImageButton important_check, ImageButton important_uncheck) {
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();
        final ArrayList<String> arrayListColor = listData.getUserListColor();

        final DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));
        final TaskModel taskModel = taskModels.get(position);

        //Updating task detail
        important_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(UserTasksList.this,
                        R.anim.fade_animation);
                important_check.startAnimation(animation);
                important_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        switch (arrayListColor.get(ListPosition)) {
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

        taskModel.setTask_is_important("true");
        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
        }

        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
        dbHandler.addImportantTask(taskModel, arrayListName.get(ListPosition));

        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                + "/UserTasks")
                .document(String.valueOf(taskModel.getTasks_id()));

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("is Important", "true");
        docRef.update(updateTotalObj);
    }

    @Override
    public void onImportantCheck(int position, ImageButton important_check, ImageButton important_uncheck) {
        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();

        final DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
        List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));
        final TaskModel taskModel = taskModels.get(position);

        //Updating task detail
        important_uncheck.setVisibility(View.VISIBLE);
        important_check.setVisibility(View.GONE);

        taskModel.setTask_is_important("false");
        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
        }

        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
        id = dbHandler.getImportantTaskId(String.valueOf(
                taskModel.getTasks_id()),
                arrayListName.get(ListPosition));
        if (id.size() != 0) {
            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
        }

        UserData userData = UserData.getInstance();
        final String currentUserEmail = userData.getUserEmail();
        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                + "/UserTasks")
                .document(String.valueOf(taskModel.getTasks_id()));

        Map<String, Object> updateTotalObj = new HashMap<>();
        updateTotalObj.put("is Important", "false");
        docRef.update(updateTotalObj);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int rowPosition = viewHolder.getAdapterPosition();
            final ListData listData = ListData.getInstance();
            final ArrayList<String> arrayListName = listData.getUserList();
            final ArrayList<String> arrayListTotal = listData.getUserListTotal();
            final ArrayList<String> arrayListColor = listData.getUserListColor();

            final DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);
            List<TaskModel> taskModels = dbHandler.getAllUserTasks(arrayListName.get(ListPosition));

            final TaskModel taskModel = taskModels.get(rowPosition);

            addTaskCard.setVisibility(View.INVISIBLE);
            userAddFab.setVisibility(View.VISIBLE);

            InputMethodManager imm = (InputMethodManager)getSystemService
                    (Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(addTaskName.getWindowToken(), 0);

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    //Updating task detail
                    if (taskModel.getTask_is_my_day().equals("false")) {
                        taskModel.setTask_is_my_day("true");

                        dbHandler.addMyDayTask(taskModel, arrayListName.get(ListPosition));
                        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
                        List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(ListPosition));
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
                    } else {
                        taskModel.setTask_is_my_day("false");
                        List<String> id = dbHandler.getMyDayTaskId(
                                String.valueOf(taskModel.getTasks_id()), arrayListName.get(ListPosition));
                        if (id.size() != 0) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                        }

                        dbHandler.updateUserTask(taskModel, arrayListName.get(ListPosition));
                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(ListPosition));
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("is My Day", "false");
                        docRef.update(updateTotalObj);
                    }

                    onStart();
                    break;
                    case ItemTouchHelper.LEFT:
                        boolean isImportantDeleted = false;

                        cancelAlarm(taskModel.getTasks_id());

                        dbHandler.deleteUserTask(taskModel);
                        List<String> id = dbHandler.getMyDayTaskId(
                                String.valueOf(taskModel.getTasks_id()), arrayListName.get(ListPosition));
                        String finalTempId2 = "";
                        if (id.size() != 0) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            finalTempId2 = id.get(0);
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(ListPosition));
                        if (id.size() != 0) {
                            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                            isImportantDeleted = true;
                        }

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

                        String tempId = "0";
                        if (id.size() != 0) {
                            tempId = id.get(0);
                        }
                        final boolean finalIsImportantDeleted = isImportantDeleted;
                        final String finalTempId = tempId;
                        final String finalTempIdTemp = finalTempId2;
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addDeletedTaskToDatabase(taskModel, finalTempId, finalTempIdTemp,
                                        finalIsImportantDeleted);
                            }
                        }).show();

                        onStart();
                        break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_trash_white)
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.swipe_red))
                    .addSwipeRightActionIcon(R.drawable.ic_sun_white)
                    .addSwipeRightBackgroundColor(getResources().getColor(R.color.swipe_blue))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void addDeletedTaskToDatabase(TaskModel deletedModel, String lastTempId, String lastTempId2, boolean isImportantDeleted) {
        String task_title = deletedModel.getTask_title();
        String dueDateString = deletedModel.getTask_due_date();
        String reminderString = deletedModel.getTask_reminder();
        String repeatString = deletedModel.getTask_repeat();
        String deletedRepeatDays = deletedModel.getTask_repeat_days();
        String isImportant = deletedModel.getTask_is_important();
        String isMyDay = deletedModel.getTask_is_my_day();
        String isCompleted = deletedModel.getTask_is_completed();
        String CreatedTime = deletedModel.getTask_created_time();

        ListData listData = ListData.getInstance();
        ArrayList<String> arrayListName = listData.getUserList();
        ArrayList<String> arrayListColor = listData.getUserListColor();
        ArrayList<String> arrayListTotal = listData.getUserListTotal();

        ArrayList<String> tempUserTasksid = listData.getUserTasksid();
        if (tempUserTasksid.size() == 0) {
            tempUserTasksid = new ArrayList<>();
        }
        //Adding data in SQLite Database
        DatabaseHandler dbHandler = new DatabaseHandler(UserTasksList.this);

        TaskModel taskModel = new TaskModel();
        taskModel.setTask_title(task_title);
        taskModel.setTask_due_date(dueDateString);
        taskModel.setTask_repeat(repeatString);
        taskModel.setTask_repeat_days(deletedRepeatDays);
        taskModel.setTask_reminder(reminderString);
        taskModel.setTask_is_important(isImportant);
        taskModel.setTask_is_my_day(isMyDay);
        taskModel.setTask_is_completed(isCompleted);
        taskModel.setTask_created_time(CreatedTime);

        dbHandler.addUserTask(taskModel, arrayListName.get(ListPosition));

        if (isImportantDeleted) {
            dbHandler.addRemovedImportantTask(deletedModel,
                    arrayListName.get(ListPosition),
                    Integer.parseInt(lastTempId));
        }

        if (!TextUtils.isEmpty(lastTempId2.trim())) {
            dbHandler.addRemovedMyDayTask(deletedModel,
                    arrayListName.get(ListPosition),
                    Integer.parseInt(lastTempId));
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
        taskObj.put("Title", task_title);
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
