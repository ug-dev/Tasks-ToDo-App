package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskDetail extends AppCompatActivity {
    private int keyPosition;
    private int listPosition;
    private String themeColor;

    private String DueDateString = "";
    private String ReminderString = "";
    private String RepeatString = "";

    private String Today;
    private String Tomorrow;
    private String NextWeek;

    private String DueToday = "Due Today";
    private String DueTomorrow = "Due Tomorrow";
    private String DueNextWeek;
    private String DueDatePickString;
    private String RemindAtString = "Remind me at";
    private String ReminderDateTimePickString;
    private String ReminderDatePickString;
    private ArrayList<String> dayList = new ArrayList<>();

    RelativeLayout myDay_row, reminder_row, dueDate_row, repeat_row;
    ImageButton myDay_close, reminder_close, dueDate_close, repeat_close, delete, checkbox_checked, important_check;
    TextView myDay_text, reminder_time_text, reminder_date_text, dueDate_text, repeat_text, repeat_days_text,
            time_ago_text;
    ImageView myDay_image, reminder_image, dueDate_image, repeat_image;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText day_input;
    private Spinner day_spinner;
    private CheckBox day_sun, day_mon, day_tue, day_wed, day_thu, day_fri, day_sat;
    private TextView day_cancel, day_done;
    private TableRow custom_days, custom_days2;
    private String value;
    private String SpinnerString;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean DatePickFlag;
    private boolean DateTimePickFlag;
    private boolean CustomPickFlag;
    private boolean currentSpinnerInput;

    private SoundPool soundPool;
    private int sound;

    List<TaskModel> taskModels;
    TaskModel taskModel;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Util.IS_IMPORTANT_DELETED = false;
        Util.IS_MY_DAY_DELETED = false;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        sound = soundPool.load(this, R.raw.task_completed, 1);

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

        Bundle task_detail_bundle = getIntent().getExtras();
        themeColor = Objects.requireNonNull(task_detail_bundle).getString("themeColor");

        if (Util.IS_TASKS) {
            keyPosition = task_detail_bundle.getInt("tasksKeyPosition");
        } else {
            keyPosition = task_detail_bundle.getInt("keyPosition");
            listPosition = task_detail_bundle.getInt("listPosition");
        }

        //Just Adding Back button...
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        final ListData listData = ListData.getInstance();
        final ArrayList<String> arrayListName = listData.getUserList();
        final ArrayList<String> arrayListTotal = listData.getUserListTotal();
        final ArrayList<String> arrayListColor = listData.getUserListColor();

        if (Util.IS_TASKS) {
            getSupportActionBar().setTitle("Tasks");
        } else {
            getSupportActionBar().setTitle(arrayListName.get(listPosition));
        }

        final DatabaseHandler dbHandler = new DatabaseHandler(TaskDetail.this);

        if (Util.IS_TASKS) {
            taskModels = dbHandler.getAllTasks();
            taskModel = taskModels.get(keyPosition);
        } else {
            taskModels = dbHandler.getAllUserTasks(arrayListName.get(listPosition));
            taskModel = taskModels.get(keyPosition);
        }

        checkbox_checked = findViewById(R.id.taskDetail_checkbox_check);
        important_check = findViewById(R.id.taskDetail_important_check);
        myDay_image = findViewById(R.id.taskDetail_myDay_image);
        myDay_text = findViewById(R.id.taskDetail_myDay_text);
        reminder_image = findViewById(R.id.taskDetail_reminder_image);
        reminder_time_text = findViewById(R.id.taskDetail_reminder_time_text);
        dueDate_image = findViewById(R.id.taskDetail_dueDate_image);
        dueDate_text = findViewById(R.id.taskDetail_dueDate_text);
        repeat_image = findViewById(R.id.taskDetail_repeat_image);
        repeat_text = findViewById(R.id.taskDetail_repeat_text);

        switch (themeColor) {
            case "Theme Color 1":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_1));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_1));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                }
                break;
            case "Theme Color 2":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_2));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_2));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                }
                break;
            case "Theme Color 3":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_3));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_3));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                }
                break;
            case "Theme Color 4":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_4));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_4));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                }
                break;
            case "Theme Color 5":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_5));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_5));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                }
                break;
            case "Theme Color 6":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_6));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_6));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                }
                break;
            case "Theme Color 7":
                checkbox_checked.setColorFilter(getResources().getColor(R.color.theme_color_7));
                important_check.setColorFilter(getResources().getColor(R.color.theme_color_7));
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    myDay_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_reminder().trim())) {
                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_due_date().trim())) {
                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                }
                if (!TextUtils.isEmpty(taskModel.getTask_repeat().trim())) {
                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                }
                break;
        }

        final ImageButton checkbox_unchecked;

        checkbox_unchecked = findViewById(R.id.taskDetail_checkbox_uncheck);
        final EditText task_name = findViewById(R.id.taskDetail_task_name);
        final ImageButton important_uncheck = findViewById(R.id.taskDetail_important_uncheck);

        //Default Values
        task_name.setText(taskModel.getTask_title());
        task_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    if (Util.IS_TASKS) {
                        taskModel.setTask_title(s.toString().trim());
                        dbHandler.updateTask(taskModel);
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/Tasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Title", s.toString().trim());
                        docRef.update(updateTotalObj);
                    } else {
                        taskModel.setTask_title(s.toString().trim());
                        dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Title", s.toString().trim());
                        docRef.update(updateTotalObj);
                    }
                } else {
                    if (Util.IS_TASKS) {
                        taskModel.setTask_title(taskModel.getTask_title());
                        dbHandler.updateTask(taskModel);
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/Tasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Title", taskModel.getTask_title());
                        docRef.update(updateTotalObj);
                    } else {
                        taskModel.setTask_title(taskModel.getTask_title());
                        dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Title", taskModel.getTask_title());
                        docRef.update(updateTotalObj);
                    }
                }
            }
        });


        if (taskModel.getTask_is_completed().equals("true")) {
            task_name.setTextColor(getResources()
                    .getColor(R.color.background_color_5));
            checkbox_unchecked.setVisibility(View.GONE);
            checkbox_checked.setVisibility(View.VISIBLE);
        }

        if (taskModel.getTask_is_important().equals("true")) {
            important_uncheck.setVisibility(View.GONE);
            important_check.setVisibility(View.VISIBLE);
        }

        checkbox_unchecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> settings = dbHandler.getAllSettings();

                //Playing task completed sound
                if (settings.get(1).equals("true")) {
                    soundPool.play(sound, 1, 1, 0, 0, 1);
                }

                task_name.setTextColor(getResources()
                        .getColor(R.color.background_color_5));
                checkbox_unchecked.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(TaskDetail.this,
                                R.anim.fade_animation);
                        checkbox_checked.startAnimation(animation);
                        checkbox_checked.setVisibility(View.VISIBLE);
                    }
                }, 50);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_is_completed("true");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

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
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
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
            }
        });

        checkbox_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_name.setTextColor(getResources()
                        .getColor(R.color.white));
                checkbox_unchecked.setVisibility(View.VISIBLE);
                checkbox_checked.setVisibility(View.GONE);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_is_completed("false");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

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
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
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
            }
        });

        important_uncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                important_uncheck.setVisibility(View.GONE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(TaskDetail.this,
                                R.anim.fade_animation);
                        important_check.startAnimation(animation);
                        important_check.setVisibility(View.VISIBLE);
                    }
                }, 50);

                //Updating task detail

                if (Util.IS_TASKS) {
                    taskModel.setTask_is_important("true");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    dbHandler.addImportantTask(taskModel, "Tasks");

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
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    dbHandler.addImportantTask(taskModel, arrayListName.get(listPosition));

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
        });

        important_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                important_check.setVisibility(View.GONE);
                important_uncheck.setVisibility(View.VISIBLE);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_is_important("false");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                        Util.IS_IMPORTANT_DELETED = true;
                    }

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
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                        Util.IS_IMPORTANT_DELETED = true;
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
            }
        });

        myDay_row = findViewById(R.id.taskDetail_myDay_row);
        myDay_close = findViewById(R.id.taskDetail_myDay_close);

        //Default values
        if (taskModel.getTask_is_my_day().equals("true")) {
            myDay_text.setText(R.string.taskDetail_myDay_text2);
            myDay_close.setVisibility(View.VISIBLE);
        }

        myDay_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDay_image.setColorFilter(getResources().getColor(R.color.background_color_6));
                myDay_text.setText(R.string.taskDetail_myDay_text);
                myDay_text.setTextColor(getResources().getColor(R.color.background_color_6));
                myDay_close.setVisibility(View.GONE);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_is_my_day("false");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                        Util.IS_MY_DAY_DELETED = true;
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/Tasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("is My Day", "false");
                    docRef.update(updateTotalObj);
                } else {
                    taskModel.setTask_is_my_day("false");
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));

                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                        Util.IS_MY_DAY_DELETED = true;
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
            }
        });

        myDay_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskModel.getTask_is_my_day().equals("true")) {
                    myDay_image.setColorFilter(getResources().getColor(R.color.background_color_6));
                    myDay_text.setTextColor(getResources().getColor(R.color.background_color_6));
                    myDay_text.setText(R.string.taskDetail_myDay_text);
                    myDay_close.setVisibility(View.GONE);

                    //Updating task detail
                    if (Util.IS_TASKS) {
                        taskModel.setTask_is_my_day("false");
                        dbHandler.updateTask(taskModel);
                        List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            Util.IS_MY_DAY_DELETED = true;
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/Tasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("is My Day", "false");
                        docRef.update(updateTotalObj);
                    } else {
                        taskModel.setTask_is_my_day("false");
                        dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                        List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            Util.IS_MY_DAY_DELETED = true;
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
                } else {
                    myDay_text.setText(R.string.taskDetail_myDay_text2);
                    myDay_close.setVisibility(View.VISIBLE);
                    switch (themeColor) {
                        case "Theme Color 1":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                            }
                            break;
                        case "Theme Color 2":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                            }
                            break;
                        case "Theme Color 3":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                            }
                            break;
                        case "Theme Color 4":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                            }
                            break;
                        case "Theme Color 5":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                            }
                            break;
                        case "Theme Color 6":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                            }
                            break;
                        case "Theme Color 7":
                            if (taskModel.getTask_is_my_day().equals("false")) {
                                myDay_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                myDay_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                            }
                            break;
                    }

                    //Updating task detail
                    if (Util.IS_TASKS) {
                        taskModel.setTask_is_my_day("true");
                        dbHandler.updateTask(taskModel);
                        List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        dbHandler.addMyDayTask(taskModel, "Tasks");

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
                        dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                        List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayListName.get(listPosition));
                        if (id.size() != 0) {
                            dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                        }

                        dbHandler.addMyDayTask(taskModel, arrayListName.get(listPosition));

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("is My Day", "true");
                        docRef.update(updateTotalObj);
                    }
                }
            }
        });

        reminder_row = findViewById(R.id.taskDetail_reminder_row);
        reminder_close = findViewById(R.id.taskDetail_reminder_close);
        reminder_date_text = findViewById(R.id.taskDetail_reminder_date_text);

        //Default values
        if (!taskModel.getTask_reminder().equals("")) {
            String[] separated = taskModel.getTask_reminder().split(" ");
            String time_text = "Remind me at " + separated[0] + " " + separated[1];
            String date_text = separated[2] + " " + separated[3] + " " + separated[4] + " " + separated[5];

            reminder_time_text.setText(time_text);

            if (date_text.equals(Today)) {
                reminder_date_text.setText(R.string.today_text);
            } else if (date_text.equals(Tomorrow)) {
                reminder_date_text.setText(R.string.reminderTomorrowAction);
            } else {
                reminder_date_text.setText(date_text);
            }

            reminder_close.setVisibility(View.VISIBLE);
            reminder_date_text.setVisibility(View.VISIBLE);
        }

        reminder_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TaskDetail.this, reminder_time_text);
                popupMenu.getMenuInflater().inflate(R.menu.menu_reminder, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.reminderTomorrowAction:
                                ReminderString = "9:00 AM " + Tomorrow;
                                String RemindTomorrowString = RemindAtString + " 9:00 AM";
                                reminder_time_text.setText(RemindTomorrowString);
                                reminder_date_text.setText(R.string.reminderTomorrowAction);
                                break;
                            case R.id.reminderNextWeekAction:
                                ReminderString = "9:00 AM " + NextWeek;
                                String RemindNextWeekString = RemindAtString + " 9:00 AM ";
                                reminder_time_text.setText(RemindNextWeekString);
                                reminder_date_text.setText(NextWeek);
                                break;
                            case R.id.reminderPickAction:
                                DateTimePickFlag = true;

                                DatePickerDialog datePickerDialog = new
                                        DatePickerDialog(TaskDetail.this,
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
                                                        new TimePickerDialog(TaskDetail.this,
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
                                                                                        .toString();

                                                                        ReminderString = DateFormat.format("h:mm a",
                                                                                calendar).toString()
                                                                                + " " +
                                                                                ReminderDatePickString;

                                                                        reminder_time_text.setText(ReminderDateTimePickString);
                                                                        reminder_date_text.setText(ReminderDatePickString);

                                                                        reminder_close.setVisibility(View.VISIBLE);
                                                                        reminder_date_text.setVisibility(View.VISIBLE);

                                                                        switch (themeColor) {
                                                                            case "Theme Color 1":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                                                                                break;
                                                                            case "Theme Color 2":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                                                                                break;
                                                                            case "Theme Color 3":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                                                                                break;
                                                                            case "Theme Color 4":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                                                                                break;
                                                                            case "Theme Color 5":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                                                                                break;
                                                                            case "Theme Color 6":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                                                                                break;
                                                                            case "Theme Color 7":
                                                                                reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                                                                reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                                                                                break;
                                                                        }

                                                                        //Updating task detail
                                                                        taskModel.setTask_reminder(ReminderString);
                                                                        if (Util.IS_TASKS) {
                                                                            dbHandler.updateTask(taskModel);
                                                                            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                                                                    taskModel.getTasks_id()),
                                                                                    "Tasks");
                                                                            if (id.size() != 0) {
                                                                                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                                                            }

                                                                            id = dbHandler.getImportantTaskId(String.valueOf(
                                                                                    taskModel.getTasks_id()),
                                                                                    "Tasks");
                                                                            if (id.size() != 0) {
                                                                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                                                            }

                                                                            UserData userData = UserData.getInstance();
                                                                            final String currentUserEmail = userData.getUserEmail();
                                                                            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                                                                    + "/Tasks")
                                                                                    .document(String.valueOf(taskModel.getTasks_id()));

                                                                            Map<String, Object> updateTotalObj = new HashMap<>();
                                                                            updateTotalObj.put("Reminder", ReminderString);
                                                                            docRef.update(updateTotalObj);
                                                                        } else {
                                                                            dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                                                                            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                                                                    taskModel.getTasks_id()),
                                                                                    arrayListName.get(listPosition));
                                                                            if (id.size() != 0) {
                                                                                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                                                            }

                                                                            id = dbHandler.getImportantTaskId(String.valueOf(
                                                                                    taskModel.getTasks_id()),
                                                                                    arrayListName.get(listPosition));
                                                                            if (id.size() != 0) {
                                                                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                                                            }

                                                                            UserData userData = UserData.getInstance();
                                                                            final String currentUserEmail = userData.getUserEmail();
                                                                            DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                                                                    + "/UserTasks")
                                                                                    .document(String.valueOf(taskModel.getTasks_id()));

                                                                            Map<String, Object> updateTotalObj = new HashMap<>();
                                                                            updateTotalObj.put("Reminder", ReminderString);
                                                                            docRef.update(updateTotalObj);
                                                                        }

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
                            reminder_close.setVisibility(View.VISIBLE);
                            reminder_date_text.setVisibility(View.VISIBLE);

                            switch (themeColor) {
                                case "Theme Color 1":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                                    break;
                                case "Theme Color 2":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                                    break;
                                case "Theme Color 3":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                                    break;
                                case "Theme Color 4":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                                    break;
                                case "Theme Color 5":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                                    break;
                                case "Theme Color 6":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                                    break;
                                case "Theme Color 7":
                                    reminder_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                    reminder_time_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                                    break;
                            }

                            //Updating task detail
                            if (Util.IS_TASKS) {
                                taskModel.setTask_reminder(ReminderString);
                                dbHandler.updateTask(taskModel);
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/Tasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Reminder", ReminderString);
                                docRef.update(updateTotalObj);
                            } else {
                                taskModel.setTask_reminder(ReminderString);
                                dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/UserTasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Reminder", ReminderString);
                                docRef.update(updateTotalObj);
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        reminder_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder_time_text.setText(R.string.reminder);

                ReminderString = "";

                reminder_time_text.setTextColor(getResources()
                        .getColor(R.color.background_color_6));
                reminder_image.setColorFilter(getResources()
                        .getColor(R.color.background_color_6));
                reminder_close.setVisibility(View.GONE);
                reminder_date_text.setVisibility(View.GONE);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_reminder(ReminderString);
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/Tasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Reminder", ReminderString);
                    docRef.update(updateTotalObj);
                } else {
                    taskModel.setTask_reminder(ReminderString);
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/UserTasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Reminder", ReminderString);
                    docRef.update(updateTotalObj);
                }
            }
        });

        dueDate_row = findViewById(R.id.taskDetail_dueDate_row);
        dueDate_close = findViewById(R.id.taskDetail_dueDate_close);

        //Default values
        if (!taskModel.getTask_due_date().equals("")) {
            String date_text = taskModel.getTask_due_date();

            if (date_text.equals(Today)) {
                dueDate_text.setText(DueToday);
            } else if (date_text.equals(Tomorrow)) {
                dueDate_text.setText(DueTomorrow);
            } else {
                String text = "Due " + date_text;
                dueDate_text.setText(text);
            }

            dueDate_close.setVisibility(View.VISIBLE);
        }

        dueDate_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TaskDetail.this, dueDate_text);
                popupMenu.getMenuInflater().inflate(R.menu.menu_due_date, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.dueDateTodayAction:
                                DueDateString = Today;
                                dueDate_text.setText(DueToday);
                                break;
                            case R.id.dueDateTomorrowAction:
                                DueDateString = Tomorrow;
                                dueDate_text.setText(DueTomorrow);
                                break;
                            case R.id.dueDateNextWeekAction:
                                DueDateString = NextWeek;
                                DueNextWeek = "Due " + NextWeek;
                                dueDate_text.setText(DueNextWeek);
                                break;
                            case R.id.dueDatePickAction:
                                DatePickFlag = true;

                                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskDetail.this,
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

                                                dueDate_text.setText(DueDatePickString);
                                                dueDate_close.setVisibility(View.VISIBLE);

                                                switch (themeColor) {
                                                    case "Theme Color 1":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                                                        break;
                                                    case "Theme Color 2":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                                                        break;
                                                    case "Theme Color 3":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                                                        break;
                                                    case "Theme Color 4":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                                                        break;
                                                    case "Theme Color 5":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                                                        break;
                                                    case "Theme Color 6":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                                                        break;
                                                    case "Theme Color 7":
                                                        dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                                        dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                                                        break;
                                                }

                                                //Updating task detail
                                                if (Util.IS_TASKS) {
                                                    taskModel.setTask_due_date(DueDateString);
                                                    dbHandler.updateTask(taskModel);
                                                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                                            taskModel.getTasks_id()),
                                                            "Tasks");
                                                    if (id.size() != 0) {
                                                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                                    }

                                                    id = dbHandler.getImportantTaskId(String.valueOf(
                                                            taskModel.getTasks_id()),
                                                            "Tasks");
                                                    if (id.size() != 0) {
                                                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                                    }

                                                    UserData userData = UserData.getInstance();
                                                    final String currentUserEmail = userData.getUserEmail();
                                                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                                            + "/Tasks")
                                                            .document(String.valueOf(taskModel.getTasks_id()));

                                                    Map<String, Object> updateTotalObj = new HashMap<>();
                                                    updateTotalObj.put("Due Date", DueDateString);
                                                    docRef.update(updateTotalObj);
                                                } else {
                                                    taskModel.setTask_due_date(DueDateString);
                                                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                                                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                                            taskModel.getTasks_id()),
                                                            arrayListName.get(listPosition));
                                                    if (id.size() != 0) {
                                                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                                    }

                                                    id = dbHandler.getImportantTaskId(String.valueOf(
                                                            taskModel.getTasks_id()),
                                                            arrayListName.get(listPosition));
                                                    if (id.size() != 0) {
                                                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                                    }

                                                    UserData userData = UserData.getInstance();
                                                    final String currentUserEmail = userData.getUserEmail();
                                                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                                            + "/UserTasks")
                                                            .document(String.valueOf(taskModel.getTasks_id()));

                                                    Map<String, Object> updateTotalObj = new HashMap<>();
                                                    updateTotalObj.put("Due Date", DueDateString);
                                                    docRef.update(updateTotalObj);
                                                }

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
                            dueDate_close.setVisibility(View.VISIBLE);

                            switch (themeColor) {
                                case "Theme Color 1":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                                    break;
                                case "Theme Color 2":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                                    break;
                                case "Theme Color 3":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                                    break;
                                case "Theme Color 4":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                                    break;
                                case "Theme Color 5":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                                    break;
                                case "Theme Color 6":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                                    break;
                                case "Theme Color 7":
                                    dueDate_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                    dueDate_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                                    break;
                            }

                            //Updating task detail
                            if (Util.IS_TASKS) {
                                taskModel.setTask_due_date(DueDateString);
                                dbHandler.updateTask(taskModel);
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/Tasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Due Date", DueDateString);
                                docRef.update(updateTotalObj);
                            } else {
                                taskModel.setTask_due_date(DueDateString);
                                dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/UserTasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Due Date", DueDateString);
                                docRef.update(updateTotalObj);
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        dueDate_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DueDateString = "";

                dueDate_text.setText(R.string.add_due_date);
                dueDate_image.setColorFilter(getResources()
                        .getColor(R.color.background_color_6));
                dueDate_text.setTextColor(getResources()
                        .getColor(R.color.background_color_6));
                dueDate_close.setVisibility(View.GONE);

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_due_date(DueDateString);
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/Tasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Due Date", DueDateString);
                    docRef.update(updateTotalObj);
                } else {
                    taskModel.setTask_due_date(DueDateString);
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/UserTasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Due Date", DueDateString);
                    docRef.update(updateTotalObj);
                }
            }
        });

        repeat_row = findViewById(R.id.taskDetail_repeat_row);
        repeat_close = findViewById(R.id.taskDetail_repeat_close);
        repeat_days_text = findViewById(R.id.taskDetail_repeat_days_text);

        //Default values
        if (!taskModel.getTask_repeat().equals("")) {
            repeat_text.setText(taskModel.getTask_repeat());

            if (!taskModel.getTask_repeat_days().equals("0")) {
                String days_text = taskModel.getTask_repeat_days().substring(1,
                        taskModel.getTask_repeat_days().length() - 1);

                String[] separated = days_text.split(" ");

                String final_days_text = "";
                int dayCount = 0;
                while (dayCount != separated.length) {
                    final_days_text = final_days_text + separated[dayCount] + " ";
                    dayCount++;
                }

                repeat_days_text.setText(final_days_text);
                repeat_days_text.setVisibility(View.VISIBLE);
            }

            repeat_close.setVisibility(View.VISIBLE);
        }

        repeat_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TaskDetail.this, repeat_text);
                popupMenu.getMenuInflater().inflate(R.menu.menu_repeat, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.repeatDailyAction:
                                RepeatString = "Daily";
                                repeat_text.setText(RepeatString);
                                break;
                            case R.id.repeatWeekdaysAction:
                                RepeatString = "Weekly Weekdays";
                                repeat_text.setText(RepeatString);
                                break;
                            case R.id.repeatWeeklyAction:
                                RepeatString = "Weekly";
                                repeat_text.setText(RepeatString);
                                break;
                            case R.id.repeatMonthlyAction:
                                RepeatString = "Monthly";
                                repeat_text.setText(RepeatString);
                                break;
                            case R.id.repeatYearlyAction:
                                RepeatString = "Yearly";
                                repeat_text.setText(RepeatString);
                                break;
                            case R.id.repeatCustomAction:
                                CustomPickFlag = true;
                                createCustomPopup(taskModel, arrayListName, dbHandler);
                                break;
                        }

                        if (!CustomPickFlag) {
                            repeat_days_text.setVisibility(View.GONE);
                            repeat_close.setVisibility(View.VISIBLE);

                            switch (themeColor) {
                                case "Theme Color 1":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                                    break;
                                case "Theme Color 2":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                                    break;
                                case "Theme Color 3":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                                    break;
                                case "Theme Color 4":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                                    break;
                                case "Theme Color 5":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                                    break;
                                case "Theme Color 6":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                                    break;
                                case "Theme Color 7":
                                    repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                                    repeat_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                                    break;
                            }

                            //Updating task detail
                            if (Util.IS_TASKS) {
                                taskModel.setTask_repeat(RepeatString);
                                if (dayList.size() == 0) {
                                    taskModel.setTask_repeat_days("0");
                                } else {
                                    taskModel.setTask_repeat_days(String.valueOf(dayList));
                                }
                                dbHandler.updateTask(taskModel);
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        "Tasks");
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/Tasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Repeat", RepeatString);
                                if (dayList.size() == 0)
                                    updateTotalObj.put("Repeat Days", "0");
                                else {
                                    updateTotalObj.put("Repeat Days", String.valueOf(dayList));
                                }
                                docRef.update(updateTotalObj);
                            } else {
                                taskModel.setTask_repeat(RepeatString);
                                if (dayList.size() == 0) {
                                    taskModel.setTask_repeat_days("0");
                                } else {
                                    taskModel.setTask_repeat_days(String.valueOf(dayList));
                                }
                                dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                                List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                id = dbHandler.getImportantTaskId(String.valueOf(
                                        taskModel.getTasks_id()),
                                        arrayListName.get(listPosition));
                                if (id.size() != 0) {
                                    dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                                }

                                UserData userData = UserData.getInstance();
                                final String currentUserEmail = userData.getUserEmail();
                                DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                        + "/UserTasks")
                                        .document(String.valueOf(taskModel.getTasks_id()));

                                Map<String, Object> updateTotalObj = new HashMap<>();
                                updateTotalObj.put("Repeat", RepeatString);
                                if (dayList.size() == 0)
                                    updateTotalObj.put("Repeat Days", "0");
                                else {
                                    updateTotalObj.put("Repeat Days", String.valueOf(dayList));
                                }
                                docRef.update(updateTotalObj);
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        repeat_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatString = "";

                repeat_days_text.setVisibility(View.GONE);
                repeat_text.setText(R.string.repeat);
                repeat_text.setTextColor(getResources().getColor(R.color.background_color_6));
                repeat_close.setVisibility(View.GONE);
                repeat_image.setColorFilter(getResources().getColor(R.color.background_color_6));

                //Updating task detail
                if (Util.IS_TASKS) {
                    taskModel.setTask_repeat(RepeatString);
                    taskModel.setTask_repeat_days("0");
                    dbHandler.updateTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/Tasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Repeat", RepeatString);
                    updateTotalObj.put("Repeat Days", "0");
                    docRef.update(updateTotalObj);
                } else {
                    taskModel.setTask_repeat(RepeatString);
                    taskModel.setTask_repeat_days("0");
                    dbHandler.updateUserTask(taskModel, arrayListName.get(listPosition));
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/UserTasks")
                            .document(String.valueOf(taskModel.getTasks_id()));

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Repeat", RepeatString);
                    updateTotalObj.put("Repeat Days", "0");
                    docRef.update(updateTotalObj);
                }
            }
        });

        //Add time ago text
        time_ago_text = findViewById(R.id.taskDetail_time_ago_text);
        delete = findViewById(R.id.taskDetail_delete);

        String time_text = taskModel.getTask_created_time();

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
        ParsePosition parsePosition = new ParsePosition(0);
        Date date = simpleDateFormat1.parse(time_text, parsePosition);
        Timestamp timestamp = new Timestamp(Objects.requireNonNull(date));
        time_text = (String) DateUtils.getRelativeTimeSpanString(timestamp.getSeconds() * 1000);

        String timeAgo = "Created " + time_text;

        time_ago_text.setText(timeAgo);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.IS_TASKS) {
                    dbHandler.deleteTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        if (!Util.IS_MY_DAY_DELETED) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                        }
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            "Tasks");
                    if (id.size() != 0) {
                        if (!Util.IS_IMPORTANT_DELETED) {
                            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                        }
                    }

                    UserData userData = UserData.getInstance();
                    final String currentUserEmail = userData.getUserEmail();
                    DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                            + "/Tasks")
                            .document(String.valueOf(taskModel.getTasks_id()));
                    docRef.delete();

                    ArrayList<String> tempUserTasksId = listData.getTasksid();
                    tempUserTasksId.remove(String.valueOf(taskModel.getTasks_id()));

                    listData.setTasksid(tempUserTasksId);

                    Map<String, Object> idObj = new HashMap<>();
                    idObj.put("Tasks ID", String.valueOf(tempUserTasksId));

                    DocumentReference documentReference = db.collection("List Id")
                            .document(currentUserEmail);
                    documentReference.update(idObj);

                    //Updating Total in Database
                    DocumentReference document = db.collection("userLists")
                            .document(currentUserEmail);

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("Tasks Total", String.valueOf(dbHandler.getTasksCount()));

                    document.update(updateTotalObj);
                } else {
                    dbHandler.deleteUserTask(taskModel);
                    List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        if (!Util.IS_MY_DAY_DELETED) {
                            dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                        }
                    }

                    id = dbHandler.getImportantTaskId(String.valueOf(
                            taskModel.getTasks_id()),
                            arrayListName.get(listPosition));
                    if (id.size() != 0) {
                        if (!Util.IS_IMPORTANT_DELETED) {
                            dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
                        }
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

                    arrayListTotal.set(listPosition, String.valueOf(
                            dbHandler.getUserTasksCount(arrayListName.get(listPosition))));

                    ListModel listModel = new ListModel();
                    listModel.setList_id(listPosition);
                    listModel.setList_name(arrayListName.get(listPosition));
                    listModel.setList_color(arrayListColor.get(listPosition));
                    listModel.setList_total(arrayListTotal.get(listPosition));
                    dbHandler.updateList(listModel);

                    //Updating Total in Database
                    DocumentReference document = db.collection("userLists")
                            .document(currentUserEmail);

                    Map<String, Object> updateTotalObj = new HashMap<>();
                    updateTotalObj.put("List Total", String.valueOf(listData.getUserListTotal()));

                    document.update(updateTotalObj);
                }
                onBackPressed();
            }
        });
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
    public void onBackPressed() {
        if (Util.IS_TASKS) {
            Util.IS_TASKS = false;
        }
        super.onBackPressed();
    }

    private void createCustomPopup(final TaskModel taskModel, final ArrayList<String> arrayList,
                                   final DatabaseHandler handler) {
        builder = new AlertDialog.Builder(TaskDetail.this);
        View view = getLayoutInflater().inflate(R.layout.custom_repeat_popup, null);

        dayList = new ArrayList<>();

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

        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(TaskDetail.this,
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
            public void onNothingSelected(AdapterView<?> parent) { }
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
                    repeat_text.setText(RepeatString);

                    if (dayList.size() != 0) {
                        String temp = "";
                        repeat_days_text.setVisibility(View.VISIBLE);
                        int i = 0;
                        while (i != dayList.size()) {
                            if (i == dayList.size() - 1) {
                                temp = temp + dayList.get(i);
                            } else {
                                temp = temp + dayList.get(i) + ", ";
                            }
                            i++;
                        }
                        repeat_days_text.setText(temp);
                    }

                    switch (themeColor) {
                        case "Theme Color 1":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_1));
                            break;
                        case "Theme Color 2":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_2));
                            break;
                        case "Theme Color 3":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_3));
                            break;
                        case "Theme Color 4":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_4));
                            break;
                        case "Theme Color 5":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_5));
                            break;
                        case "Theme Color 6":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_6));
                            break;
                        case "Theme Color 7":
                            repeat_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                            repeat_text.setTextColor(getResources().getColor(R.color.theme_color_7));
                            break;
                    }

                    //Updating task detail
                    if (Util.IS_TASKS) {
                        taskModel.setTask_repeat(RepeatString);
                        if (dayList.size() == 0) {
                            taskModel.setTask_repeat_days("0");
                        } else {
                            taskModel.setTask_repeat_days(String.valueOf(dayList));
                        }
                        handler.updateTask(taskModel);
                        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            if (!Util.IS_MY_DAY_DELETED) {
                                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                            }
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                "Tasks");
                        if (id.size() != 0) {
                            if (!Util.IS_IMPORTANT_DELETED) {
                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                            }
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/Tasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Repeat", RepeatString);
                        if (dayList.size() == 0)
                            updateTotalObj.put("Repeat Days", "0");
                        else {
                            updateTotalObj.put("Repeat Days", String.valueOf(dayList));
                        }
                        docRef.update(updateTotalObj);
                    } else {
                        taskModel.setTask_repeat(RepeatString);
                        if (dayList.size() == 0) {
                            taskModel.setTask_repeat_days("0");
                        } else {
                            taskModel.setTask_repeat_days(String.valueOf(dayList));
                        }
                        handler.updateUserTask(taskModel, arrayList.get(listPosition));
                        DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                        List<String> id = dbHandler.getMyDayTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayList.get(listPosition));
                        if (id.size() != 0) {
                            if (!Util.IS_MY_DAY_DELETED) {
                                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
                            }
                        }

                        id = dbHandler.getImportantTaskId(String.valueOf(
                                taskModel.getTasks_id()),
                                arrayList.get(listPosition));
                        if (id.size() != 0) {
                            if (!Util.IS_IMPORTANT_DELETED) {
                                dbHandler.updateImportantTask(taskModel, Integer.parseInt(id.get(0)));
                            }
                        }

                        UserData userData = UserData.getInstance();
                        final String currentUserEmail = userData.getUserEmail();
                        DocumentReference docRef = db.collection("userLists/" + currentUserEmail
                                + "/UserTasks")
                                .document(String.valueOf(taskModel.getTasks_id()));

                        Map<String, Object> updateTotalObj = new HashMap<>();
                        updateTotalObj.put("Repeat", RepeatString);
                        if (dayList.size() == 0)
                            updateTotalObj.put("Repeat Days", "0");
                        else {
                            updateTotalObj.put("Repeat Days", String.valueOf(dayList));
                        }
                        docRef.update(updateTotalObj);
                    }

                    repeat_close.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (Util.IS_TASKS) {
                Util.IS_TASKS = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}