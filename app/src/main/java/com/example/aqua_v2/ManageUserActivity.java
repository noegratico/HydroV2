package com.example.aqua_v2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ManageUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
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

    TextInputEditText name, email, password;
    MaterialButton verifyBtn, updateBtn, activeBtn;
    MaterialButton resetBtn;
    private Spinner spinner;
    boolean isVerify = true;

    private String sName = "";
    private String sEmail = "";
    private String sUserLevel = "";
    private String id = "";
    private boolean active = false;


    private String selectedUserLevel = "member";

    private String checkemail;
    private String checkname;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_manage_user);
        settingBtn = findViewById(R.id.settingBtn);
        settings();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
//        password = findViewById(R.id.passwordInput);
//        verifyBtn = findViewById(R.id.verifyBtn);
        updateBtn = findViewById(R.id.updateBtn);
        activeBtn = findViewById(R.id.activateUserBtn);
        resetBtn = findViewById(R.id.resetBtn);
        spinner = (Spinner) findViewById(R.id.adminTxt);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.user_level, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Bundle extras = getIntent().getExtras();
        sName = extras == null ? "" : changeNullToEmptyString(extras.getString("name"));
        sEmail = extras == null ? "" : changeNullToEmptyString(extras.getString("email"));
        sUserLevel = extras == null ? "" : changeNullToEmptyString(extras.getString("userLevel"));
        spinner.setSelection(Math.max(adapter.getPosition(StringUtils.capitalize(sUserLevel)), 0));

        id = extras == null ? "" : changeNullToEmptyString(extras.getString("uuid"));
        name.setText(sName);
        email.setText(sEmail);
//        verifyBtn.setEnabled(!isVerify);
        active = extras.getBoolean("active");

        changeActiveBtn(active);


        activeBtn.setOnClickListener(this);

        updateBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    private void changeActiveBtn(boolean active) {
        if (active) {
            activeBtn.setText("deactivate");
            activeBtn.setBackgroundColor(Color.parseColor("#ff0000"));
        } else {
            activeBtn.setText("activate");
            activeBtn.setBackgroundColor(Color.parseColor("#00ff00"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activateUserBtn) {
            Map<String, Object> data = new HashMap<>();
            data.put("disable", active);
            data.put("id", id);
            mFunctions
                    .getHttpsCallable("activationAndDeactivationOfUser")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        active = !active;
                        Toast.makeText(this, String.format("User %s!", active ? "Activated" : "Deactivated"), Toast.LENGTH_SHORT).show();
                        changeActiveBtn(active);
                        addUserLog(active ?"User "+ name.getText().toString()+" Activated": "User "+name.getText().toString()+" Deactivated");
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, String.format("Failed user %s!", !active ? "Activation" : "Deactivation"), Toast.LENGTH_SHORT).show();
                    });
        } else if (v.getId() == R.id.updateBtn) {
            Map<String, String> data = new HashMap<>();
            if (!sName.equals(name.getText().toString())) {
                data.put("name", name.getText().toString());
            }
            if (!sEmail.equals(email.getText().toString())) {
                data.put("email", email.getText().toString());
            }
            if (!sUserLevel.equals(selectedUserLevel)) {
                data.put("userLevel", selectedUserLevel);
            }

            if (data.entrySet().size() != 0 && !id.equals("")) {
                data.put("id", id);
                mFunctions
                        .getHttpsCallable("updateUser")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            Toast.makeText(this, "Account Successfully Updated", Toast.LENGTH_SHORT).show();
                            addUserLog("User Updated an Account");
                            startActivity(new Intent(ManageUserActivity.this, MemberListActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to Update User", Toast.LENGTH_SHORT).show();
                        });
            }
        } else if (v.getId() == R.id.resetBtn) {
            mAuth.sendPasswordResetEmail(sEmail).addOnSuccessListener((result) -> {
                addUserLog("User Send a Reset Password Email");
                Toast.makeText(this, "Email for Reset Password has been Sent", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener((e) -> {
                Log.e("Error", e.getMessage(), e);
            });
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUserLevel = ((String) spinner.getItemAtPosition(position)).toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String changeNullToEmptyString(String value) {
        return value == null ? "" : value;
    }

    private void settings() {
        Dialog dialog = new Dialog(ManageUserActivity.this);
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
                    verify.observe(ManageUserActivity.this, verifyState -> {
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

                                checkemail = (String) data.get("email");
                                checkname = (String) data.get("name");
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
                                Toast.makeText(ManageUserActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
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
                                    if (editName.getText().toString() != null && editName.getText().toString() != checkname) {
                                        data.put("name", editName.getText().toString());
                                    }
                                    if (editEmail.getText().toString() != null && editEmail.getText().toString() != checkemail) {
                                        data.put("email", editEmail.getText().toString());
                                    }
                                    mFunctions
                                            .getHttpsCallable("updateUserInfo")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                addUserLog("User " + editName.getText().toString() + " Profile Updated");
                                                Toast.makeText(ManageUserActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(ManageUserActivity.this, MemberListActivity.class);
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
                                            Intent logoutIntent = new Intent(ManageUserActivity.this, MainActivity.class);
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
                    startActivity(new Intent(ManageUserActivity.this, UserLogActivity.class));
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        Map<String, String> data = new HashMap<>();
        data.put("activity", userActivity);
        data.put("datetime", currentDateAndTime);
        mFunctions
                .getHttpsCallable("logUserActivity")
                .call(data);
    }



}
