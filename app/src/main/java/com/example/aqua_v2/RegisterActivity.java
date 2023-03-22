package com.example.aqua_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    MaterialButton registerBtn;
    TextInputEditText name, email, password;
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private String selectedUserLevel = "member";
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBtn = findViewById(R.id.register_btn);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.passwordInput);
        spinner = (Spinner) findViewById(R.id.adminTxt);
        mFunctions = FirebaseFunctions.getInstance();
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.user_level, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //sign up
       /* mAuth = FirebaseAuth.getInstance();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(Objects.requireNonNull(email.getText()).toString(), Objects.requireNonNull(password.getText()).toString())
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(RegisterActivity.this, MemberListActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });*/
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(email.getText().toString(), name.getText().toString(), password.getText().toString(), selectedUserLevel);

            }
        });


    }

    private void signUp(String email, String name, String password, String userLevel) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("password", password);
        data.put("userLevel", userLevel);
        data.put("push", true);
        mFunctions
                .getHttpsCallable("signUp")
                .call(data)
                .addOnSuccessListener(result -> {
                    startActivity(new Intent(RegisterActivity.this, MemberListActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add User", Toast.LENGTH_SHORT).show();
                });
                /*.continueWith(new Continuation<HttpsCallableResult, User>() {
                    @Override
                    public User then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        User result = (User) task.getResult().getData();
                        startActivity(new Intent(RegisterActivity.this, MemberListActivity.class));
                        finish();
                        return result;
                    }
                });*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUserLevel = ((String) spinner.getItemAtPosition(position)).toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}