package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.bankapp.Utils.BottomNavigationViewHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private static final int ACTIVITY_NUM = 1;

    FirebaseFirestore db;

    private Context mContext = DashboardActivity.this;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    TextView totalTv;
    ConstraintLayout layout;
    TextView accTv;
    TextView balTv;

    private TextView title;
    int total;

    String greatestAccName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);

        super.onCreate(savedInstanceState);
        //BELOW LINE OF CODE CAUSES THE UI ISSUE WITH CREATING ANOTHER HOME.
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreate: dashboard started");
        updateUI(user);
        setupBottomNavigationView();


        layout = findViewById(R.id.acc_view);
        accTv = layout.findViewById(R.id.acc_name_tv);
        balTv = layout.findViewById(R.id.acc_balance_tv);

        db.collection(user.getEmail())
                .whereGreaterThan("accBalance", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double greatest = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getDouble("accBalance") > greatest ) {
                                    greatest = document.getDouble("accBalance");
                                }
                                balTv.setText(String.valueOf(greatest));
                            }
                            Log.d(TAG, "Greatest: " + greatest);
                            db.collection(user.getEmail())
                                    .whereEqualTo("accBalance", greatest)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                greatestAccName = String.valueOf(document.getString("accName"));
                                                accTv.setText(greatestAccName);
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "error getting documents: " + task.getException());
                        }
                    }
                });

        totalTv = findViewById(R.id.total_balance_num);
        title = findViewById(R.id.home_title);
        title.setText("Dashboard");


        db
                .collection(user.getEmail())
                        .whereGreaterThan("accBalance", 0)
                                .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, "document.getDouble => " + Double.parseDouble(String.valueOf(document.getDouble("accBalance"))));
                                                        total += document.getDouble("accBalance");
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        totalTv.setText("$" + String.valueOf(Double.parseDouble(String.valueOf(document.getDouble("accBalance"))) + total));
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });


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
