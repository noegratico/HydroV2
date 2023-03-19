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
    private RecycleViewClickListener listener;

    public recyclerAdapter(ArrayList<User> userList, RecycleViewClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameText;
        private TextView idText;

        public MyViewHolder(final View view) {
            super(view);
            idText = view.findViewById(R.id.userId);
            nameText = view.findViewById(R.id.userName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
listener.onCLick(v, getAdapterPosition());
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

    public interface RecycleViewClickListener {
        void onCLick(View v, int position);
    }
}