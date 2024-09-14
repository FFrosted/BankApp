package com.example.bankapp;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    private static final String TAG = "ActivitiesAdapter";
    List<String> transactions;
    List<Drawable> imageViews;

    public ActivitiesAdapter(List<String> transactions, List<Drawable> imageViews){
        Collections.reverse(transactions);
        this.transactions = transactions;
        Collections.reverse(imageViews);
        this.imageViews = imageViews;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activities_tv);
            imageView = (ImageView) itemView.findViewById(R.id.activities_image);
        }

        public TextView getTextView(){ return textView; }

        public ImageView getImageView(){ return imageView; }


    }
    @NonNull
    @Override
    public ActivitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activities_item, parent, false);

        return new ActivitiesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: setting position" + position);
        holder.getTextView().setText(String.valueOf(transactions.get(position)));
        holder.getImageView().setImageDrawable(imageViews.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
