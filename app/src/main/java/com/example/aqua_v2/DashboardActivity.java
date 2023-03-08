package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.appsearch.AppSearchBatchResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aqua_v2.fragments.DeviceDashboardActivity;
import com.example.aqua_v2.fragments.GreenhouseDashboardActivity;
import com.example.aqua_v2.fragments.WaterLevelDashboardActivity;
import com.example.aqua_v2.model.Weather;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageButton settingBtn, closeBtn;
    MaterialButton cancelBtn, changePassword, editProfile;
    ViewPager pager;
    PagerAdapter pagerAdapter;

    TextView temperatureTxt, weatherTxt;
    ImageView weatherIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        settingBtn = findViewById(R.id.settingBtn);
        temperatureTxt = findViewById(R.id.temperatureTxt);
        weatherTxt = findViewById(R.id.weatherTxt);
        weatherIcon = findViewById(R.id.weather_icon);

        Dialog dialog = new Dialog(DashboardActivity.this);

        PopupMenu popupMenu = new PopupMenu(this, settingBtn);

        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Member");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Logout");


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

//                profile menu
                if (id == 0) {
                    dialog.setContentView(R.layout.activity_profile);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    closeBtn = dialog.findViewById(R.id.closeBtn);
                    changePassword = dialog.findViewById(R.id.changePassword);
                    editProfile = dialog.findViewById(R.id.editProfile);


                    dialog.show();
//                  close button
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    //change password
                    changePassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.setContentView(R.layout.activity_change_password);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            closeBtn = dialog.findViewById(R.id.closeBtn);
                            dialog.show();
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

//                            add change password function here
                        }
                    });
//                    editProfile button
                    editProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.setContentView(R.layout.activity_edit_profile);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            closeBtn = dialog.findViewById(R.id.closeBtn);
                            dialog.show();
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

//                            add edit profile function here
                        }
                    });

                }
//                theme menu
                else if (id == 1) {
                    dialog.setContentView(R.layout.activity_theme_dialog);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    closeBtn = dialog.findViewById(R.id.closeBtn);
                    dialog.show();

                    switcher = dialog.findViewById(R.id.themeSwitch);

                    if (nightMode) {
                        switcher.setChecked(true);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        switcher.setChecked(false);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    switcher.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nightMode) {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                editor = sharedPreferences.edit();
                                editor.putBoolean("night", false);
                            } else {
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                editor = sharedPreferences.edit();
                                editor.putBoolean("night", true);
                            }
                            editor.commit();
                        }
                    });
//                  close btn
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
//                   add dark theme function here

//                    Member menu
                } else if (id == 2) {
                    Intent intent = new Intent(DashboardActivity.this, MemberListActivity.class);
                    startActivity(intent);
                }
//                lag-out menu
                else if (id == 3) {
                    dialog.setContentView(R.layout.activity_logout_dialog);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    closeBtn = dialog.findViewById(R.id.closeBtn);
                    cancelBtn = dialog.findViewById(R.id.cancel_button);

                    dialog.show();
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

//                    add logout function here
                }

                return false;
            }


        });


        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });


//        weather forecast
      
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = "https://api.open-meteo.com/v1/forecast?latitude=14.65&longitude=121.05&hourly=temperature_2m&current_weather=true";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject currentWeather = response.getJSONObject("current_weather");

                            String temp = currentWeather.getString("temperature");
                            int wCode = currentWeather.getInt("weathercode");

                            temperatureTxt.setText(temp + "°C");
                            if(wCode == 0){
                                weatherTxt.setText("Clear");
                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.clear));
                            }
                            else if(wCode == 1 || wCode == 2 || wCode == 3){
                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
                                switch (wCode){
                                    case 1:
                                        weatherTxt.setText("Mainly Clear");
                                        break;
                                    case 2:
                                        weatherTxt.setText("Partly Cloudy");
                                        break;
                                    case 3:
                                        weatherTxt.setText("Overcast");
                                        break;
                                }
                            }else if(wCode == 45 || wCode == 48){
                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
                                switch (wCode){
                                    case 45:
                                        weatherTxt.setText("Fog");
                                        break;
                                    case 48:
                                        weatherTxt.setText("Depositing Rime Fog");
                                        break;
                                }
                            }else if(wCode == 51 || wCode == 53 || wCode == 55 ){
                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
                                switch (wCode){
                                    case 51:
                                        weatherTxt.setText("Drizzle: Light ");
                                        break;
                                    case 53:
                                        weatherTxt.setText("Drizzle: Moderate");
                                        break;
                                    case 55:
                                        weatherTxt.setText("Drizzle: Dense Intensity");
                                        break;
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        temperatureTxt.setText("error ");
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);
                requestQueue.add(jsonObjectRequest);
            }
        },100);




        List<Fragment> list = new ArrayList<>();
        list.add(new GreenhouseDashboardActivity());
        list.add(new WaterLevelDashboardActivity());
        list.add(new DeviceDashboardActivity());


        pager = findViewById(R.id.pager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(pagerAdapter);
    }


}