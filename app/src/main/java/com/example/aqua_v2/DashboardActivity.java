package com.example.aqua_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aqua_v2.fragments.DeviceDashboardActivity;
import com.example.aqua_v2.fragments.GreenhouseDashboardActivity;
import com.example.aqua_v2.fragments.WaterLevelDashboardActivity;
import com.example.aqua_v2.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity {


    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageButton settingBtn, closeBtn;
    MaterialButton cancelBtn, changePassword, editProfile, logout;
    //    settings text
    MaterialButton saveProfileBtn;
    TextView userName;
    TextView userEmail;
    TextView userLevel;
    TextInputEditText editEmail;
    TextInputEditText editName;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView temperatureTxt, weatherTxt, dateTxt, cityTxt, userLvlTxt;
    ImageView weatherIcon;
    private final static int REQUEST_CODE = 100;

    public static double latitude;
    public static double longitude;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");
    private final MutableLiveData<Boolean> verify = new MutableLiveData<>(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkUser();
        showsplash();
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
        userLvlTxt = findViewById(R.id.userLvlTxt);


        settings();


//location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

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

    private void settings() {
        Dialog dialog = new Dialog(DashboardActivity.this);
        PopupMenu popupMenu = new PopupMenu(this, settingBtn);

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult result) {
                    boolean isAdmin = result.getClaims().containsKey("admin") && (boolean) result.getClaims().get("admin");
                    if (isAdmin) {
                        userLvlTxt.setText("Admin");
                        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Member");
                        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Logout");
                    } else {
                        userLvlTxt.setText("User");
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
//                    userId = dialog.findViewById(R.id.userId);
                    userName = dialog.findViewById(R.id.userName);
                    userEmail = dialog.findViewById(R.id.userEmail);
                    userLevel = dialog.findViewById(R.id.userLevel);
                    verify.observe(DashboardActivity.this, verifyState -> {
                        changePassword.setVisibility(verifyState ? View.GONE : View.VISIBLE);
                    });
                    mFunctions
                            .getHttpsCallable("getProfile")
                            .call()
                            .addOnSuccessListener(result -> {
                                HashMap<String, Object> data = (HashMap<String, Object>) result.getData();
                                userEmail.setText((String) data.get("email"));
                                userName.setText((String) data.get("name"));
                                userLevel.setText((String) data.get("userLevel"));
                                verify.setValue((Boolean) data.get("isEmailVerified"));
                            });

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
                            user.sendEmailVerification().addOnSuccessListener(result -> {
                                Toast.makeText(DashboardActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
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
                            saveProfileBtn = dialog.findViewById(R.id.profileSaveBtn);
                            editName = dialog.findViewById(R.id.editName);
                            editEmail = dialog.findViewById(R.id.editEmail);
                            editName.setText(userName.getText());
                            editEmail.setText(userEmail.getText());
                            saveProfileBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Map<String, String> data = new HashMap<>();
                                    if (editName.getText().toString() != null) {
                                        data.put("name", editName.getText().toString());
                                    }
                                    if (editEmail.getText().toString() != null) {
                                        data.put("email", editEmail.getText().toString());
                                    }
                                    mFunctions
                                            .getHttpsCallable("updateUserInfo")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                Toast.makeText(DashboardActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            });
                                }
                            });


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
    }

    private void checkUser() {
        if (user == null) {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    private void getTempWeatherCode(double latitude, double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";

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
                    } else {
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
                                    cityTxt.setText(addresses.get(0).getLocality() + ", PH");

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

    public void showsplash() {

        final Dialog splashDialog = new Dialog(DashboardActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        splashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        splashDialog.getWindow().getAttributes().windowAnimations = R.style.splashAnimation;
        splashDialog.setContentView(R.layout.activity_splash_screen);
        splashDialog.setCancelable(true);
        splashDialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    splashDialog.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, 2000);
    }
}