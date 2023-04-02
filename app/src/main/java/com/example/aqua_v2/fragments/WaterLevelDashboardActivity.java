package com.example.aqua_v2.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aqua_v2.R;
import com.example.aqua_v2.WaterActivity;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class WaterLevelDashboardActivity extends Fragment {
TextView waterBox;
TextView waterTxt;
TextView snapA;
TextView snapB;
private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_water_level, container, false);
         waterTxt = (TextView) rootView.findViewById(R.id.waterTxtBtn);
        waterBox = (TextView) rootView.findViewById(R.id.waterBox);
        snapA = (TextView) rootView.findViewById(R.id.snapATxt);
        snapB = (TextView) rootView.findViewById(R.id.snapBTxt);

        waterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                startActivity(intent);

            }
        });

        db.collection("water_level").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            waterBox.setText(documentChange.getDocument().get("value", String.class) +"%");
                        }
                    });
                }
            }
        });
        db.collection("snap_a").orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        });
        return rootView;
    }
}
