package com.example.aqua_v2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterActivity;

public class WaterLevelDashboardActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_water_level, container, false);
TextView waterTxt = (TextView) rootView.findViewById(R.id.waterTxtBtn);
waterTxt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), WaterActivity.class);
        startActivity(intent);
    }
});
        return rootView;
    }
}
