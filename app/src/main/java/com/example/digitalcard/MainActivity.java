package com.example.digitalcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText etFullName;
    EditText etJobTitle;
    EditText etPhone;
    EditText etWebsite;
    Button btnCreateCard;
    Button btnFillSample;
    Button btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etFullName = findViewById(R.id.etFullName);
        etJobTitle = findViewById(R.id.etJobTitle);
        etPhone = findViewById(R.id.etPhone);
        etWebsite = findViewById(R.id.etWebsite);
        btnCreateCard = findViewById(R.id.btnCreateCard);
        btnFillSample = findViewById(R.id.btnFillSample);
        btnAbout = findViewById(R.id.btnAbout);

        btnCreateCard.setOnClickListener(v -> createCard());
        btnFillSample.setOnClickListener(v -> fillSample());
        btnAbout.setOnClickListener(v -> showAboutScreen());
    }

    public void fillSample() {
        etFullName.setText("Noam Druskin");
        etJobTitle.setText("Android Developer");
        etPhone.setText("050-1234567");
        etWebsite.setText("example.com");
    }

    public void showAboutScreen(){
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    public boolean errorCheck() {
        boolean isValid = true;

        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Full name is required");
            isValid = false;
        }

        if (etJobTitle.getText().toString().trim().isEmpty()) {
            etJobTitle.setError("Job title is required");
            isValid = false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            isValid = false;
        }

        if (etWebsite.getText().toString().trim().isEmpty()) {
            etWebsite.setError("Website is required");
            isValid = false;
        }

        return isValid;
    }

    public void createCard(){
        if (errorCheck()) {
            Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
            intent.putExtra("NAME", etFullName.getText().toString().trim());
            intent.putExtra("PROF", etJobTitle.getText().toString().trim());
            intent.putExtra("PHONE", etPhone.getText().toString().trim());
            intent.putExtra("WEBSITE", etWebsite.getText().toString().trim());
            startActivity(intent);
        }
    }
}