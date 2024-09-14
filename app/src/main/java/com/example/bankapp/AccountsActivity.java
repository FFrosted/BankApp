package com.example.bankapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsActivity extends AppCompatActivity {
    private static final String TAG = "AccountsActivity";
    TextView topTitle;
    Button backButton;
    Button addAccButton;
    List<String> accountNames;
    List<Double> accountBalances;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accounts);
        topTitle = findViewById(R.id.home_title);
        topTitle.setText("Accounts");
        backButton = findViewById(R.id.account_back);
        addAccButton = findViewById(R.id.add_acc_btn);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        accountNames = new ArrayList<>();
        accountBalances = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        RecyclerView recyclerView = findViewById(R.id.accounts_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountsAdapter accountsAdapter = new AccountsAdapter(accountNames, accountBalances, AccountsActivity.this);
        recyclerView.setAdapter(accountsAdapter);

        db.collection(user.getEmail())
                        .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                addAccounts(String.valueOf(document.get("accName")), Double.parseDouble(String.valueOf(document.get("accBalance"))));
                                            }
                                        } else {
                                            Log.d(TAG, "onComplete: Error getting documents" + task.getException());
                                        }
                                    }
                                });

        addAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AccountsActivity.this);
                dialog.setContentView(R.layout.dialog_addacc);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.color.white));
                dialog.show();

                Button confirmButton = dialog.findViewById(R.id.confirm_new_acc);
                TextInputEditText accNameInput = dialog.findViewById(R.id.input_new_acc);
                TextInputEditText accBalanceInput = dialog.findViewById(R.id.input_new_acc_balance);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String accName = String.valueOf(accNameInput.getText());
                        String accBalance = String.valueOf(Double.parseDouble(accBalanceInput.getText().toString()));

                        Map<String, Object> userAccount = new HashMap<>();

                        userAccount.put("accName", String.valueOf(accName));
                        userAccount.put("accBalance", String.valueOf(accBalance));
                        db.collection(user.getEmail()).document(accName).set(userAccount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "onSuccess: added account" + accName +" with $" + accBalance);
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "onFailure: ", e);
                                                    }
                                                });

                        dialog.dismiss();
                        addAccounts(accName, Double.valueOf(accBalance));
                    }

                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_back);
            }
        });


    }

    public void addAccounts(String accountName, Double accountBalance) {
        accountNames.add(accountName);
        accountBalances.add(accountBalance);
        RecyclerView recyclerView = findViewById(R.id.accounts_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountsAdapter accountsAdapter = new AccountsAdapter(accountNames, accountBalances, AccountsActivity.this);
        recyclerView.setAdapter(accountsAdapter);
    }
}