package com.example.bankapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.Utils.BottomNavigationViewHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView welcomeUserTv;
    FirebaseFirestore database;
    Button depositBtn;
    Button accountsBtn;
    Button confirmBtn;
    Double currentBalance;
    Button transferBtn;
    Button activitiesBtn;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    GsonBuilder gsonb;
    Gson gson;
    List<String> transactions;
    List<String> drawables;
    Calendar calendar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNavigationView();
        welcomeUserTv = findViewById(R.id.welcome_user);
        depositBtn = findViewById(R.id.button_deposit);
        accountsBtn = findViewById(R.id.button_accounts);
        sharedPref = getApplicationContext().getSharedPreferences("TRANSACTIONS_KEY", Context.MODE_PRIVATE);
        prefEditor = sharedPref.edit();
        transferBtn = findViewById(R.id.button_transfer);
        activitiesBtn = findViewById(R.id.button_activities);
        gsonb = new GsonBuilder();
        gson = gsonb.create();
        transactions = new ArrayList<>();
        drawables = new ArrayList<>();



        welcomeUserTv.setText("Welcome, " + String.valueOf(user.getEmail()) + ".");
        database = FirebaseFirestore.getInstance();

        depositBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.dialog_deposit);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.color.white));
                dialog.show();

                confirmBtn = dialog.findViewById(R.id.btn_deposit);
                TextInputEditText inputAmount = dialog.findViewById(R.id.input_amount);
                Spinner fromSpinner = dialog.findViewById(R.id.from_spinner);
                Spinner toSpinner = dialog.findViewById(R.id.to_spinner);
                int fakeBankBalance = 500;
                ArrayList<String> items = new ArrayList<>();
                items.add("Bank Account 1: $" + String.valueOf(fakeBankBalance));
                ArrayAdapter adapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(
                        android.R.layout.
                                simple_spinner_dropdown_item
                );

                fromSpinner.setAdapter(adapter);

                List<String> items2 = new ArrayList<>();
                ArrayAdapter adapter2 = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, items2);
                adapter2.setDropDownViewResource(
                        android.R.layout.
                                simple_spinner_dropdown_item
                );
                toSpinner.setAdapter(adapter2);
                database.collection(user.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        String accName = snapshot.getString("accName");
                                        Double accBalance = Double.parseDouble(String.valueOf(snapshot.get("accBalance")));
                                        items2.add(accName + ": $" + accBalance);
                                    }

                                    adapter2.notifyDataSetChanged();
                                }
                                else {
                                    Log.w(TAG, "onComplete2: ", task.getException());
                                }
                            }
                        });
                fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = String.valueOf(parent.getItemAtPosition(position));
                        Log.d(TAG, "onItemSelected: Selected item " + position + " in deposit dialog. (fromspinner)");

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



                toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = String.valueOf(parent.getItemAtPosition(position));
                        Log.d(TAG, "onItemSelected: Selected item " + position + " in deposit dialog. (toSpinner)");

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String toSpinnerItem = String.valueOf(toSpinner.getSelectedItem());
                        int colonIndex = toSpinnerItem.indexOf(":");
                        String selectedAccName = String.valueOf(toSpinnerItem.substring(0, colonIndex));
                        Double inputtedAmount = Double.parseDouble(String.valueOf(inputAmount.getText()));
                        Log.d(TAG, "onClick: " + selectedAccName);
                        database
                        .collection(user.getEmail())
                        .document(selectedAccName)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    currentBalance = Double.parseDouble(document.getString("accBalance"));
                                    Double amountToDeposit = currentBalance + inputtedAmount;
                                    Log.d(TAG, "currentBalance" + currentBalance);
                                    database.collection(user.getEmail())
                                            .document(selectedAccName)
                                            .update(
                                                    "accBalance", (amountToDeposit)
                                            );
                                    dialog.dismiss();
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                            });
                        Log.d(TAG, "currentBalance " + currentBalance);
                        Log.d(TAG, "inputted amount" + inputtedAmount);



                        String value = sharedPref.getString("TRANSACTIONS", null);
                        List<String> temp = new ArrayList<String>(Arrays.asList(gson.fromJson(value, String[].class)));
                        Date currentTime = Calendar.getInstance().getTime();
                        temp.add("Deposited $" + inputtedAmount + " from Bank Account 1 to " + selectedAccName + " at " + currentTime + ".");
                        String transactionsJson = gson.toJson(temp);
                        prefEditor.putString("TRANSACTIONS", transactionsJson);

                        String valueDrawables = sharedPref.getString("DRAWABLES", null);
                        List<String> tempDrawables = new ArrayList<String>(Arrays.asList(gson.fromJson(valueDrawables, String[].class)));
                        tempDrawables.add(String.valueOf(R.drawable.baseline_arrow_downward_24_green));
                        String drawablesJson = gson.toJson(tempDrawables);
                        prefEditor.putString("DRAWABLES", drawablesJson);

                        prefEditor.commit();

                    }
                });
            }
        });

        accountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);
                startActivity(intent);
            }
        });

        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.dialog_transfer);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.color.white));
                dialog.show();

                Spinner fromSpinner = dialog.findViewById(R.id.transfer_from_spinner);
                Spinner toSpinner = dialog.findViewById(R.id.transfer_to_spinner);
                Button confirmBtn = dialog.findViewById(R.id.btn_confirm);
                TextInputEditText amount = dialog.findViewById(R.id.transfer_input_amount);

                List<String> toItems = new ArrayList<>();
                ArrayAdapter toAdapter =  new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, toItems);
                toAdapter.setDropDownViewResource(
                        android.R.layout.
                                simple_spinner_dropdown_item
                );

                toSpinner.setAdapter(toAdapter);


                List<String> fromItems = new ArrayList<>();
                ArrayAdapter fromAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, fromItems);
                fromAdapter.setDropDownViewResource(
                        android.R.layout.
                                simple_spinner_dropdown_item
                );

                fromSpinner.setAdapter(fromAdapter);

                database.collection(user.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        String accName = snapshot.getString("accName");
                                        Double accBalance = Double.parseDouble(String.valueOf(snapshot.get("accBalance")));
                                        fromItems.add(accName + ": $" + accBalance);
                                        toItems.add(accName + ": $" + accBalance);
                                    }
                                    fromAdapter.notifyDataSetChanged();
                                    toAdapter.notifyDataSetChanged();
                                }
                                else {
                                    Log.w(TAG, "onComplete2: ", task.getException());
                                }
                            }
                        });
                Button transferConfirmBtn = dialog.findViewById(R.id.btn_confirm);

                transferConfirmBtn.setOnClickListener(new View.OnClickListener() {



                    @Override
                    public void onClick(View v) {
                        String toItem = String.valueOf(toSpinner.getSelectedItem());
                        String fromItem = String.valueOf(fromSpinner.getSelectedItem());
                        if (String.valueOf(amount.getText()).isEmpty()) {
                            Toast.makeText(HomeActivity.this, "Amount is empty.", Toast.LENGTH_SHORT).show();
                        } /* else if (toItem == null || fromItem == null){
                            Log.d(TAG, "toItem is null");

                            int toDollarIndex = toItem.indexOf("$");
                            Double toAccAmount = Double.parseDouble(toItem.substring(toDollarIndex + 1));

                            int fromDollarIndex = fromItem.indexOf("$");
                            Double fromAccAmount = Double.parseDouble(fromItem.substring(fromDollarIndex + 1));

                            Double amountDouble = Double.parseDouble(String.valueOf(amount.getText()));

                            database
                                    .collection(user.getEmail())
                                    .document(String.valueOf(toItems.get(0)))
                                    .update(
                                            "accBalance", (toAccAmount + amountDouble)
                                    );

                            database
                                    .collection(user.getEmail())
                                    .document(String.valueOf(fromItems.get(0)))
                                    .update(
                                            "accBalance", (fromAccAmount - amountDouble)
                                    );
                            dialog.dismiss();

                        } */else {
                            int toDollarIndex = toItem.indexOf("$");
                            Double toAccAmount = Double.parseDouble(toItem.substring(toDollarIndex + 1));
                            String toAccName = String.valueOf(toItem.substring(0, toDollarIndex - 2));

                            int fromDollarIndex = fromItem.indexOf("$");
                            Double fromAccAmount = Double.parseDouble(fromItem.substring(fromDollarIndex + 1));
                            String fromAccName = String.valueOf(fromItem.substring(0, fromDollarIndex - 2));


                            Double amountDouble = Double.parseDouble(String.valueOf(amount.getText()));
                            Log.d(TAG, "amountDouble: " + amountDouble);


                            if (String.valueOf(toSpinner.getSelectedItem())
                                    .equals(String.valueOf(fromSpinner.getSelectedItem()))){
                                Toast.makeText(HomeActivity.this, "From and To are the same!", Toast.LENGTH_SHORT).show();
                            } else if (amountDouble > fromAccAmount) {
                                Toast.makeText(HomeActivity.this, "Amount greater than balance!", Toast.LENGTH_SHORT).show();
                            } else {
                                database
                                        .collection(user.getEmail())
                                        .document(toAccName)
                                        .update(
                                                "accBalance", (toAccAmount + amountDouble)
                                        );

                                database.collection(user.getEmail())
                                        .document(fromAccName)
                                        .update(
                                                "accBalance", (fromAccAmount - amountDouble)
                                        );

                                String value = sharedPref.getString("TRANSACTIONS", null);
                                if (value != null) {
                                    List<String> temp = new ArrayList<String>(Arrays.asList(gson.fromJson(value, String[].class)));
                                    Log.d(TAG, "temp: " + temp);
                                    Date currentTime = Calendar.getInstance().getTime();
                                    temp.add("Transferred $" + amountDouble + " from " + fromAccName + " to " + toAccName + " at " + currentTime + ".");
                                    String transactionsJson = gson.toJson(temp);
                                    prefEditor.putString("TRANSACTIONS", transactionsJson);

                                    String valueDrawables = sharedPref.getString("DRAWABLES", null);
                                    List<String> tempDrawables = new ArrayList<String>(Arrays.asList(gson.fromJson(valueDrawables, String[].class)));
                                    tempDrawables.add(String.valueOf(R.drawable.baseline_compare_arrows_24_green));
                                    String drawablesJson = gson.toJson(tempDrawables);
                                    prefEditor.putString("DRAWABLES", drawablesJson);

                                    prefEditor.commit();
                                } else {
                                    Log.d(TAG, "transactions is empty");
                                    Date currentTime = Calendar.getInstance().getTime();
                                    List<String> temp = new ArrayList<>();
                                    Date currentTime2 = Calendar.getInstance().getTime();
                                    temp.add("Transferred $" + amountDouble + " from " + fromAccName + " to " + toAccName + " at " + currentTime2 + ".");
                                    String transactionsJson = gson.toJson(temp);
                                    prefEditor.putString("TRANSACTIONS", transactionsJson);

                                    List<String> tempDrawables = new ArrayList<>();
                                    tempDrawables.add(String.valueOf(R.drawable.baseline_compare_arrows_24_green));
                                    String drawablesJson = gson.toJson(tempDrawables);
                                    prefEditor.putString("DRAWABLES", drawablesJson);

                                    prefEditor.commit();
;                                }




                                dialog.dismiss();
                            }
                        }
                    }
                });

                fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    // int previous = fromSpinner.getSelectedItemPosition();
                   //  String previousItem = String.valueOf(fromSpinner.getSelectedItem());
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 //    int previous = fromSpinner.getSelectedItemPosition();
                    // String previousItem = String.valueOf(fromSpinner.getSelectedItem());
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



            }
        });

        activitiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ActivitiesActivity.class);
                startActivity(intent);
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(mContext, Login.class);
            startActivity(intent);
        }
    }


}
