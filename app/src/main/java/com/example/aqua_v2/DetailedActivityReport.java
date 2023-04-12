package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aqua_v2.model.InfoModel;
import com.example.aqua_v2.model.TempModel;
import com.example.aqua_v2.model.TemperatureSensor;

import java.util.ArrayList;
import java.util.List;

public class DetailedActivityReport extends AppCompatActivity {


    TextView sensorName;
    private RecyclerView recyclerView;
    RecyclerView infoRecycle;
    private List<TemperatureSensor> dataList = new ArrayList<>();
    private String sensor;
    private List<InfoModel> info = new ArrayList<>();
    ImageButton infoBtn;
    ImageButton reportBtn;
    private String report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);
        recyclerView = findViewById(R.id.recyclerView);
        sensorName = findViewById(R.id.sensorName);
        infoBtn = findViewById(R.id.infoBtn);
        reportBtn = findViewById(R.id.reportBtn);
        Bundle bundle = getIntent().getExtras();
        dataList = bundle.<TempModel>getParcelable("data").getTemperatureSensors();
        sensor = bundle.getString("sensorName");
        sensorName.setText(sensor);
        getSensorData();
        if(sensor.equals("pH Level")){
            infoBtn.setVisibility(View.VISIBLE);
        }else {
            infoBtn.setVisibility(View.INVISIBLE);
        }
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(DetailedActivityReport.this);
                dialog.setContentView(R.layout.info_diaglog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                infoRecycle = dialog.findViewById(R.id.infoRecycle);
                if(sensor.equals("pH Level")){
                    addInfo();
                    InfoAdapter adapter= new InfoAdapter(info);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    infoRecycle.setLayoutManager(layoutManager);
                    infoRecycle.setItemAnimator(new DefaultItemAnimator());
                    infoRecycle.setAdapter(adapter);
                    dialog.show();
                }

            }
        });
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sensor.equals("pH Level")){
                    report = "ph_level";
                }else if(sensor.equals("ECC Level")){
                    report = "ec_level";
                }
                Intent intent = new Intent(DetailedActivityReport.this, GreenhouseRerportActivity.class);
                intent.putExtra("sensor", report );
                startActivity(intent);
            }
        });

    }

    private void addInfo() {
        info.add(new InfoModel("0","Bad"));
        info.add(new InfoModel("1","Bad"));
        info.add(new InfoModel("2","Bad"));
        info.add(new InfoModel("3","Bad"));
        info.add(new InfoModel("4","Bad"));
        info.add(new InfoModel("5","Good"));
        info.add(new InfoModel("6","Good"));
        info.add(new InfoModel("7","Good"));
        info.add(new InfoModel("8","Bad"));
        info.add(new InfoModel("9","Bad"));
        info.add(new InfoModel("10","Bad"));
        info.add(new InfoModel("11","Bad"));
        info.add(new InfoModel("12","Bad"));
        info.add(new InfoModel("13","Bad"));
        info.add(new InfoModel("14","Bad"));


    }

    private void getSensorData() {
        recycleTemperatureData recycleData = new recycleTemperatureData((ArrayList<TemperatureSensor>) dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleData);
    }


}