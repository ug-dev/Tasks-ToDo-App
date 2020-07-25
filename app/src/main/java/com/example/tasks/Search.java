package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Search extends AppCompatActivity implements UserTasksRecyclerViewAdapter.OnTaskListener {

    private RecyclerView recyclerView;
    private UserTasksRecyclerViewAdapter userTasksRecyclerViewAdapter;
    private TextView not_found_text, task_not_found_text;
    private ImageView not_found_image, task_not_found;

    private List<TaskModel> taskModels;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData userData = UserData.getInstance();
    private final String currentUserEmail = userData.getUserEmail();

    private SoundPool soundPool;
    private int sound;
    private Snackbar snackbar;

    private int ListPosition;
    private String Str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       taskModels = new ArrayList<>();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        sound = soundPool.load(this, R.raw.task_completed, 1);

        not_found_image = findViewById(R.id.search_not_found_image);
        task_not_found = findViewById(R.id.search_task_not_found);
        not_found_text = findViewById(R.id.search_not_found_text);
        task_not_found_text = findViewById(R.id.search_task_not_found_text);

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Search.this));

        snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.white))
                .setDuration(10000);

        ImageButton backButton = findViewById(R.id.search_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ImageButton closeButton = findViewById(R.id.search_closeButton);
        final EditText searchBox = findViewById(R.id.searchBox);

        showSoftKeyboard(searchBox);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    closeButton.setVisibility(View.VISIBLE);
                    not_found_image.setVisibility(View.GONE);
                    not_found_text.setVisibility(View.GONE);
                    showFilteredItems(s.toString().trim());
                } else {
                    closeButton.setVisibility(View.GONE);
                    not_found_image.setVisibility(View.VISIBLE);
                    not_found_text.setVisibility(View.VISIBLE);
                    task_not_found.setVisibility(View.GONE);
                    task_not_found_text.setVisibility(View.GONE);

                    showEmptyAdapter();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.getText().clear();
                InputMethodManager imm = (InputMethodManager)getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            }
        });
    }

    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showEmptyAdapter() {
        List<TaskModel> taskModels = new ArrayList<>();
        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                Search.this, this, "Theme Color 1");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Str != null) {
            if (!TextUtils.isEmpty(Str.trim())) {
                showFilteredItems(Str);
            }
        }
    }

    private void showFilteredItems(String s) {
        Str = s;
        DatabaseHandler handler = new DatabaseHandler(Search.this);
        taskModels = handler.getAllFilteredItems(s);
        Util.SEARCH_LIST_NAMES = handler.getAllFilteredListNames(s);

        if (taskModels.size() == 0) {
            task_not_found.setVisibility(View.VISIBLE);
            task_not_found_text.setVisibility(View.VISIBLE);
        } else {
            task_not_found.setVisibility(View.GONE);
            task_not_found_text.setVisibility(View.GONE);
        }

        userTasksRecyclerViewAdapter = new UserTasksRecyclerViewAdapter(taskModels,
                Search.this, this, "Theme Color 1");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(userTasksRecyclerViewAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.SEARCH_LIST_NAMES = new ArrayList<>();
        Str = null;

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public void onTaskClick(int position) {
        DatabaseHandler handler = new DatabaseHandler(this);

        if (Util.SEARCH_LIST_NAMES.get(position).equals("Tasks")) {
            Util.IS_TASKS = true;
            Intent intent = new Intent(getApplicationContext(), TaskDetail.class);
            intent.putExtra("themeColor", "Theme Color 1");
            List<TaskModel> tempTaskModels = handler.getAllTasks();

            int pos = 0;
            while (pos != tempTaskModels.size()) {
                TaskModel taskModel = taskModels.get(position);
                TaskModel tempTaskModel = tempTaskModels.get(pos);
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
            intent.putExtra("themeColor", "Theme Color 1");
            List<TaskModel> tempTaskModels = handler.getAllUserTasks(
                    Util.SEARCH_LIST_NAMES.get(position));

            int pos = 0;
            while (pos != tempTaskModels.size()) {
                TaskModel taskModel = taskModels.get(position);
                TaskModel tempTaskModel = tempTaskModels.get(pos);

                if (taskModel.getTasks_id() == tempTaskModel.getTasks_id()) {
                    intent.putExtra("keyPosition", pos);
                }
                pos++;
            }

            ListData listData = ListData.getInstance();
            ArrayList<String> arrayList = listData.getUserList();

            int ListPos = 0;
            while (ListPos != arrayList.size()) {
                if (Util.SEARCH_LIST_NAMES.get(position).equals(arrayList.get(ListPos))) {
                    intent.putExtra("listPosition", ListPos);
                }
                ListPos++;
            }

            //Navigate to new Activity
            startActivity(intent);
        }
    }

    @Override
    public void onCheckboxUnCheck(int position, TextView task_name, ImageButton checkbox_uncheck,
                                  final ImageButton checkbox_check) {
        final DatabaseHandler dbHandler = new DatabaseHandler(Search.this);
        List<String> settings = dbHandler.getAllSettings();

        //Playing task completed sound
        if (settings.get(1).equals("true")) {
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }

        final TaskModel taskModel = taskModels.get(position);

        task_name.setTextColor(getResources()
                .getColor(R.color.background_color_5));
        checkbox_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(Search.this,
                        R.anim.fade_animation);
                checkbox_check.startAnimation(animation);
                checkbox_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        if (Util.SEARCH_LIST_NAMES.get(position).equals("Tasks")) {
            taskModel.setTask_is_completed("true");
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    "Tasks");
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
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
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(position));
            id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
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

    @Override
    public void onCheckboxCheck(int position, TextView task_name, ImageButton checkbox_check,
                                ImageButton checkbox_uncheck) {
        final DatabaseHandler dbHandler = new DatabaseHandler(Search.this);
        final TaskModel taskModel = taskModels.get(position);

        task_name.setTextColor(getResources()
                .getColor(R.color.white));
        checkbox_uncheck.setVisibility(View.VISIBLE);
        checkbox_check.setVisibility(View.GONE);

        if (Util.SEARCH_LIST_NAMES.get(position).equals("Tasks")) {
            taskModel.setTask_is_completed("false");
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    "Tasks");
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
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
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(position));
            id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
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

    @Override
    public void onImportantUnCheck(int position, final ImageButton important_check, ImageButton important_uncheck) {
        final DatabaseHandler dbHandler = new DatabaseHandler(Search.this);
        final TaskModel taskModel = taskModels.get(position);

        important_uncheck.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(Search.this,
                        R.anim.fade_animation);
                important_check.startAnimation(animation);
                important_check.setVisibility(View.VISIBLE);
            }
        }, 50);

        if (Util.SEARCH_LIST_NAMES.get(position).equals("Tasks")) {
            taskModel.setTask_is_important("true");
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    "Tasks");
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
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
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(position));
            dbHandler.addImportantTask(taskModel, Util.SEARCH_LIST_NAMES.get(position));

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
        final DatabaseHandler dbHandler = new DatabaseHandler(Search.this);
        final TaskModel taskModel = taskModels.get(position);

        important_uncheck.setVisibility(View.VISIBLE);
        important_check.setVisibility(View.GONE);

        if (Util.SEARCH_LIST_NAMES.get(position).equals("Tasks")) {
            taskModel.setTask_is_important("false");
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    "Tasks");
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateTask(taskModel);
            id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    "Tasks");
            if (id.size() != 0) {
                dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
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
            List<String> id = dbHandler.getMyDayTaskId(String.valueOf(taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
            if (id.size() != 0) {
                dbHandler.updateMyDayTask(taskModel, Integer.parseInt(id.get(0)));
            }

            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(position));
            id = dbHandler.getImportantTaskId(String.valueOf(
                    taskModel.getTasks_id()),
                    Util.SEARCH_LIST_NAMES.get(position));
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
            final int rowPosition = viewHolder.getAdapterPosition();
            final DatabaseHandler dbHandler = new DatabaseHandler(Search.this);

            final TaskModel taskModel = taskModels.get(rowPosition);
            final ListData listData = ListData.getInstance();
            final ArrayList<String> arrayListName = listData.getUserList();
            final ArrayList<String> arrayListTotal = listData.getUserListTotal();
            final ArrayList<String> arrayListColor = listData.getUserListColor();

            int count = 0;
            while (arrayListName.size() != count) {
                if (arrayListName.get(count).equals(Util.SEARCH_LIST_NAMES.get(rowPosition))) {
                    ListPosition = count;
                }
                count++;
            }

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    if (Util.SEARCH_LIST_NAMES.get(rowPosition).equals("Tasks")) {
                        if (taskModel.getTask_is_my_day().equals("false")) {
                            taskModel.setTask_is_my_day("true");

                            dbHandler.addMyDayTask(taskModel, "Tasks");
                            dbHandler.updateTask(taskModel);
                            List<String> id = dbHandler.getImportantTaskId(String.valueOf(
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
                            updateTotalObj.put("is My Day", "true");
                            docRef.update(updateTotalObj);

                            Snackbar.make(recyclerView, "Added to My Day", Snackbar.LENGTH_SHORT).show();
                        } else {
                            taskModel.setTask_is_my_day("false");
                            List<String> id = dbHandler.getMyDayTaskId(
                                    String.valueOf(taskModel.getTasks_id()), "Tasks");
                            if (id.size() != 0) {
                                dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            }

                            dbHandler.updateTask(taskModel);
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
                            updateTotalObj.put("is My Day", "false");
                            docRef.update(updateTotalObj);
                        }
                    } else {
                        if (taskModel.getTask_is_my_day().equals("false")) {
                            taskModel.setTask_is_my_day("true");

                            dbHandler.addMyDayTask(taskModel, Util.SEARCH_LIST_NAMES.get(rowPosition));
                            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(rowPosition));
                            List<String> id = dbHandler.getImportantTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    Util.SEARCH_LIST_NAMES.get(rowPosition));
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

                            Snackbar.make(recyclerView, "Added to My Day", Snackbar.LENGTH_SHORT).show();
                        } else {
                            taskModel.setTask_is_my_day("false");
                            List<String> id = dbHandler.getMyDayTaskId(
                                    String.valueOf(taskModel.getTasks_id()), Util.SEARCH_LIST_NAMES.get(rowPosition));
                            if (id.size() != 0) {
                                dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            }

                            dbHandler.updateUserTask(taskModel, Util.SEARCH_LIST_NAMES.get(rowPosition));
                            id = dbHandler.getImportantTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    Util.SEARCH_LIST_NAMES.get(rowPosition));
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
                    }

                    showFilteredItems(Str);
                    break;
                    case ItemTouchHelper.LEFT:
                        if (Util.SEARCH_LIST_NAMES.get(rowPosition).equals("Tasks")) {
                            dbHandler.deleteTask(taskModel);
                            List<String> id = dbHandler.getMyDayTaskId(
                                    String.valueOf(taskModel.getTasks_id()), "Tasks");
                            if (id.size() != 0) {
                                dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            }

                            id = dbHandler.getImportantTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    "Tasks");
                            if (id.size() != 0) {
                                dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
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
                            snackbar.show();
                        } else {
                            dbHandler.deleteUserTask(taskModel);
                            List<String> id = dbHandler.getMyDayTaskId(
                                    String.valueOf(taskModel.getTasks_id()), Util.SEARCH_LIST_NAMES.get(rowPosition));
                            if (id.size() != 0) {
                                dbHandler.deleteMyDayTask(Integer.parseInt(id.get(0)));
                            }

                            id = dbHandler.getImportantTaskId(String.valueOf(
                                    taskModel.getTasks_id()),
                                    Util.SEARCH_LIST_NAMES.get(rowPosition));
                            if (id.size() != 0) {
                                dbHandler.deleteImportantTask(Integer.parseInt(id.get(0)));
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
                            snackbar.show();
                        }

                        showFilteredItems(Str);
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
}
