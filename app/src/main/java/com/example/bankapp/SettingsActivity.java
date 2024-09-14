package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankapp.Utils.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = SettingsActivity.this;

    private FirebaseAuth mAuth;

    private String[] buttonsText;

    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();
        buttonsText = new String[]{"Account settings", "Log out"};
        super.onCreate(savedInstanceState);
        //BELOW LINE OF CODE CAUSES THE UI ISSUE WITH CREATING ANOTHER HOME.
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "onCreate: settings started");

        setupBottomNavigationView();

        RecyclerView recyclerView = findViewById(R.id.settings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingsAdapter mAdapter = new SettingsAdapter(buttonsText, SettingsActivity.this);;
        recyclerView.setAdapter(mAdapter);

        title = findViewById(R.id.home_title);
        title.setText("Settings");
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(mContext, Login.class);
            startActivity(intent);
        }
    }
}
