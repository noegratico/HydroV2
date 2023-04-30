package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aqua_v2.model.TempModel;
import com.example.aqua_v2.model.TemperatureSensor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaterConditionActivity extends AppCompatActivity {
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
    private final MutableLiveData<Boolean> verify = new MutableLiveData<>();


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");
    private List<TemperatureSensor> phList = new ArrayList<>();
    private List<TemperatureSensor> eccList = new ArrayList<>();
    TextView phDisplay;
    TextView eccDisplay;
    RecyclerView phRecycle;
    RecyclerView ecRecycle;
    MaterialButton viewAllDataBtn;
    ImageButton reportSpecBtn;
    MaterialCardView cardBtn;
    MaterialCardView ecCardBtn;
    private int count;
    private int count1;

    private String name;
    private String email;
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
        setContentView(R.layout.activity_water_condition);
        viewAllDataBtn = findViewById(R.id.viewAllDataBtn);
        phDisplay = findViewById(R.id.phDisplay);
        eccDisplay = findViewById(R.id.eccDisplay);
        settingBtn = findViewById(R.id.settingBtn);
        phRecycle = findViewById(R.id.phRecycle);
        ecRecycle = findViewById(R.id.eccRecent);
        reportSpecBtn = findViewById(R.id.reportSpecBtn);
        cardBtn = findViewById(R.id.tempHolder);
        ecCardBtn = findViewById(R.id.humidityHolder);


        settings();
        phRecent();
        ecRecent();
        cardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterConditionActivity.this, DetailedActivityReport.class);
                Bundle bundle = new Bundle();
                TempModel tempModel = new TempModel();
                tempModel.setTemperatureSensors((ArrayList<TemperatureSensor>) phList);
//                TempModel humModel = new TempModel();
//                humModel.setTemperatureSensors((ArrayList<TemperatureSensor>) eccList);
                bundle.putParcelable("data", tempModel);
                bundle.putString("sensor", "ph_level");
                bundle.putInt("count", count);
