package com.example.aqua_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aqua_v2.model.UserLog;

import java.util.List;

public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.MyViewHolder> {
    private List<UserLog> userLogs;

    public UserLogAdapter(List<UserLog> userLogs){
        this.userLogs = userLogs;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userLogEmail;
        TextView userLogDateTime;
        TextView uerLogActivity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.uerLogActivity = itemView.findViewById(R.id.userActivity);
            this.userLogDateTime = itemView.findViewById(R.id.dateTime);
            this.userLogEmail = itemView.findViewById(R.id.userEmailLog);
        }
    }


    @NonNull
    @Override
    public UserLogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_log_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
String email = userLogs.get(position).getLogEmail();
String dateTime = userLogs.get(position).getLogDateTime();
String activity = userLogs.get(position).getLogActivity();

holder.userLogEmail.setText(email);
holder.userLogDateTime.setText(dateTime);
holder.uerLogActivity.setText(activity);
    }

    @Override
    public int getItemCount() {
        return userLogs.size();
    }


}
