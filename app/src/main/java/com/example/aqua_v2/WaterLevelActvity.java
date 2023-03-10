package com.example.aqua_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class WaterLevelActvity extends AppCompatActivity {
    public MaterialButton pumpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_level);

        pumpBtn = findViewById(R.id.pumpBtn);

        pumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaterLevelActvity.this, PumpsActivity.class);
                startActivity(intent);
            }
        });
    }

}
