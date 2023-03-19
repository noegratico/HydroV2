package com.example.aqua_v2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ManageUserActivity extends AppCompatActivity {

    TextInputEditText name, email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.passwordInput);
        String id = "", sName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sName = extras.getString("name");
            id = extras.getString("id");
        }
        name.setText(sName);
        email.setText(id);
        Spinner spinner = (Spinner) findViewById(R.id.adminTxt);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.user_level, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }
}
