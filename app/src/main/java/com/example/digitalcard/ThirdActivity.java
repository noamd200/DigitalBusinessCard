package com.example.digitalcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);

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
            String name = intent.getStringExtra("NAME");
            String title = intent.getStringExtra("PROF");
            String phone = intent.getStringExtra("PHONE");
            String website = intent.getStringExtra("WEBSITE");

            tvCardName.setText(name);
            tvCardTitle.setText(title);
            tvCardPhone.setText(phone);
            tvCardWebsite.setText(website);
        }

        btnCall.setOnClickListener(v -> makePhoneCall());
        btnOpenWebsite.setOnClickListener(v -> openWebsite());
        btnShare.setOnClickListener(v -> shareCard());
        btnBack.setOnClickListener(v -> finish());
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