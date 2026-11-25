package com.example.digitalcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    TextView tvCardName;
    TextView tvCardTitle;
    TextView tvCardPhone;
    TextView tvCardWebsite;

    Button btnCall;
    Button btnOpenWebsite;
    Button btnShare;
    Button btnBack;

    Toolbar toolbar;

    private String key, name, job, phone, site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvCardName = findViewById(R.id.tvCardName);
        tvCardTitle = findViewById(R.id.tvCardTitle);
        tvCardPhone = findViewById(R.id.tvCardPhone);
        tvCardWebsite = findViewById(R.id.tvCardWebsite);

        btnCall = findViewById(R.id.btnCall);
        btnOpenWebsite = findViewById(R.id.btnOpenWebsite);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        if (intent != null) {
            key = intent.getStringExtra("KEY");       // <-- Added this!
            name = intent.getStringExtra("NAME");     // <-- Removed 'String' so it saves to class field
            job = intent.getStringExtra("PROF");      // <-- Saved to 'job'
            phone = intent.getStringExtra("PHONE");   // <-- Removed 'String'
            site = intent.getStringExtra("WEBSITE");  // <-- Saved to 'site'

            tvCardName.setText(name);
            tvCardTitle.setText(job);
            tvCardPhone.setText(phone);
            tvCardWebsite.setText(site);
        }

        btnCall.setOnClickListener(v -> makePhoneCall());
        btnOpenWebsite.setOnClickListener(v -> openWebsite());
        btnShare.setOnClickListener(v -> shareCard());
        btnBack.setOnClickListener(v -> finish());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_list) {
            startActivity(new Intent(this, CardsListActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_return) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_edit) {
            EditCard();
            return true;
        }

        if (item.getItemId() == R.id.action_call) {
            makePhoneCall();
            return true;
        }
        if (item.getItemId() == R.id.action_site) {
            openWebsite();
            return true;
        }
        if (item.getItemId() == R.id.action_share) {
            shareCard();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void EditCard(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("KEY", key);
        intent.putExtra("NAME", name);
        intent.putExtra("PROF", job);
        intent.putExtra("PHONE", phone);
        intent.putExtra("WEBSITE", site);
        startActivity(intent);
        finish();
    }

    private void makePhoneCall() {
        String phoneNum = tvCardPhone.getText().toString();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNum));
        startActivity(callIntent);
    }

    private void openWebsite() {
        String url = tvCardWebsite.getText().toString();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(webIntent);
    }

    private void shareCard() {
        String cardDetails = "Business Card:\n" +
                "Name: " + tvCardName.getText().toString() + "\n" +
                "Title: " + tvCardTitle.getText().toString() + "\n" +
                "Phone: " + tvCardPhone.getText().toString() + "\n" +
                "Website: " + tvCardWebsite.getText().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, cardDetails);
        startActivity(Intent.createChooser(shareIntent, "Share Card Via"));
    }
}