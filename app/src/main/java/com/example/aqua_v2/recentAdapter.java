package com.example.aqua_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aqua_v2.model.TemperatureSensor;

import java.util.ArrayList;

public class recentAdapter extends RecyclerView.Adapter<recentAdapter.MyViewHolder> {

    private ArrayList<TemperatureSensor> tempList;

    public recentAdapter(ArrayList<TemperatureSensor> tempList) {
        this.tempList = tempList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
      TextView datetime;
      TextView value;
        public MyViewHolder(final View view) {
            super(view);
            datetime = view.findViewById(R.id.recentDate);
            value = view.findViewById(R.id.recentValue);
        }
    }

    @NonNull
    @Override
    public recentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_report_view, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull recentAdapter.MyViewHolder holder, int position) {
        String datetime = tempList.get(position).getDatetime();
        String value = tempList.get(position).getValue();
        holder.datetime.setText(datetime);
        holder.value.setText(value);
    }

    @Override
    public int getItemCount() {
        return tempList.size();
    }
}
