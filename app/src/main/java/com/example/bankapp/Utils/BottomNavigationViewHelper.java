package com.example.bankapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.bankapp.DashboardActivity;
import com.example.bankapp.HomeActivity;
import com.example.bankapp.R;
import com.example.bankapp.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHelper";

    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    Intent intent1 = new Intent(context, HomeActivity.class);
                    //Flag disables sliding animation from switching activities.
                    context.startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (itemId == R.id.dashboard) {
                    Intent intent2 = new Intent(context, DashboardActivity.class);
                    context.startActivity(intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                } else if (itemId == R.id.settings) {
                    Intent intent3 = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
                return false;
            }
        });
    }
}
