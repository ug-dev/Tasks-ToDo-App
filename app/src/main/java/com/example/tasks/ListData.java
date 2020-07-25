package com.example.tasks;

import android.app.Application;

import java.util.ArrayList;

public class ListData extends Application {
    private ArrayList<String> userList = new ArrayList<>();
    private ArrayList<String> userListColor = new ArrayList<>();
    private ArrayList<String> userListTotal = new ArrayList<>();
    private ArrayList<String> tasksid = new ArrayList<>();
    private ArrayList<String> userTasksid = new ArrayList<>();
    private static ListData instance;

    public ArrayList<String> getTasksid() {
        return tasksid;
    }

    public void setTasksid(ArrayList<String> tasksid) {
        this.tasksid = tasksid;
    }

    public ArrayList<String> getUserTasksid() {
        return userTasksid;
    }

    public void setUserTasksid(ArrayList<String> userTasksid) {
        this.userTasksid = userTasksid;
    }

    public static ListData getInstance() {
        if (instance == null)
            instance = new ListData();
        return instance;
    }

    public ListData() {}

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }

    public ArrayList<String> getUserListColor() {
        return userListColor;
    }

    public void setUserListColor(ArrayList<String> userListColor) {
        this.userListColor = userListColor;
    }

    public ArrayList<String> getUserListTotal() {
        return userListTotal;
    }

    public void setUserListTotal(ArrayList<String> userListTotal) {
        this.userListTotal = userListTotal;
    }
}
