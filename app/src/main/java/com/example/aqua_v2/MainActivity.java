package com.example.aqua_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    MaterialButton loginBtn;
    SharedPreferences sharedPreferences;
    boolean nightMode;
    TextInputEditText username, password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-southeast1");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id;

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
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.login_btn);
        username = findViewById(R.id.usernameInputTxt);
        password = findViewById(R.id.passwordInputTxt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(username.getText()).toString(), Objects.requireNonNull(password.getText()).toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    id = mAuth.getUid();

                                    db.collection("users").document(id)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    boolean isLogin;
                                                    if(documentSnapshot.getBoolean("isLogin")!= null){
                                                        isLogin = (boolean) documentSnapshot.getBoolean("isLogin");
                                                    }else{
                                                        isLogin = false;
                                                    }
                                                    String name = documentSnapshot.getString("name");
                                                    if (!isLogin) {
                                                        db.collection("users").document(id)
                                                                .update("isLogin", true)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });
                                                       HashMap<String, Object> data = new HashMap<>();
                                                        data.put("activity", "User Sign In");
                                                        data.put("datetime", currentDateAndTime);
                                                        mFunctions
                                                                .getHttpsCallable("logUserActivity")
                                                                .call(data);
                                                       startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                                        finish();
                                                    } else {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                                        builder.setMessage("Account already Log in");

                                                        builder.setTitle("Alert");

                                                        builder.setCancelable(false);

                                                        builder.setNegativeButton("Understood", (DialogInterface.OnClickListener) (dialog, which) -> {
                                                            dialog.cancel();
                                                        });

                                                        // Create the Alert dialog
                                                        AlertDialog alertDialog = builder.create();
                                                        // Show the Alert Dialog box
                                                        alertDialog.show();
                                                        mAuth.signOut();

                                                    }
                                                }
                                            });


//                                    Map<String, String> data = new HashMap<>();
//                                    data.put("activity", "User Sign In");
//                                    data.put("datetime", currentDateAndTime);
//                                    mFunctions
//                                            .getHttpsCallable("logUserActivity")
//                                            .call(data);
//                                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
//                                    finish();


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(this, DashboardActivity.class));
//            finish();
//        }
    }
}