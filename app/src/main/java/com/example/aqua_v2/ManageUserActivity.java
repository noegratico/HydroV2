package com.example.aqua_v2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.functions.FirebaseFunctions;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ManageUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    TextInputEditText name, email, password;
    MaterialButton verifyBtn, updateBtn, activeBtn;
    private Spinner spinner;
    boolean isVerify = true;

    private String sName = "";
    private String sEmail = "";
    private String sUserLevel = "";
    private String id = "";
    private boolean active = false;


    private String selectedUserLevel = "member";

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.passwordInput);
        verifyBtn = findViewById(R.id.verifyBtn);
        updateBtn = findViewById(R.id.updateBtn);
        activeBtn = findViewById(R.id.activateUserBtn);
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
        verifyBtn.setEnabled(!isVerify);
        active = extras.getBoolean("active");

        changeActiveBtn(active);


        activeBtn.setOnClickListener(this);

        updateBtn.setOnClickListener(this);
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
        if(v.getId() == R.id.activateUserBtn) {
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
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, String.format("Failed user %s!", !active ? "Activation" : "Deactivation"), Toast.LENGTH_SHORT).show();
                    });
        } else if(v.getId() == R.id.updateBtn) {
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
            if (!password.getText().toString().equals("")) {
                data.put("password", password.getText().toString());
            }

            if (data.entrySet().size() != 0 && !id.equals("")) {
                data.put("id", id);
                mFunctions
                        .getHttpsCallable("updateUser")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            startActivity(new Intent(ManageUserActivity.this, MemberListActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to Update User", Toast.LENGTH_SHORT).show();
                        });
            }
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
}
