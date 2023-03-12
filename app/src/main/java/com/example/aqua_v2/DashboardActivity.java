package com.example.aqua_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aqua_v2.fragments.DeviceDashboardActivity;
import com.example.aqua_v2.fragments.GreenhouseDashboardActivity;
import com.example.aqua_v2.fragments.WaterLevelDashboardActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


public class DashboardActivity extends AppCompatActivity {

    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageButton settingBtn, closeBtn;
    MaterialButton cancelBtn, changePassword, editProfile, logout;
    ViewPager pager;
    PagerAdapter pagerAdapter;

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView temperatureTxt, weatherTxt, dateTxt, cityTxt;
    ImageView weatherIcon;
    private final static int REQUEST_CODE = 100;

   public static double latitude;
   public static double longitude;

   private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        dateTxt = findViewById(R.id.dateTxt);
        cityTxt = findViewById(R.id.cityTxt);

        FirebaseUser user = mAuth.getCurrentUser();

        Dialog dialog = new Dialog(DashboardActivity.this);

        PopupMenu popupMenu = new PopupMenu(this, settingBtn);

        if(user != null) {
                user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult result) {
                        boolean isAdmin = result.getClaims().containsKey("admin") && (boolean) result.getClaims().get("admin");
                        if(isAdmin) {
                            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                            popupMenu.getMenu().add(Menu.NONE, 2, 2, "Member");
                            popupMenu.getMenu().add(Menu.NONE, 3, 3, "Logout");
                        }else{
                            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                            popupMenu.getMenu().add(Menu.NONE, 3, 2, "Logout");
                        }
                    }
                });


        }



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
                    logout = dialog.findViewById(R.id.logoutBtn);

                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent = new Intent(DashboardActivity.this, MainActivity.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logoutIntent);
                            finish();
                        }
                    });
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

//location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();


//        weather forecast
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//                String url ="https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current_weather=true";
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONObject currentWeather = response.getJSONObject("current_weather");
//
//                            String temp = currentWeather.getString("temperature");
//                            int wCode = currentWeather.getInt("weathercode");
//                            temperatureTxt.setText(temp + "°C");
//                            if (wCode == 0) {
//                                weatherTxt.setText("Clear");
//                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.clear));
//                            } else if (wCode == 1 || wCode == 2 || wCode == 3) {
//                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
//                                switch (wCode) {
//                                    case 1:
//                                        weatherTxt.setText("Mainly Clear");
//                                        break;
//                                    case 2:
//                                        weatherTxt.setText("Partly Cloudy");
//                                        break;
//                                    case 3:
//                                        weatherTxt.setText("Overcast");
//                                        break;
//                                }
//                            } else if (wCode == 45 || wCode == 48) {
//                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
//                                switch (wCode) {
//                                    case 45:
//                                        weatherTxt.setText("Fog");
//                                        break;
//                                    case 48:
//                                        weatherTxt.setText("Depositing Rime Fog");
//                                        break;
//                                }
//                            } else if (wCode == 51 || wCode == 53 || wCode == 55) {
//                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
//                                switch (wCode) {
//                                    case 51:
//                                        weatherTxt.setText("Drizzle: Light ");
//                                        break;
//                                    case 53:
//                                        weatherTxt.setText("Drizzle: Moderate");
//                                        break;
//                                    case 55:
//                                        weatherTxt.setText("Drizzle: Dense Intensity");
//                                        break;
//                                }
//                            }
//                            else {
//                                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
//                                weatherTxt.setText("Something went Wrong");
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        temperatureTxt.setText("error ");
//                    }
//                });
//                RequestQueue requestQueue = Volley.newRequestQueue(DashboardActivity.this);
//                requestQueue.add(jsonObjectRequest);
//            }
//        }, 1000);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        dateTxt.setText(currentDate);


        List<Fragment> list = new ArrayList<>();
        list.add(new GreenhouseDashboardActivity());
        list.add(new WaterLevelDashboardActivity());
        list.add(new DeviceDashboardActivity());


        pager = findViewById(R.id.pager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(pagerAdapter);

    }

    private void getTempWeatherCode(double latitude, double longitude) {
        String url ="https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current_weather=true";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject currentWeather = response.getJSONObject("current_weather");

                    String temp = currentWeather.getString("temperature");
                    int wCode = currentWeather.getInt("weathercode");
                    temperatureTxt.setText(temp + "°C");
                    if (wCode == 0) {
                        weatherTxt.setText("Clear");
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.clear));
                    } else if (wCode == 1 || wCode == 2 || wCode == 3) {
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
                        switch (wCode) {
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
                    } else if (wCode == 45 || wCode == 48) {
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
                        switch (wCode) {
                            case 45:
                                weatherTxt.setText("Fog");
                                break;
                            case 48:
                                weatherTxt.setText("Depositing Rime Fog");
                                break;
                        }
                    } else if (wCode == 51 || wCode == 53 || wCode == 55) {
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
                        switch (wCode) {
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
                    else {
                        weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
                        weatherTxt.setText("Something went Wrong");
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


    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(DashboardActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    latitude = addresses.get(0).getLatitude();
                                    longitude = addresses.get(0).getLongitude();
                                    DecimalFormat decimalFormat = new DecimalFormat("#.0000");

                                    double la = Double.parseDouble(decimalFormat.format(latitude));
                                    double lo = Double.parseDouble(decimalFormat.format(longitude));
                                    getTempWeatherCode(la, lo);
                                    cityTxt.setText(addresses.get(0).getLocality()+ ", PH");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    });


        } else {

            askPermission();

        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(DashboardActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}