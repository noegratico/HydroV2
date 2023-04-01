package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class WaterConditionActivity extends AppCompatActivity {
    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageButton settingBtn, closeBtn;
    MaterialButton cancelBtn, changePassword, editProfile, logout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
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
        settingBtn = findViewById(R.id.settingBtn);
        showSetting();

    }
    private void showSetting() {
        PopupMenu popupMenu = new PopupMenu(this, settingBtn);
        Dialog dialog = new Dialog(WaterConditionActivity.this);
        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult result) {
                    boolean isAdmin = result.getClaims().containsKey("admin") && (boolean) result.getClaims().get("admin");
                    if (isAdmin) {
                        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Member");
                        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Logout");
                    } else {
                        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Profile");
                        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Theme");
                        popupMenu.getMenu().add(Menu.NONE, 3, 2, "Logout");
                    }
                }
            });
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
//                        changePassword = dialog.findViewById(R.id.changePassword);
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
                                FirebaseAuth.getInstance().signOut();
                                Intent logoutIntent = new Intent(WaterConditionActivity.this, MainActivity.class);
                                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        }
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
    }
}