package com.example.aqua_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;

import com.example.aqua_v2.model.User;
import com.example.aqua_v2.model.UserList;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MemberListActivity extends AppCompatActivity {

    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseFunctions mFunctions;

    ImageButton settingBtn, closeBtn, editInfo, activateBtn, deactivateBtn;
    MaterialButton cancelBtn, changePassword, editProfile, logout;
    FloatingActionButton addUser;
    RecyclerView recyclerView;
    private recyclerAdapter.RecycleViewClickListener listener;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingScreen();
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
        setContentView(R.layout.activity_member_list);

        settingBtn = findViewById(R.id.settingBtn);
        recyclerView = findViewById(R.id.recycleView);
        addUser = findViewById(R.id.floatingActionButton);
        mFunctions = FirebaseFunctions.getInstance();
        Dialog dialog = new Dialog(MemberListActivity.this);

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
                    Intent intent = new Intent(MemberListActivity.this, MemberListActivity.class);
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
                            Intent logoutIntent = new Intent(MemberListActivity.this, MainActivity.class);
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

//        editInfo = findViewById(R.id.editInfo);
//        editInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.setContentView(R.layout.activity_profile);
//                dialog.setCancelable(false);
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                closeBtn = dialog.findViewById(R.id.closeBtn);
//                changePassword = dialog.findViewById(R.id.changePassword);
//                editProfile = dialog.findViewById(R.id.editProfile);
//
//
//                dialog.show();
////close button
//                closeBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                //change password
//                changePassword.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.setContentView(R.layout.activity_change_password);
//                        dialog.setCancelable(false);
//                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                        closeBtn = dialog.findViewById(R.id.closeBtn);
//                        dialog.show();
//                        closeBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                    }
//                });
////                    editProfile button
//                editProfile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.setContentView(R.layout.activity_edit_profile);
//                        dialog.setCancelable(false);
//                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                        closeBtn = dialog.findViewById(R.id.closeBtn);
//                        dialog.show();
//                        closeBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                    }
//                });
//            }
//        });

//        activateBtn = findViewById(R.id.activateBtn);
//        deactivateBtn = findViewById(R.id.deactivateBtn);

//        activateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.setContentView(R.layout.activity_account_status);
//                dialog.setCancelable(false);
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                closeBtn = dialog.findViewById(R.id.closeBtn);
//                dialog.show();
//                closeBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//
//        });

//        deactivateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.setContentView(R.layout.activity_account_status);
//                dialog.setCancelable(false);
//                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                closeBtn = dialog.findViewById(R.id.closeBtn);
//                dialog.show();
//                closeBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//        });
//      add user
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemberListActivity.this, RegisterActivity.class));
                finish();
            }
        });
        setAdapter();
    }

    private void loadingScreen() {
        final Dialog dialog = new Dialog(MemberListActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.splashAnimation;
        dialog.setContentView(R.layout.activity_splash_screen);
        dialog.setCancelable(true);
        dialog.show();

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    dialog.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setAdapter() {
        mFunctions
                .getHttpsCallable("listUsers")
                .call()
                .addOnSuccessListener(result -> {
                    HashMap<String, ArrayList<HashMap<String, Object>>> data = (HashMap<String, ArrayList<HashMap<String, Object>>>) result.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        userList = data.get("users").stream().map(userRecord -> {
                            User user = new User((String)userRecord.get("id"), (String)userRecord.get("name"));
                            user.setEmail((String)userRecord.get("email"));
                            user.setUserLevel((String)userRecord.get("userLevel"));
                            return user;
                        }).collect(Collectors.toList());
                    }
                    recyclerAdapter adapter = new recyclerAdapter((ArrayList<User>) userList, listener);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                });
//                .continueWith(new Continuation<HttpsCallableResult, String>() {
//                    @Override
//                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                       // String result = (String) task.getResult().getData();
//                        userList = result.getUserList();
//                        recyclerAdapter adapter = new recyclerAdapter((ArrayList<User>) userList, listener);
//                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//                        recyclerView.setLayoutManager(layoutManager);
//                        recyclerView.setItemAnimator(new DefaultItemAnimator());
//                        recyclerView.setAdapter(adapter);
                    //    return result;
//                    }
//                }).addOnCompleteListener(task -> {
//                   task.getException().printStackTrace();
//                });

        setOnClickListener();
    }

    private void setOnClickListener() {
        listener = new recyclerAdapter.RecycleViewClickListener() {
            @Override
            public void onCLick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), ManageUserActivity.class);
                intent.putExtra("name" , userList.get(position).getName());
                intent.putExtra("uuid", userList.get(position).getId());
                intent.putExtra("email", userList.get(position). getEmail());
                intent.putExtra("userLevel",userList.get(position).getUserLevel());
                startActivity(intent);
            }
        };
    }


    private void setUserInfo() {

        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));
        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));
        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));
        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));
        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));
        userList.add(new User("1", "Lina"));
        userList.add(new User("2", "Windranger"));
        userList.add(new User("3", "Sniper"));
        userList.add(new User("4", "Tinker"));

    }
}