//                bundle.putParcelable("humData", humModel);
                bundle.putString("sensorName", "pH Level");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ecCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterConditionActivity.this, DetailedActivityReport.class);
                Bundle bundle = new Bundle();
                TempModel humModel = new TempModel();
                humModel.setTemperatureSensors((ArrayList<TemperatureSensor>) eccList);
                bundle.putParcelable("data", humModel);
                bundle.putString("sensor", "ec_level");
                bundle.putString("sensorName", "ECC Level");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        viewAllDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterConditionActivity.this, GreenHouseViewAllDataActivity.class);
                Bundle bundle = new Bundle();
                TempModel tempModel = new TempModel();
                tempModel.setTemperatureSensors((ArrayList<TemperatureSensor>) phList);
                TempModel humModel = new TempModel();
                humModel.setTemperatureSensors((ArrayList<TemperatureSensor>) eccList);
                bundle.putString("phText", "ph Level");
                bundle.putString("sensor1", "ph_level");
                bundle.putString("sensor2", "ec_level");
                bundle.putInt("count", count);
                bundle.putInt("count1", count1);
                bundle.putString("eccText", "ECC Level");
                bundle.putParcelable("data", tempModel);
                bundle.putParcelable("humData", humModel);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        reportSpecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaterConditionActivity.this, GreenhouseRerportActivity.class);
                intent.putExtra("sensor", "ph_level");
                startActivity(intent);
            }
        });

    }


    private void phRecent() {
        HashMap<String, Object> humData = new HashMap<>();
        humData.put("collectionName", "ph_level");
        humData.put("limit", 25);
        mFunctions
                .getHttpsCallable("getAllSensorData")
                .call(humData)
                .addOnSuccessListener(result -> {
                    HashMap<String, ArrayList<HashMap<String, Object>>> resultData = (HashMap<String, ArrayList<HashMap<String, Object>>>) result.getData();
                    count = ((HashMap<String, Integer>) result.getData()).get("count");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        phList = resultData.get("data").stream().map(tempRecord -> {
                            Map<String, Integer> datetime = (HashMap<String, Integer>) tempRecord.get("datetime");
                            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
//                            if need
//                            SimpleDateFormat time = new SimpleDateFormat("hh:mm");
                            TemperatureSensor tempData = new TemperatureSensor(jdf.format(new Date(datetime.get("_seconds") * 1000L)), (String) tempRecord.get("value"));
                            return tempData;
                        }).collect(Collectors.toList());
                        phList.stream().findFirst().ifPresent(humResult -> {
                            phDisplay.setText(humResult.getValue());
                        });
                        recentAdapter recentAdapter = new recentAdapter((ArrayList<TemperatureSensor>)
                                phList
                                        .stream()
                                        .limit(3)
                                        .collect(Collectors.toList()));
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        phRecycle.setLayoutManager(layoutManager);
                        phRecycle.setItemAnimator(new DefaultItemAnimator());
                        phRecycle.setAdapter(recentAdapter);
                    }
                }).addOnFailureListener(result -> {
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void ecRecent() {
        HashMap<String, Object> ecData = new HashMap<>();
        ecData.put("collectionName", "ec_level");
        ecData.put("limit", 25);
        mFunctions
                .getHttpsCallable("getAllSensorData")
                .call(ecData)
                .addOnSuccessListener(result -> {
                    HashMap<String, ArrayList<HashMap<String, Object>>> resultData = (HashMap<String, ArrayList<HashMap<String, Object>>>) result.getData();
                    count1 = ((HashMap<String, Integer>) result.getData()).get("count");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        eccList = resultData.get("data").stream().map(tempRecord -> {
                            Map<String, Integer> datetime = (HashMap<String, Integer>) tempRecord.get("datetime");
                            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
//                            if need
//                            SimpleDateFormat time = new SimpleDateFormat("hh:mm");
                            TemperatureSensor tempData = new TemperatureSensor(jdf.format(new Date(datetime.get("_seconds") * 1000L)), (String) tempRecord.get("value"));
                            return tempData;
                        }).collect(Collectors.toList());
                        eccList.stream().findFirst().ifPresent(humResult -> {
                            eccDisplay.setText(humResult.getValue());
                        });
                        recentAdapter recentAdapter = new recentAdapter((ArrayList<TemperatureSensor>)
                                eccList
                                        .stream()
                                        .limit(3)
                                        .collect(Collectors.toList()));
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        ecRecycle.setLayoutManager(layoutManager);
                        ecRecycle.setItemAnimator(new DefaultItemAnimator());
                        ecRecycle.setAdapter(recentAdapter);
                    }
                }).addOnFailureListener(result -> {
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void settings() {
        Dialog dialog = new Dialog(WaterConditionActivity.this);
        PopupMenu popupMenu = new PopupMenu(this, settingBtn);

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult result) {
                    boolean isAdmin = result.getClaims().containsKey("admin") && (boolean) result.getClaims().get("admin");
                    if (isAdmin) {
                        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Member");
                        popupMenu.getMenu().add(Menu.NONE, 3, 4, "Logout");
                        popupMenu.getMenu().add(Menu.NONE, 4, 3, "User Log");
                    } else {
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
                    verify.observe(WaterConditionActivity.this, verifyState -> {
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

                                name = (String) data.get("name");
                                email = (String) data.get("email");
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
                                addUserLog("User " + userName.getText() + " Verified The Account");
                                Toast.makeText(WaterConditionActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                    if (editName.getText().toString() != null && editName.getText().toString() != name) {
                                        data.put("name", editName.getText().toString());
                                    }
                                    if (editEmail.getText().toString() != null && editEmail.getText().toString() != email) {
                                        data.put("email", editEmail.getText().toString());
                                    }
                                    mFunctions
                                            .getHttpsCallable("updateUserInfo")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                addUserLog("User " + editName.getText().toString() + " Profile Updated");
                                                Toast.makeText(WaterConditionActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(WaterConditionActivity.this, MemberListActivity.class);
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
                            FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid())
                                    .update("isLogin", false)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseAuth.getInstance().signOut();
                                            Intent logoutIntent = new Intent(WaterConditionActivity.this, MainActivity.class);
                                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(logoutIntent);
                                            finish();
                                        }
                                    });
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
                } else if (id == 4) {
                    startActivity(new Intent(WaterConditionActivity.this, UserLogActivity.class));
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

    private void addUserLog(String userActivity) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        Map<String, String> data = new HashMap<>();
        data.put("activity", userActivity);
        data.put("datetime", currentDateAndTime);
        mFunctions
                .getHttpsCallable("logUserActivity")
                .call(data);
    }
}