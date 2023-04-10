package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelectionActivity extends AppCompatActivity {

    MaterialButton signInRedirect;
    MaterialButton howToUseBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfFirst();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        signInRedirect = findViewById(R.id.signInRidirect);
        howToUseBtn = findViewById(R.id.howToUseBtn);

        signInRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectionActivity.this, MainActivity.class));
            }
        });
        howToUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectionActivity.this, LandingPageActivity.class));
            }
        });

    }

    private void checkIfFirst() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        boolean firstTime = sharedPreferences.getBoolean("FirstTimeInstall",true);

        if(firstTime){
            startActivity(new Intent(SelectionActivity.this,LandingPageActivity.class));
        }
        editor = sharedPreferences.edit();
        editor.putBoolean("FirstTimeInstall",false);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }
}