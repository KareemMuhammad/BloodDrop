package com.example.blooddrop;

import android.app.Application;

import com.example.blooddrop.Models.User;

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
