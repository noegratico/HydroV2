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

import com.example.aqua_v2.DevicesActivity;
import com.example.aqua_v2.R;

public class DeviceDashboardActivity extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_devices, container, false);

        TextView deviceTxt = (TextView) rootView.findViewById(R.id.deviceTxt);
        deviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent device = new Intent(getActivity(), DevicesActivity.class);
                startActivity(device);
            }
        });

        return rootView;
    }
}
