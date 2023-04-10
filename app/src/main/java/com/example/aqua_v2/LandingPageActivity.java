package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.aqua_v2.fragments.DeviceDashboardActivity;
import com.example.aqua_v2.fragments.GreenhouseDashboardActivity;
import com.example.aqua_v2.fragments.OnboardingFive;
import com.example.aqua_v2.fragments.OnboardingFour;
import com.example.aqua_v2.fragments.OnboardingOne;
import com.example.aqua_v2.fragments.OnboardingThree;
import com.example.aqua_v2.fragments.OnboardingTwo;
import com.example.aqua_v2.fragments.OnboardingZero;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class LandingPageActivity extends AppCompatActivity {

    boolean nightMode;
    ViewPager landingPager;
    private LandingPageAdapter landingPageAdapter;
    MaterialButton gotItBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        landingPager = findViewById(R.id.landingPager);
        gotItBtn = findViewById(R.id.materialButton4);

        List<Fragment> list = new ArrayList<>();
        list.add(new OnboardingZero());
        list.add(new OnboardingOne());
        list.add(new OnboardingTwo());
        list.add(new OnboardingThree());
        list.add(new OnboardingFour());
        list.add(new OnboardingFive());

        landingPageAdapter = new LandingPageAdapter(getSupportFragmentManager(),list);
        landingPager.setAdapter(landingPageAdapter);

        gotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingPageActivity.this, SelectionActivity.class));
            }
        });

    }
}