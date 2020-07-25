package com.example.tasks;

import android.app.Application;

public class UserData extends Application {
    private String userId;
    private String userName;
    private String userEmail;
    private static UserData instance;

    public static UserData getInstance() {
        if (instance == null)
            instance = new UserData();
        return instance;
    }

    public UserData() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
