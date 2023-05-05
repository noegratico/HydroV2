package com.example.aqua_v2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.DevicesActivity;
import com.example.aqua_v2.ImageProcessingActivity;
import com.example.aqua_v2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

public class DeviceDashboardActivity extends Fragment {
    MaterialButton deviceBtn;
    CardView mlBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_devices, container, false);

        TextView deviceTxt = (TextView) rootView.findViewById(R.id.deviceTxt);
        mlBtn = rootView.findViewById(R.id.mlBtn);
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
                Intent intent = new Intent(getActivity(), DevicesActivity.class);

                db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        intent.putExtra("currentUserLevel",(String) documentSnapshot.get("userLevel"));
                        startActivity(intent);
                    }
                });
            }
        });
        mlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ImageProcessingActivity.class));
            }
        });

        return rootView;
    }
}
