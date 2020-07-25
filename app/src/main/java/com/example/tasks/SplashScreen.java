package com.example.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final DatabaseHandler dbHandler = new DatabaseHandler(SplashScreen.this);

        Util.IS_TASKS = false;

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    //Adding ids

                    documentRef = db.collection("List Id")
                            .document(Objects.requireNonNull(currentUser.getEmail()));
                    documentRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        ListData listData = ListData.getInstance();
                                        ArrayList<String> arrayUserTasks = new ArrayList<>();

                                        String tempTasksid = documentSnapshot
                                                .getString("User Tasks ID");

                                        String Tasksid = Objects.requireNonNull(tempTasksid)
                                                .substring(1, tempTasksid.length() - 1);

                                        String[] a1 = Objects.requireNonNull(Tasksid)
                                                .split(",");

                                        int c1 = 0;
                                        while (c1 != a1.length) {
                                            arrayUserTasks.add(a1[c1].trim());
                                            c1++;
                                        }
                                        listData.setUserTasksid(arrayUserTasks);
                                    }
                                }
                            });

                    documentRef = db.collection("List Id")
                            .document(Objects.requireNonNull(currentUser.getEmail()));
                    documentRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        ListData listData = ListData.getInstance();
                                        ArrayList<String> arrayTasks = new ArrayList<>();

                                        String tempTasksid = documentSnapshot
                                                .getString("Tasks ID");

                                        String Tasksid = Objects.requireNonNull(tempTasksid)
                                                .substring(1, tempTasksid.length() - 1);

                                        String[] a2 = Objects.requireNonNull(Tasksid)
                                                .split(",");

                                        int c2 = 0;
                                        while (c2 != a2.length) {
                                            arrayTasks.add(a2[c2].trim());
                                            c2++;
                                        }
                                        listData.setTasksid(arrayTasks);
                                    }
                                }
                            });

                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    documentReference = db.collection("userLists")
                            .document(Objects.requireNonNull(currentUser.getEmail()));
                    documentReference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        ListData listData = ListData.getInstance();
                                        ArrayList<String> arrayListName = new ArrayList<>();
                                        ArrayList<String> arrayListColor = new ArrayList<>();
                                        ArrayList<String> arrayListTotal = new ArrayList<>();

                                        String tempListsName = documentSnapshot
                                                .getString("List Name");
                                        String tempListsColor = documentSnapshot
                                                .getString("List Color");
                                        String tempListsTotal = documentSnapshot
                                                .getString("List Total");

                                        String ListsName = Objects.requireNonNull(tempListsName)
                                                .substring(1, tempListsName.length() - 1);
                                        String ListsColor = Objects.requireNonNull(tempListsColor)
                                                .substring(1, tempListsColor.length() - 1);
                                        String ListsTotal = Objects.requireNonNull(tempListsTotal)
                                                .substring(1, tempListsTotal.length() - 1);

                                        String[] arr1 = Objects.requireNonNull(ListsName)
                                                .split(",");
                                        String[] arr2 = Objects.requireNonNull(ListsColor)
                                                .split(",");
                                        String[] arr3 = Objects.requireNonNull(ListsTotal)
                                                .split(",");

                                        //Adding data in SQLite Database
                                        DatabaseHandler dbHandler = new DatabaseHandler(SplashScreen.this);

                                        int count = 0;
                                        while (count != arr1.length) {
                                            arrayListName.add(arr1[count].trim());
                                            arrayListColor.add(arr2[count].trim());
                                            arrayListTotal.add(arr3[count].trim());

                                            if (Util.USER_LOGGED) {
                                                ListModel listModel = new ListModel();
                                                listModel.setList_name(arr1[count].trim());
                                                listModel.setList_color(arr2[count].trim());
                                                listModel.setList_total(arr3[count].trim());
                                                dbHandler.addList(listModel);
                                            }

                                            count++;
                                        }

                                        listData.setUserList(arrayListName);
                                        listData.setUserListColor(arrayListColor);
                                        listData.setUserListTotal(arrayListTotal);
                                    }
                                }
                            });

                    collectionReference
                            .whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException e) {

                                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            UserData userData = UserData.getInstance();
                                            userData.setUserId(snapshot.getString("userId"));
                                            userData.setUserEmail(snapshot.getString("userEmail"));
                                            userData.setUserName(snapshot.getString("userName"));
                                        }
                                    }
                                }
                            });

                    if (Util.USER_LOGGED) {

                        db.collection("/userLists/"+currentUser.getEmail()+"/UserTasks")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(
                                                    task.getResult())) {

                                                TaskModel taskModel = new TaskModel();
                                                taskModel.setTasks_id(Integer.parseInt(document.getId()));
                                                taskModel.setTask_title(String.valueOf(document.getData().get("Title")));
                                                taskModel.setTask_due_date(String.valueOf(document.getData().get("Due Date")));
                                                taskModel.setTask_repeat(String.valueOf(document.getData().get("Repeat")));
                                                taskModel.setTask_repeat_days(String.valueOf(
                                                        document.getData().get("Repeat Days")));
                                                taskModel.setTask_reminder(String.valueOf(document.getData().get("Reminder")));
                                                taskModel.setTask_is_important(String.valueOf(document.getData().get("is Important")));
                                                taskModel.setTask_is_my_day(String.valueOf(document.getData().get("is My Day")));
                                                taskModel.setTask_is_completed(String.valueOf(document.getData().get("is Completed")));
                                                taskModel.setTask_created_time(String.valueOf(document.getData().get("Created Time")));

                                                dbHandler.addUserTask(taskModel, String.valueOf(
                                                        document.getData().get("UserTasksList Name")));

                                                if (String.valueOf(document.getData().get("is Important")).equals("true")) {
                                                    dbHandler.addImportantTask(taskModel,
                                                            String.valueOf(document.getData().get("UserTasksList Name")));
                                                }

                                                if (String.valueOf(document.getData().get("is My Day")).equals("true")) {
                                                    dbHandler.addMyDayTask(taskModel,
                                                            String.valueOf(document.getData().get("UserTasksList Name")));
                                                }
                                            }
                                        }
                                    }
                                });

                        db.collection("/userLists/"+currentUser.getEmail()+"/Tasks")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(
                                                    task.getResult())) {

                                                TaskModel taskModel = new TaskModel();
                                                taskModel.setTasks_id(Integer.parseInt(document.getId()));
                                                taskModel.setTask_title(String.valueOf(document.getData().get("Title")));
                                                taskModel.setTask_due_date(String.valueOf(document.getData().get("Due Date")));
                                                taskModel.setTask_repeat(String.valueOf(document.getData().get("Repeat")));
                                                taskModel.setTask_repeat_days(String.valueOf(
                                                        document.getData().get("Repeat Days")));
                                                taskModel.setTask_reminder(String.valueOf(document.getData().get("Reminder")));
                                                taskModel.setTask_is_important(String.valueOf(document.getData().get("is Important")));
                                                taskModel.setTask_is_my_day(String.valueOf(document.getData().get("is My Day")));
                                                taskModel.setTask_is_completed(String.valueOf(document.getData().get("is Completed")));
                                                taskModel.setTask_created_time(String.valueOf(document.getData().get("Created Time")));

                                                dbHandler.addTask(taskModel);

                                                if (String.valueOf(document.getData().get("is Important")).equals("true")) {
                                                    dbHandler.addImportantTask(taskModel, "Tasks");
                                                }

                                                if (String.valueOf(document.getData().get("is My Day")).equals("true")) {
                                                    dbHandler.addMyDayTask(taskModel, "Tasks");
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    if (currentUser.isEmailVerified()) {
                        startActivity(new Intent(SplashScreen.this, TaskLists.class));
                        finish();
                    } else {
                        firebaseAuth.signOut();
                        startActivity(new Intent(SplashScreen.this, EmailVerify.class));
                        finish();
                    }
                } else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                }
            }
        }, 4000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.IS_TASKS = false;
        Util.IS_IMPORTANT = false;
        Util.IS_MY_DAY = false;

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
}
