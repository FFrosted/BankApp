package com.example.bankapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
   private Button backButton;
   private TextView title;
   private TextInputEditText updateEmail;
    private Button updateEmailButton;
   private FirebaseAuth mAuth;
   private FirebaseUser user;
   private String mPassword;
   private Context context;
   private Dialog dialog;
   private Button dialogConfirmBtn;
   private Button dialogCancelBtn;
   private TextInputEditText inputPassword;
   private TextInputEditText inputEmail;
   private TextView dialogConfirmTv;
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Log.d(TAG, "OnCreate: Starting AccountSettings");
        backButton = findViewById(R.id.back_button);
        updateEmail = findViewById(R.id.update_email_input);
        updateEmailButton = findViewById(R.id.button_update_email);
        context = AccountSettingsActivity.this;
        title = findViewById(R.id.home_title);
        title.setText("Settings > Account Settings");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        updateEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String mPassword = sharedPreferences.getString("password", "");
        String mEmail = sharedPreferences.getString("email", "");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                AccountSettingsActivity.this.finish();
            }
        });

        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(inputEmail.getText());
                Log.d(TAG, "onClick: capturing and changing email: " + email);
                dialogConfirmTv.setText("New Email: " + email);
                dialog.show();
            }
        });

        dialog = new Dialog(AccountSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_confirm_password);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.color.gray_opaque));

        dialogConfirmTv = dialog.findViewById(R.id.confirm_change_tv);
        dialogConfirmBtn = dialog.findViewById(R.id.confirm_change_button);
        dialogCancelBtn = dialog.findViewById(R.id.dialog_cancel_button);
        inputEmail = findViewById(R.id.update_email_input);
        dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = String.valueOf(inputEmail.getText());
                user.verifyBeforeUpdateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: User email address updated.");
                                        } else {
                                            Log.d(TAG, "onComplete: exception " + task.getException());
                                        }
                                    }
                                });
                Toast.makeText(AccountSettingsActivity.this, "Verification email sent..", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button passwordResetBtn = findViewById(R.id.button_update_password);

        passwordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Email sent.");
                                    Toast.makeText(AccountSettingsActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}