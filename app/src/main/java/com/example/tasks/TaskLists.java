package com.example.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class TaskLists extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView user_title, user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_lists);
        Util.USER_LOGGED = false;

        UserData userData = UserData.getInstance();

        ListData listData = ListData.getInstance();
        final ArrayList<String> tempUserList = listData.getUserList();
        final ArrayList<String> tempUserListColor = listData.getUserListColor();
        final ArrayList<String> tempUserListTotal = listData.getUserListTotal();

        listData.setUserList(tempUserList);
        listData.setUserListColor(tempUserListColor);
        listData.setUserListTotal(tempUserListTotal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_open_menu);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);

        user_email = view.findViewById(R.id.user_email);
        user_title = view.findViewById(R.id.user_title);

        user_title.setText(userData.getUserName());
        user_email.setText(userData.getUserEmail());
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            //Take User to settings Activity
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
