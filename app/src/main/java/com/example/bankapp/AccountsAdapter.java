package com.example.bankapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {
    private static final String TAG = "AccountsAdapter";
    Activity mActivity;
    List<String> accountNames;
    List<Double> accountBalances;

    public AccountsAdapter(List<String> accountNames, List<Double> accountBalances, Activity mActivity) {
        this.accountNames = accountNames;
        this.accountBalances = accountBalances;
        this.mActivity = mActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView accNameTv;
        TextView accBalanceTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            accNameTv = (TextView) itemView.findViewById(R.id.acc_name_tv);
            accBalanceTv = (TextView) itemView.findViewById(R.id.acc_balance_tv);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getAccNameTv() {
            return accNameTv;
        }

        public TextView getAccBalanceTv() {
            return accBalanceTv;
        }
    }

    @NonNull
    @Override
    public AccountsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_recycler_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: setting position" + position);
        holder.getAccNameTv().setText(String.valueOf(accountNames.get(position)));
        holder.getAccBalanceTv().setText("Balance: $" + String.valueOf(accountBalances.get(position)));

    }

    @Override
    public int getItemCount() {
        return accountNames.size();
    }

}
