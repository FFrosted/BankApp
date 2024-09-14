package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ActivitiesActivity extends AppCompatActivity {
    GsonBuilder gsonb;
    Gson gson;
    private static final String TAG = "ActivitiesActivity";
    Button backButton;

    boolean transactionsEmpty;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activities);
        backButton = findViewById(R.id.back);

        sharedPref = getApplicationContext().getSharedPreferences("TRANSACTIONS_KEY", Context.MODE_PRIVATE);
        gsonb = new GsonBuilder();
        gson = gsonb.create();

        SharedPreferences.Editor prefEditor = sharedPref.edit();


        String transactions = sharedPref.getString("TRANSACTIONS", null);

        if (transactions == null) {
            Log.d(TAG, "transactions is null, not creating any views");
        } else {
            String[] transactionsArray = gson.fromJson(transactions, String[].class);
            List<String> transactionsList = Arrays.asList(transactionsArray);

            String drawables = sharedPref.getString("DRAWABLES", null);

            String[] drawablesArray = gson.fromJson(drawables, String[].class);
            List<String> drawablesListString = Arrays.asList(drawablesArray);


            Log.d(TAG, "transactionsList: " + transactionsList);
            Log.d(TAG, "drawablesListString: " + drawablesListString);


            List<Drawable> drawablesList = new ArrayList<>();
            for (String id : drawablesListString ) {
                drawablesList.add(getDrawable(Integer.parseInt(id)));
            }


            RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ActivitiesAdapter activitiesAdapter = new ActivitiesAdapter(transactionsList, drawablesList); //ADD SHAREDPREFS
            recyclerView.setAdapter(activitiesAdapter);


        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitiesActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_back);
            }
        });








    }
}