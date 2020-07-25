package com.example.tasks.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.DatabaseHandler;
import com.example.tasks.Important;
import com.example.tasks.ListData;
import com.example.tasks.ListModel;
import com.example.tasks.MainActivity;
import com.example.tasks.MyDay;
import com.example.tasks.R;
import com.example.tasks.RecyclerViewAdapter;
import com.example.tasks.Tasks;

import com.example.tasks.UserData;
import com.example.tasks.UserTasksList;
import com.example.tasks.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.internal.DiskLruCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment implements RecyclerViewAdapter.OnListListener {
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private TextView create_button;
    private TextView cancel_button;
    private EditText list_title;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference documentReference;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView task_total, important_count, myDay_count;

    private ImageView home_line, tasks_image, important_image, my_day_image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        documentReference = db.collection("userLists")
                .document(Objects.requireNonNull(Objects.requireNonNull(user).getEmail()));

        Util.USER_LOGGED = false;

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.lists_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        home_line = view.findViewById(R.id.home_line1);
        tasks_image = view.findViewById(R.id.tasks_image);
        important_image = view.findViewById(R.id.important_image);
        my_day_image = view.findViewById(R.id.my_day_image);

        task_total = view.findViewById(R.id.tasks_count);
        important_count = view.findViewById(R.id.important_count);
        myDay_count = view.findViewById(R.id.myDay_count);

        CardView my_day_card = view.findViewById(R.id.my_day_card);
        my_day_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.IS_MY_DAY = true;
                startActivity(new Intent(getActivity(), MyDay.class));
            }
        });

        CardView important_card = view.findViewById(R.id.important_card);
        important_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.IS_IMPORTANT = true;
                startActivity(new Intent(getActivity(), Important.class));
            }
        });

        CardView tasks_card = view.findViewById(R.id.tasks_card);
        tasks_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Tasks.class));
            }
        });

        CardView new_list_card = view.findViewById(R.id.new_list_card);
        new_list_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewListPopup();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.EXIT_FLAG) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            requireActivity().finish();
            Util.EXIT_FLAG = false;
        }

        DatabaseHandler dbHandler = new DatabaseHandler(getActivity());
        List<ListModel> listModels = dbHandler.getAllLists();

        if (listModels.size() != 0) {
            home_line.setVisibility(View.VISIBLE);
        } else {
            home_line.setVisibility(View.GONE);
        }

        if (dbHandler.getAllMyDayTask().size() != 0) {
            myDay_count.setText(String.valueOf(dbHandler.getAllMyDayTask().size()));
            myDay_count.setVisibility(View.VISIBLE);
        } else {
            myDay_count.setVisibility(View.GONE);
        }

        if (dbHandler.getAllImportantTask().size() != 0) {
            important_count.setText(String.valueOf(dbHandler.getAllImportantTask().size()));
            important_count.setVisibility(View.VISIBLE);
        } else {
            important_count.setVisibility(View.GONE);
        }

        if (dbHandler.getTasksCount() != 0) {
            task_total.setText(String.valueOf(dbHandler.getTasksCount()));
            task_total.setVisibility(View.VISIBLE);
        } else {
            task_total.setVisibility(View.GONE);
        }

        ArrayList<String> arrayList = dbHandler.getThemeColorSize();

        if (arrayList.size() != 0) {
            switch (dbHandler.getThemeColor(3)) {
                case "Theme Color 1":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    break;
                case "Theme Color 2":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    break;
                case "Theme Color 3":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    break;
                case "Theme Color 4":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    break;
                case "Theme Color 5":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    break;
                case "Theme Color 6":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    break;
                case "Theme Color 7":
                    tasks_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    break;
            }

            switch (dbHandler.getThemeColor(2)) {
                case "Theme Color 1":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    break;
                case "Theme Color 2":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    break;
                case "Theme Color 3":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    break;
                case "Theme Color 4":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    break;
                case "Theme Color 5":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    break;
                case "Theme Color 6":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    break;
                case "Theme Color 7":
                    important_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    break;
            }

            switch (dbHandler.getThemeColor(1)) {
                case "Theme Color 1":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_1));
                    break;
                case "Theme Color 2":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_2));
                    break;
                case "Theme Color 3":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_3));
                    break;
                case "Theme Color 4":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_4));
                    break;
                case "Theme Color 5":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_5));
                    break;
                case "Theme Color 6":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_6));
                    break;
                case "Theme Color 7":
                    my_day_image.setColorFilter(getResources().getColor(R.color.theme_color_7));
                    break;
            }
        }

        //Setup Adapter
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), this,
                listModels);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void createNewListPopup() {
        builder = new AlertDialog.Builder(getActivity());
        @SuppressLint("InflateParams") final View view = getLayoutInflater()
                .inflate(R.layout.activity_create_list_popup, null);
        list_title = view.findViewById(R.id.list_title);
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
                dialog.dismiss();
            }
        });

        create_button = view.findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (create_button.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    dialog.dismiss();
                    String List_Title = list_title.getText().toString().trim();
                    createUserList(List_Title);
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void createUserList(final String ListName) {
        String ListColor = "Theme Color 1";
        String ListTotal = "0";

        UserData userData = UserData.getInstance();
        final String currentUserId = userData.getUserId();

        ListData listData = ListData.getInstance();
        ArrayList<String> tempUserList = listData.getUserList();
        ArrayList<String> tempUserListColor = listData.getUserListColor();
        ArrayList<String> tempUserListTotal = listData.getUserListTotal();

        if (tempUserList.size() == 0) {
            tempUserList = new ArrayList<>();
            tempUserListColor = new ArrayList<>();
            tempUserListTotal = new ArrayList<>();
        }

        //Checking if list is already available then add number after it..
        if (tempUserList.contains(ListName)) {
            int cnt = 1;
            while (true) {
                if (tempUserList.contains(ListName+" ("+cnt+")")) {
                    cnt++;
                } else {
                    tempUserList.add(ListName+" ("+cnt+")");
                    break;
                }
            }
        } else {
            tempUserList.add(ListName);
        }

        tempUserListColor.add(ListColor);
        tempUserListTotal.add(ListTotal);

        //Adding data in SQLite Database
        DatabaseHandler dbHandler = new DatabaseHandler(getActivity());

        ListModel listModel = new ListModel();
        listModel.setList_name(tempUserList.get(tempUserList.size() - 1));
        listModel.setList_color(tempUserListColor.get(tempUserListColor.size() - 1));
        listModel.setList_total(tempUserListTotal.get(tempUserListTotal.size() - 1));
        dbHandler.addList(listModel);

        listData.setUserList(tempUserList);
        listData.setUserListColor(tempUserListColor);
        listData.setUserListTotal(tempUserListTotal);

        Map<String, String> listObj = new HashMap<>();
        listObj.put("List Id", currentUserId);
        listObj.put("List Name", String.valueOf(listData.getUserList()));
        listObj.put("List Color", String.valueOf(listData.getUserListColor()));
        listObj.put("List Total", String.valueOf(listData.getUserListTotal()));

        documentReference.delete();
        documentReference.set(listObj);

        Util.LIST_POS = (tempUserList.size() - 1);
        startActivity(new Intent(getActivity(), UserTasksList.class));
    }

    @Override
    public void onListClick(int position) {
        Util.LIST_POS = position;
        startActivity(new Intent(getActivity(), UserTasksList.class).putExtra("position", position));
    }
}
