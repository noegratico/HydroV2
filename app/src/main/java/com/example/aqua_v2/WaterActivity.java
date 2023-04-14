package com.example.aqua_v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WaterActivity extends AppCompatActivity {
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

    //    pump button variable
    MaterialButton wPumpBtn;
    MaterialButton saveBtn;
    MaterialButton literBtn;
    MaterialButton mlBtn;
    TextInputEditText timerInput;
    TextInputEditText valueInput;
    private String unit = "liter";


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Switch waterPumpSwitch;
    private boolean checkPump;


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
        setContentView(R.layout.activity_pumps);

        settingBtn = findViewById(R.id.settingBtn);
        wPumpBtn = findViewById(R.id.wpumpschedBtn);
        waterPumpSwitch = findViewById(R.id.wpumpSwitch);
//        snapABtn = findViewById(R.id.snapaschedBtn);
//        snapBBtn = findViewById(R.id.snapbschedBtn);

        settings();
        db.collection("scheduler").document("water_pump").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                waterPumpSwitch.setChecked((Boolean) value.get("switch"));
                checkPump = (boolean) value.get("switch");
            }
        });

        waterPumpSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                JSONObject data = new JSONObject();
                if (checkPump) {
                    try {
                        object.put("docName", "water_pump");
                        data.put("switch", false);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn OFF Water Pump Switch");
                                Toast.makeText(WaterActivity.this, "Water Pump Switch is OFF", Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    try {
                        object.put("docName", "water_pump");
                        data.put("switch", true);
                        object.put("data", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("scheduler")
                            .call(object)
                            .addOnSuccessListener((result -> {
                                addUserLog("User Turn ON Water Pump Switch");
                                Toast.makeText(WaterActivity.this, "Water Pump Switch is ON", Toast.LENGTH_SHORT).show();
                            }));
                }
            }
        });
//        water pump schedualer
        wPumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(WaterActivity.this);
                dialog.setContentView(R.layout.activity_scheduler);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                closeBtn = dialog.findViewById(R.id.closeBtn);
                saveBtn = dialog.findViewById(R.id.saveBtn);
                timerInput = dialog.findViewById(R.id.timerInput);
                valueInput = dialog.findViewById(R.id.valueInput);
                literBtn = dialog.findViewById(R.id.litterBtn);
                mlBtn = dialog.findViewById(R.id.mlBtn);

                dialog.show();
                literBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unit = "liter";
                        literBtn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#ff0000")));

                        mlBtn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#6cbce2")));
                    }
                });
                mlBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unit = "milliliter";
                        literBtn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#6cbce2")));
                        mlBtn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                    }
                });
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject object = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            object.put("docName", "water_pump");
                            data.put("value", Integer.parseInt(valueInput.getText().toString()));
                            data.put("timer", timerInput.getText().toString());
                            data.put("unit", unit);
                            object.put("data", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mFunctions
                                .getHttpsCallable("scheduler")
                                .call(object)
                                .addOnSuccessListener((result) -> {
                                    addUserLog("User Scheduled a Water Pump");
                                    dialog.dismiss();
                                    Toast.makeText(WaterActivity.this, "Water Pump has been Scheduled", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener((e) -> {
                                    Log.e("SCHEDULER ERROR", e.getMessage(), e);
                                    Toast.makeText(WaterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

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

    }

    private void settings() {
        Dialog dialog = new Dialog(WaterActivity.this);
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
                    verify.observe(WaterActivity.this, verifyState -> {
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
                                Toast.makeText(WaterActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(WaterActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(WaterActivity.this, MemberListActivity.class);
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
                            Intent logoutIntent = new Intent(WaterActivity.this, MainActivity.class);
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
                    startActivity(new Intent(WaterActivity.this, UserLogActivity.class));
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