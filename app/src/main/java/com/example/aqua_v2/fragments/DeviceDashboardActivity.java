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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

public class DeviceDashboardActivity extends Fragment {
    MaterialButton deviceBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_devices, container, false);

        TextView deviceTxt = (TextView) rootView.findViewById(R.id.deviceTxt);
        deviceBtn = (MaterialButton) rootView.findViewById(R.id.deviceManagementBtn);
        deviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent device = new Intent(getActivity(), DevicesActivity.class);
                startActivity(device);
            }
        });
        deviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DevicesActivity.class));
            }
        });

        return rootView;
    }
}
