package com.example.aqua_v2.fragments;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.GreenHouseActivity;
import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterConditionActivity;
import com.example.aqua_v2.model.Sensors;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GreenhouseDashboardActivity extends Fragment {
    TextView temp, hum, ec, ph, greenHouseTxt, waterConditionTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_greenhouse_dashboard, container, false);
        temp = (TextView) rootView.findViewById(R.id.greenHouseTemp);
        hum = (TextView) rootView.findViewById(R.id.greenHouseHum);
        ec = (TextView) rootView.findViewById(R.id.greenHouseEc);
        ph = (TextView) rootView.findViewById(R.id.greenHousePh);
        greenHouseTxt = (TextView) rootView.findViewById(R.id.greenTxt);
        waterConditionTxt = (TextView) rootView.findViewById(R.id.greenHouseTxt);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("sensors_monitoring").child("sensors1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    temp.setText("invalid");
                } else {
                    temp.setText(String.valueOf(task.getResult().child("value_tem").getValue()));
                    hum.setText(String.valueOf(task.getResult().child("value_hum").getValue()) + "%");
                    ;
                    ec.setText(String.valueOf(task.getResult().child("value_lss").getValue()));
                }

            }
        });
        greenHouseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), GreenHouseActivity.class));
                getActivity().finish();
            }
        });
        waterConditionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),  WaterConditionActivity.class));
                getActivity().finish();
            }
        });

        return rootView;
    }
}
