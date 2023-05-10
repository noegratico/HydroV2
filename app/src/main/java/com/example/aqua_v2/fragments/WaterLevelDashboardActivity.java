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
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.ComboBoxActivity;
import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterActivity;
import com.example.aqua_v2.WaterLightActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class WaterLevelDashboardActivity extends Fragment {
    TextView waterBox;
    TextView waterTxt;
    TextView lightResistance;
    TextView snapB;
    MaterialButton pumpBtn;
    ImageButton gHBtn;
    CardView plantBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Notification notification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_water_level, container, false);
        waterTxt = (TextView) rootView.findViewById(R.id.waterTxtBtn);
        waterBox = (TextView) rootView.findViewById(R.id.waterBox);
//        snapA = (TextView) rootView.findViewById(R.id.snapATxt);
//        snapB = (TextView) rootView.findViewById(R.id.snapBTxt);
        lightResistance = rootView.findViewById(R.id.lightResistance);
        pumpBtn = (MaterialButton) rootView.findViewById(R.id.pumpBtn);
        gHBtn = rootView.findViewById(R.id.gHBtn);
        plantBtn = rootView.findViewById(R.id.plantBtn);

        waterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                startActivity(intent);

            }
        });
        db.collection("water_leak_2").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
//                            int lightLevel = Integer.parseInt(documentChange.getDocument().get("value", String.class));
//
//                            if (lightLevel <= 960) {
//                                showNotification("Light Resistance Level", "Low");
//                            } else if (lightLevel >= 1080) {
//                                showNotification("Light Resistance Level", "High");
//                            }
                            lightResistance.setText(documentChange.getDocument().get("value", String.class));
                        }
                    });

                }

            }
        });



        db.collection("water_leak_1").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
//                            int waterlevel = Integer.parseInt(documentChange.getDocument().get("value", String.class));
//                            if (waterlevel <= 20) {
//                                showNotification("Water Level", "Low");
//                            } else if (waterlevel >= 90) {
//                                showNotification("Water Level", "High");
//                            }
                            waterBox.setText(documentChange.getDocument().get("value", String.class));
                        }
                    });
                }
            }
        });
     /*   db.collection("snap_a").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            snapA.setText(documentChange.getDocument().get("value", String.class) +"%");
                        }
                    });
                }
            }
        });
        db.collection("snap_b").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            snapB.setText(documentChange.getDocument().get("value", String.class) + "%");
                        }
                    });
                }
            }
        });*/
        pumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), WaterActivity.class);
               db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       intent.putExtra("currentUserLevel", (String) documentSnapshot.get("userLevel"));
                       startActivity(intent);
                   }
               });

            }
        });
        gHBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WaterLightActivity.class));
            }
        });

        plantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ComboBoxActivity.class));
            }
        });
        return rootView;
    }

    private void showNotification(String name, String status) {

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
//        String switchStatus = (sensorSwitch) ? "ON" : "OFF";
        notification = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Switch Notice")
                .setContentText(name + " is now " + status)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        notificationManager.notify(1, notification);
    }
}
