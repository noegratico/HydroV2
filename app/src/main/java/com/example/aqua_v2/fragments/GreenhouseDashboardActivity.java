package com.example.aqua_v2.fragments;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.GreenHouseActivity;
import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterConditionActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.UUID;

public class GreenhouseDashboardActivity extends Fragment {
    TextView temp, hum, ec, ph, greenHouseTxt, waterConditionTxt;
    ImageButton gHbtn, wCBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");
    private Notification notification;
    private Notification notification1;


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
                            int humlevel = Integer.parseInt(documentChange.getDocument().get("value", String.class));
                            if(humlevel < 25 || humlevel > 80){
                                hum.setTextColor(Color.parseColor("#FF0000"));
                                if(humlevel < 25){
                                    showNotification("Humidity Level ", "Low");
                                }else if(humlevel > 80 ){
                                    showNotification("Humidity Level ", "High");
                                }
                            }else{
                                hum.setTextColor(Color.parseColor("#00FF00"));
                            }
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
                            int templevel = Integer.parseInt(documentChange.getDocument().get("value",String.class));
                            if(templevel < 30 || templevel > 80){
                                temp.setTextColor(Color.parseColor("#FF0000"));
                                if(templevel <25){
                                    showNotification("Temperature Level ", "Low");
                                }else if(templevel >80){
                                    showNotification("Temperature Level ", "High");
                                }
                            }else{
                                temp.setTextColor(Color.parseColor("#00FF00"));
                            }

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
                        if (documentChange.getType() == DocumentChange.Type.ADDED || documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            ph.setText(documentChange.getDocument().get("value", String.class));

                            float phlevel = Float.parseFloat(documentChange.getDocument().get("value",String.class));
                            if(phlevel < 7.0 || phlevel > 7.0){
                                ph.setTextColor(Color.parseColor("#FF0000"));

                                if(phlevel < 7.0){
                                    showNotification("pH Level ", "Low");
                                }else if (phlevel > 7.0){
                                    showNotification("pH Level ", "High");
                                }
                            }else{
                                ph.setTextColor(Color.parseColor("#00FF00"));
                            }

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
                            float eclvl = Float.parseFloat(documentChange.getDocument().get("value", String.class));
                            if (eclvl >= 1.0 && eclvl <= 2.0) {
                                ec.setTextColor(Color.parseColor("#00FF00"));
                                ec.setText(documentChange.getDocument().get("value", String.class));
                            } else {
                                ec.setTextColor(Color.parseColor("#FF0000"));
                                ec.setText(documentChange.getDocument().get("value", String.class));
                                if (eclvl < 1.0) {
                                    showNotification("EC Level ", "Low");
                                } else if (eclvl > 2.0) {
                                    showNotification("EC Level ", "High");
                                }
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

    private void showNotification(String sensor, String status) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        String CHANNEL_ID = UUID.randomUUID().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.

            notificationChannel.setDescription("Sample Channel description");

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notification = new NotificationCompat.Builder(getActivity(), CHANNEL_ID).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis()).setContentTitle("Switch Notice").setContentText(sensor + "is now " + status).setSmallIcon(R.mipmap.ic_launcher).build();
        notificationManager.notify(1, notification);
    }
}
