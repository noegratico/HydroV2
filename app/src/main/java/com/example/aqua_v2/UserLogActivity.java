package com.example.aqua_v2;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aqua_v2.model.UserLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.longrunning.WaitOperationRequest;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserLogActivity extends AppCompatActivity {

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> verify = new MutableLiveData<>(true);

    TextView titleText;
    SearchView searchView;

    RecyclerView recyclerView;

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
        setContentView(R.layout.activity_user_log);
        settingBtn = findViewById(R.id.settingBtn);
        recyclerView = findViewById(R.id.userLogView);
        titleText = findViewById(R.id.titleText);
        searchView = findViewById(R.id.searchView);
        settings();
        if (getIntent().getStringExtra("Log") == null) {
            viewUserLog();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("keyword", WordUtils.capitalizeFully(query));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("getAllUserLogs")
                            .call(object)
                            .addOnSuccessListener((result) -> {
                                ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                                    ).collect(Collectors.toList());
                                    UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("keyword", WordUtils.capitalizeFully(newText));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFunctions
                            .getHttpsCallable("getAllUserLogs")
                            .call(object)
                            .addOnSuccessListener((result) -> {
                                ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                                    ).collect(Collectors.toList());
                                    UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                    return true;
                }
            });
        } else if (getIntent().getStringExtra("Log").equals("Water")) {
            titleText.setText(getIntent().getStringExtra("Title"));
            showSensorLog(getIntent().getStringExtra("Log"));
        } else if (getIntent().getStringExtra("Log").equals("Air")) {
            titleText.setText(getIntent().getStringExtra("Title"));
            showSensorLog(getIntent().getStringExtra("Log"));
        } else if (getIntent().getStringExtra("Log").equals("Grow")) {
            titleText.setText(getIntent().getStringExtra("Title"));
            showSensorLog(getIntent().getStringExtra("Log"));
        } else if (getIntent().getStringExtra("Log").equals("Cool")) {
            titleText.setText(getIntent().getStringExtra("Title"));
            showSensorLog(getIntent().getStringExtra("Log"));
        }

    }

    private void showSensorLog(String log) {
        JSONObject object = new JSONObject();
        try {
            object.put("keyword", log);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mFunctions
                .getHttpsCallable("getAllUserLogs")
                .call(object)
                .addOnSuccessListener((result) -> {
                    ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                        ).collect(Collectors.toList());
                        UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                JSONObject dataSearch = new JSONObject();
                try {
                    dataSearch.put("keyword", log);
                    dataSearch.put("date", query);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mFunctions
                        .getHttpsCallable("getAllUserLogs")
                        .call(dataSearch)
                        .addOnSuccessListener((result) -> {
                            ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                                ).collect(Collectors.toList());
                                UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(adapter);
                            }
                        });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                JSONObject dataSearch = new JSONObject();
                try {
                    dataSearch.put("keyword", log);
                    dataSearch.put("date", newText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mFunctions
                        .getHttpsCallable("getAllUserLogs")
                        .call(dataSearch)
                        .addOnSuccessListener((result) -> {
                            ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                                ).collect(Collectors.toList());
                                UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(adapter);
                            }
                        });
                return true;
            }
        });

    }

    private void viewUserLog() {
        mFunctions
                .getHttpsCallable("getAllUserLogs")
                .call()
                .addOnSuccessListener((result) -> {
                    ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) result.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        List<UserLog> userLogsData = data.stream().map((logData) -> new UserLog(logData.get("email"), logData.get("datetime"), logData.get("activity"))
                        ).collect(Collectors.toList());
                        UserLogAdapter adapter = new UserLogAdapter(userLogsData);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    private void settings() {
        Dialog dialog = new Dialog(UserLogActivity.this);
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
                    verify.observe(UserLogActivity.this, verifyState -> {
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
                                Toast.makeText(UserLogActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(UserLogActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(UserLogActivity.this, MemberListActivity.class);
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
                                            Intent logoutIntent = new Intent(UserLogActivity.this, MainActivity.class);
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
                    startActivity(new Intent(UserLogActivity.this, UserLogActivity.class));
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
