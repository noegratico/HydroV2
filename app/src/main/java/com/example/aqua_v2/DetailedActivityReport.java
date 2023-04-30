package com.example.aqua_v2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aqua_v2.model.InfoModel;
import com.example.aqua_v2.model.TempModel;
import com.example.aqua_v2.model.TemperatureSensor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetailedActivityReport extends AppCompatActivity {
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
    private final MutableLiveData<Boolean> verify = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView sensorName;
    private RecyclerView recyclerView;
    RecyclerView infoRecycle;
    private List<TemperatureSensor> dataList = new ArrayList<>();
    private String sensor;
    private List<InfoModel> info = new ArrayList<>();
    ImageButton infoBtn;
    ImageButton reportBtn;
    private String report;
    private boolean isLoading = false;
    private int limit = 25;
    private int count;
    private recycleTemperatureData recycleData;
    TextView displaySensorText;

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
        setContentView(R.layout.activity_detailed_report);
        settingBtn = findViewById(R.id.settingBtn);
        recyclerView = findViewById(R.id.recyclerView);
        sensorName = findViewById(R.id.sensorName);
        infoBtn = findViewById(R.id.infoBtn);
        reportBtn = findViewById(R.id.reportBtn);
        displaySensorText = findViewById(R.id.realTimeView);
        Bundle bundle = getIntent().getExtras();
        dataList = bundle.<TempModel>getParcelable("data").getTemperatureSensors();
        count = bundle.getInt("count");
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
                }else if(sensor.equals("Water Level")){
                    report = "water_level";
                }else if(sensor.equals("Light Resistance")){
                    report = "light_resistance";
                }else if(sensor.equals("Temperature")){
                    report = "temperature";
                }else if(sensor.equals("Humidity")){
                    report = "humidity";
                }
                Intent intent = new Intent(DetailedActivityReport.this, GreenhouseRerportActivity.class);
                intent.putExtra("sensor", report );
                startActivity(intent);
            }
        });
        settings();
        setupOnScrollListener();

        db.collection(getIntent().getStringExtra("sensor")).orderBy("datetime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getDocumentChanges().stream().findFirst().ifPresent(documentChange -> {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Log.d(TAG, documentChange.getDocument().get("value", String.class));
//                            Toast.makeText(getActivity(), documentChange.getDocument().get("value", String.class), Toast.LENGTH_SHORT).show();
                            displaySensorText.setText(documentChange.getDocument().get("value", String.class));
                        }
                    });
                }
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

    private void setupOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dataList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }

            }
        });
    }

    private void loadMore() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("collectionName", getIntent().getStringExtra("sensor"));
        data.put("limit", limit);
        data.put("pageIndex", (dataList.size() / limit) - 1);
        mFunctions
                .getHttpsCallable("getAllSensorData")
                .call(data)
                .addOnSuccessListener((result) -> {
                    HashMap<String, ArrayList<HashMap<String, Object>>> resultData = (HashMap<String, ArrayList<HashMap<String, Object>>>) result.getData();
                    count = ((HashMap<String, Integer>) result.getData()).get("count");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dataList.addAll(resultData.get("data").stream().map(tempRecord -> {
                            Map<String, Integer> datetime = (HashMap<String, Integer>) tempRecord.get("datetime");
                            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
                            //                            if need
                            //                            SimpleDateFormat time = new SimpleDateFormat("hh:mm");
                            TemperatureSensor tempData = new TemperatureSensor(jdf.format(new Date(datetime.get("_seconds") * 1000L)), (String) tempRecord.get("value") + "°c");
                            return tempData;
                        }).collect(Collectors.toList()));
                    }
                    recycleData.notifyItemInserted(dataList.size() - 1);
                    isLoading = false;
                });
    }


    private void getSensorData() {
        recycleData = new recycleTemperatureData((ArrayList<TemperatureSensor>) dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycleData);
    }
    private void settings() {
        Dialog dialog = new Dialog(DetailedActivityReport.this);
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
                    verify.observe(DetailedActivityReport.this, verifyState -> {
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
                                Toast.makeText(DetailedActivityReport.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                    if (editEmail.getText().toString() != null && editName.getText().toString() != email) {
                                        data.put("email", editEmail.getText().toString());
                                    }
                                    mFunctions
                                            .getHttpsCallable("updateUserInfo")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                addUserLog("User " + editName.getText().toString() + " Profile Updated");
                                                Toast.makeText(DetailedActivityReport.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(DetailedActivityReport.this, MemberListActivity.class);
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
                                            Intent logoutIntent = new Intent(DetailedActivityReport.this, MainActivity.class);
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
                    startActivity(new Intent(DetailedActivityReport.this, UserLogActivity.class));
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