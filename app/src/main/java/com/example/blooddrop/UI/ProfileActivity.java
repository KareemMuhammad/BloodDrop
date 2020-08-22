package com.example.blooddrop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.blooddrop.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private androidx.appcompat.widget.Toolbar toolbar;
    private GoogleSignInClient mGoogleSignInClient;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private NavController navController;
    private AppBarConfiguration appBarConfig;
    private FirebaseAuth mAuth;
    private TextView profileNameTxt;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.nav_view);
        View hView =  navigation.getHeaderView(0);
        profileNameTxt = hView.findViewById(R.id.name_nav_view);
        circleImageView = hView.findViewById(R.id.circle_image_nav);
        if (mAuth.getCurrentUser() != null) {
            profileNameTxt.setText(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .placeholder(R.drawable.ic_baseline_loop_24)
                        .into(circleImageView);
            }
        }else {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null){
                profileNameTxt.setText(acct.getDisplayName());
                Glide.with(this)
                        .load(acct.getPhotoUrl())
                        .placeholder(R.drawable.ic_baseline_loop_24)
                        .into(circleImageView);
            }
        }
        final Set<Integer> topLevelDestinations = new ArraySet<>();
        topLevelDestinations.add(R.id.nav_profile);
        topLevelDestinations.add(R.id.nav_map);
        topLevelDestinations.add(R.id.nav_donation);
        topLevelDestinations.add(R.id.nav_about);
        appBarConfig = new AppBarConfiguration.Builder(topLevelDestinations)
                .setDrawerLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(this.<NavigationView>findViewById(R.id.nav_view), navController);
        navigation.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                logout();
                return true;
            }
        });
    }

    private void logout() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(),R.string.sign_out_msg,Toast.LENGTH_SHORT).show();
        }else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),R.string.sign_out_msg,Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

