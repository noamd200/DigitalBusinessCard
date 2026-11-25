package com.example.digitalcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    EditText etFullName;
    EditText etJobTitle;
    EditText etPhone;
    EditText etWebsite;
    Button btnCreateCard;
    Button btnFillSample;
    Button btnAbout;

    Toolbar toolbar;

    private String editKey = null;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFullName = findViewById(R.id.etFullName);
        etJobTitle = findViewById(R.id.etJobTitle);
        etPhone = findViewById(R.id.etPhone);
        etWebsite = findViewById(R.id.etWebsite);
        btnCreateCard = findViewById(R.id.btnCreateCard);
        btnFillSample = findViewById(R.id.btnFillSample);
        btnAbout = findViewById(R.id.btnAbout);

        btnCreateCard.setOnClickListener(v -> saveCard());
        btnFillSample.setOnClickListener(v -> fillSample());
        btnAbout.setOnClickListener(v -> showAboutScreen());

        editKey = getIntent().getStringExtra("KEY");
        if (editKey != null) {
            Intent intent = getIntent();
            if (intent != null) {
                String name = intent.getStringExtra("NAME");
                String title = intent.getStringExtra("PROF");
                String phone = intent.getStringExtra("PHONE");
                String website = intent.getStringExtra("WEBSITE");

                // Fill the 4 fields
                etFullName.setText(name);
                etJobTitle.setText(title);
                etPhone.setText(phone);
                etWebsite.setText(website);

                // Update button and toolbar title
                btnCreateCard.setText("Save Changes");
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Card");
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_return).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);

        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_call).setVisible(false);
        menu.findItem(R.id.action_site).setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_list) {
            startActivity(new Intent(this, CardsListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void saveCard() {
        String name  = etFullName.getText().toString().trim();
        String job   = etJobTitle.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String site  = etWebsite.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "חובה למלא שם", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!FirebaseConfig.isConfigured()) {
            Toast.makeText(this, "חסרים ערכים ב-FirebaseConfig.java", Toast.LENGTH_LONG).show();
            return;
        }

        final Card card = new Card(name, job, phone, site);
        DatabaseReference ref = CardsDb.cards(this);

        if (editKey == null) {
            DatabaseReference newCardRef = ref.push();
            card.setKey(newCardRef.getKey()); // only if Card has setKey(String)

            newCardRef.setValue(card)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(MainActivity.this, "הכרטיס נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                        clearForm();
                        openCard(card);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );

        } else {
            card.setKey(editKey);
            ref.child(editKey).setValue(card)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(MainActivity.this, "Card updated", Toast.LENGTH_SHORT).show();
                        openCard(card); // Open ThirdActivity with the new values
                        finish();       // Close edit mode so Back goes to the list
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(MainActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }
    }

    private void openCard(Card c) {
        Intent intent = new Intent(this, ThirdActivity.class);
        intent.putExtra("KEY", c.getKey());
        intent.putExtra("NAME", c.getName());
        intent.putExtra("PROF", c.getJob());
        intent.putExtra("PHONE", c.getPhone());
        intent.putExtra("WEBSITE", c.getSite());
        startActivity(intent);
    }

    // מרוקן את ארבעת השדות, כדי שאפשר יהיה להקליד מיד כרטיס חדש
    private void clearForm() {
        etFullName.setText("");
        etJobTitle.setText("");
        etPhone.setText("");
        etWebsite.setText("");
        etFullName.requestFocus();
    }
}