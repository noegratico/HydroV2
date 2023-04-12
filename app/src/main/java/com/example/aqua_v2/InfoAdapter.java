package com.example.aqua_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aqua_v2.model.InfoModel;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyViewHolder> {
    private List<InfoModel> infoModelList;
    public InfoAdapter(List<InfoModel> infoModelList){
        this.infoModelList = infoModelList;
    }

    @NonNull
    @Override
    public InfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_info_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.MyViewHolder holder, int position) {
        String level = infoModelList.get(position).getLevel();
        String status = infoModelList.get(position).getStatus();
        holder.status.setText(status);
        holder.level.setText(level);

    }

    @Override
    public int getItemCount() {
        return infoModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView level;
        TextView status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            level = itemView.findViewById(R.id.infoTxtLvl);
            status = itemView.findViewById(R.id.infoStatus);
        }
    }
}
