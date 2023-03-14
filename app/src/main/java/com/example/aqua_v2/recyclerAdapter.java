package com.example.aqua_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aqua_v2.model.User;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<User> userList;

    public recyclerAdapter(ArrayList<User> userList) {
        this.userList = userList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView idText;

        public MyViewHolder(final View view) {
            super(view);
            idText = view.findViewById(R.id.userId);
            nameText = view.findViewById(R.id.userName);

        }

    }


    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycle_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String name = userList.get(position).getName();
        String id = userList.get(position).getId();
        holder.idText.setText(id);
        holder.nameText.setText(name);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}