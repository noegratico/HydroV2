package com.example.aqua_v2;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DevicesActivity extends AppCompatActivity {
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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");
    private final MutableLiveData<Boolean> verify = new MutableLiveData<>(true);

    MaterialButton growLightBtn, coolingFanStatusBtn, liveCameraBtn, pairedDevices;
    TextInputEditText timerInput;
    TextInputEditText tempInput;
    MaterialButton saveBtn;
    Switch lightSwitch;
    Switch airPumpSwitch;
    Switch coolingFanSwitch;
    Switch nightTimeSwitch;
    Switch blokedSwitch;
    MaterialButton okBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean checkSwitch;
    private boolean checkAirSwitch;
    private boolean checkCoolingSwitch;
    private boolean checkNightSwitch;
    private boolean checkBlockedSwitch;

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
        setContentView(R.layout.activity_devices_sched);

        settingBtn = findViewById(R.id.settingBtn);
        growLightBtn = findViewById(R.id.wpumpschedBtn);
        coolingFanStatusBtn = findViewById(R.id.coolingFanStatusBtn);
        lightSwitch = findViewById(R.id.wpumpSwitch);
        airPumpSwitch = findViewById(R.id.airPumpSwitch);
        coolingFanSwitch = findViewById(R.id.coolingFan);
//        liveCameraBtn = findViewById(R.id.liveCameraBtn);
//        pairedDevices = findViewById(R.id.pairedDevicesBtn);
        settings();
        Dialog dialog = new Dialog(DevicesActivity.this);
//grow light status
        growLightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.activity_scheduler_devices);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                closeBtn = dialog.findViewById(R.id.closeBtn);
                nightTimeSwitch = dialog.findViewById(R.id.nihtTimeSwitch);
                blokedSwitch = dialog.findViewById(R.id.blockedSwitch);
                okBtn = dialog.findViewById(R.id.okBtn);
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                db.collection("scheduler").document("grow_light").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        blokedSwitch.setChecked((Boolean) value.get("blocked_switch"));
                        nightTimeSwitch.setChecked((Boolean) value.get("night_switch"));
                        checkBlockedSwitch = (boolean) value.get("blocked_switch");
                        checkNightSwitch = (boolean) value.get("night_switch");

                    }
                });

                nightTimeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject object = new JSONObject();
                        JSONObject data = new JSONObject();
                        if (checkNightSwitch) {
                            try {
                                object.put("docName", "grow_light");
                                data.put("night_switch", false);
                                object.put("data", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mFunctions
                                    .getHttpsCallable("scheduler")
                                    .call(object)
                                    .addOnSuccessListener((result -> {
                                        addUserLog("User Turn OFF Night Time Switch");
                                        Toast.makeText(DevicesActivity.this, "Night Time Switch is OFF", Toast.LENGTH_SHORT).show();
                                    }));
                        } else {
                            try {
                                object.put("docName", "grow_light");
                                data.put("night_switch", true);
                                object.put("data", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mFunctions
                                    .getHttpsCallable("scheduler")
                                    .call(object)
                                    .addOnSuccessListener((result -> {
                                        addUserLog("User Turn ON Night Time Switch");
                                        Toast.makeText(DevicesActivity.this, "Night Time Switch is ON", Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }
                });
                blokedSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject object = new JSONObject();
                        JSONObject data = new JSONObject();
                        if (checkBlockedSwitch) {
                            try {
                                object.put("docName", "grow_light");
                                data.put("blocked_switch", false);
                                object.put("data", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mFunctions
                                    .getHttpsCallable("scheduler")
                                    .call(object)
                                    .addOnSuccessListener((result -> {
                                        addUserLog("User Turn OFF Block Switch");
                                        Toast.makeText(DevicesActivity.this, "Block Switch is OFF", Toast.LENGTH_SHORT).show();
                                    }));
                        } else {
                            try {
                                object.put("docName", "grow_light");
                                data.put("blocked_switch", true);
                                object.put("data", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mFunctions
                                    .getHttpsCallable("scheduler")
                                    .call(object)
                                    .addOnSuccessListener((result -> {
                                        addUserLog("User Turn ON Block");
                                        Toast.makeText(DevicesActivity.this, "Block Switch is ON", Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                });


                dialog.show();
//                  close button
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

//    colling fan status
        coolingFanStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.activity_cooling_fan_scheduler);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                closeBtn = dialog.findViewById(R.id.closeBtn);
                saveBtn = dialog.findViewById(R.id.saveBtn);
                timerInput = dialog.findViewById(R.id.timerInput);
                tempInput = dialog.findViewById(R.id.tempInput);
                dialog.show();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject object = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            object.put("docName", "cooling_fan");
                            data.put("temperature", tempInput.getText());
                            data.put("timer", timerInput.getText());
                            object.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mFunctions
                                .getHttpsCallable("scheduler")
                                .call(object)
                                .addOnSuccessListener((result -> {
                                    addUserLog("User Scheduled a Cooling Fan");
                                    dialog.dismiss();
                                    Toast.makeText(DevicesActivity.this, "Cooling Fan has been Scheduled", Toast.LENGTH_SHORT).show();
                                }));
                    }
                });
//                  close button
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        db.collection("scheduler").document("cooling_fan").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                coolingFanSwitch.setChecked((Boolean) value.get("switch"));
                checkCoolingSwitch = (boolean) value.get("switch");
            }
        });
        db.collection("scheduler").document("air_pump").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                airPumpSwitch.setChecked((Boolean) value.get("switch"));
                checkAirSwitch = (boolean) value.get("switch");
            }
        });
        db.collection("scheduler").document("grow_light").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                lightSwitch.setChecked((Boolean) value.get("switch"));
                checkSwitch = (boolean) value.get("switch");
            }
        });

        coolingFanSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                JSONObject data = new JSONObject();
                if (checkCoolingSwitch) {
                    try {
                        object.put("docName", "cooling_fan");
                        data.put("switch", false);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn OFF Cooling Fan");
                                Toast.makeText(DevicesActivity.this, "Cooling Fan is OFF", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    try {
                        object.put("docName", "cooling_fan");
                        data.put("switch", true);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn ON Cooling Fan");
                                Toast.makeText(DevicesActivity.this, "Cooling Fan is ON", Toast.LENGTH_SHORT).show();
                            }));
                }
            }

        });
        airPumpSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                JSONObject data = new JSONObject();
                if (checkAirSwitch) {
                    try {
                        object.put("docName", "air_pump");
                        data.put("switch", false);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn OFF Air Pump");
                                Toast.makeText(DevicesActivity.this, "Air Pump is OFF", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    try {
                        object.put("docName", "air_pump");
                        data.put("switch", true);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn ON Air Pump");
                                Toast.makeText(DevicesActivity.this, "Air Pump is ON", Toast.LENGTH_SHORT).show();
                            }));
                }
            }

        });
        lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                JSONObject data = new JSONObject();
                if (checkSwitch) {
                    try {
                        object.put("docName", "grow_light");
                        data.put("switch", false);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn OFF Grow Light");
                                Toast.makeText(DevicesActivity.this, "Grow Light is OFF", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    try {
                        object.put("docName", "grow_light");
                        data.put("switch", true);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn ON Grow Light");
                                Toast.makeText(DevicesActivity.this, "Grow Light is ON", Toast.LENGTH_SHORT).show();
                            }));
                }
            }
        });
    }


    private void settings() {
        Dialog dialog = new Dialog(DevicesActivity.this);
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
                    verify.observe(DevicesActivity.this, verifyState -> {
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
                                addUserLog("User " + userName.getText() + " Verified The Account");
                                Toast.makeText(DevicesActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                                addUserLog("User " + editName.getText().toString() + " Profile Updated");
                                                Toast.makeText(DevicesActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(DevicesActivity.this, MemberListActivity.class);
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
                            Intent logoutIntent = new Intent(DevicesActivity.this, MainActivity.class);
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
                } else if (id == 4) {
                    startActivity(new Intent(DevicesActivity.this, UserLogActivity.class));
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