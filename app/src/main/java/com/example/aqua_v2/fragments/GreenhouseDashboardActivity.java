package com.example.aqua_v2.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.GreenHouseActivity;
import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterConditionActivity;
import com.example.aqua_v2.model.Sensors;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;

public class GreenhouseDashboardActivity extends Fragment {
    TextView temp, hum, ec, ph, greenHouseTxt, waterConditionTxt;
    ImageButton gHbtn, wCBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");


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
        gHbtn = (ImageButton) rootView.findViewById(R.id.gHBtn);
        wCBtn = (ImageButton) rootView.findViewById(R.id.wCBtn);
        waterConditionTxt = (TextView) rootView.findViewById(R.id.greenHouseTxt);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

      /*  mFunctions
                .getHttpsCallable("getSensorData")
                .call()
                .addOnSuccessListener(result->{
                    HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>) result.getData();
                    temp.setText(data.get("temperature").get("value"));
                    hum.setText(data.get("humidity").get("value"));
                    ph.setText(data.get("phLevel").get("value"));
                    int eclvl = Integer.parseInt(data.get("ecLevel").get("value"));
                   if(eclvl > 500 && eclvl < 2000){
                       ec.setTextColor(Color.parseColor("#00FF00"));
                       ec.setText(data.get("ecLevel").get("value"));
                   }else{
                       ec.setTextColor(Color.parseColor("#FF0000"));
                       ec.setText(data.get("ecLevel").get("value"));
                   }
//
                });*/

//        --> humidity
        db.collection("humidity").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            hum.setText(documentChange.getDocument().get("value", String.class) + "%");
                        }
                    });
                }
            }
        });

//      ->> temperature
        db.collection("temperature").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            temp.setText(documentChange.getDocument().get("value", String.class) + "°C");
                        }
                    });
                }
            }
        });
        db.collection("ph_level").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            ph.setText(documentChange.getDocument().get("value", String.class));
                        }
                    });
                }
            }
        });
        db.collection("ec_level").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            int eclvl = Integer.parseInt(documentChange.getDocument().get("value", String.class));
                            if (eclvl >= 600 && eclvl <= 900) {
                                ec.setTextColor(Color.parseColor("#00FF00"));
                                ec.setText(documentChange.getDocument().get("value", String.class));
                            } else {
                                ec.setTextColor(Color.parseColor("#FF0000"));
                                ec.setText(documentChange.getDocument().get("value", String.class));
                            }

                        }
                    });
                }
            }
        });



/*
        ref.child("sensors_monitoring").child("sensors1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    temp.setText("invalid");
                } else {
                    temp.setText(String.valueOf(task.getResult().child("value_tem").getValue()));
                    hum.setText(String.valueOf(task.getResult().child("value_hum").getValue()) + "%");
                    ec.setText(String.valueOf(task.getResult().child("value_lss").getValue()));
                }

            }
        });*/
        greenHouseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), GreenHouseActivity.class));
            }
        });
        gHbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GreenHouseActivity.class));
            }
        });
        wCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WaterConditionActivity.class));
            }
        });
        waterConditionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WaterConditionActivity.class));
            }
        });


        return rootView;
    }
}
