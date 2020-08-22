package com.example.blooddrop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.blooddrop.Data.UserViewModel;
import com.example.blooddrop.Models.User;
import com.example.blooddrop.R;
import com.example.blooddrop.UserClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final int DelayMills = 2000;
    private FirebaseAuth mAuth;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, DelayMills);
    }
    private void getUserDocument(){
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getLiveData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userArrayList) {
                for (User u : userArrayList) {
                    if (u.getUser_id().equals(mAuth.getCurrentUser().getUid()))
                        ((UserClient)(getApplicationContext())).setUser(u);
                    Log.d("SplashActivity","user set");
                }
            }
        });
    }
}
