package com.example.bankapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private static final String TAG = "SettingsAdapter";
    private String[] buttonsText;
    Activity mActivity;

    public SettingsAdapter(String[] buttons, Activity mActivity) {
        buttonsText = buttons;
        this.mActivity = mActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button button;
        private final Context context;
        public ViewHolder(View itemView){
            super(itemView);
            button = (Button) itemView.findViewById(R.id.button_item);
            context = itemView.getContext();
        }

        public Button getButton() {
            return button;

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.button_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: setting position" + position);
        holder.getButton().setText(buttonsText[position]);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder.OnClick: Clicked Position" + position);
                if (position == 0){
                    Context mContext = holder.context;
                    Intent intent = new Intent(mActivity, AccountSettingsActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
                else if (position == 1){
                    FirebaseAuth.getInstance().signOut();
                    mActivity.finish();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return buttonsText.length;
    }
}
