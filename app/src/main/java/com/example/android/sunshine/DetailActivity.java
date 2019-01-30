package com.example.android.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mWeatherDetailsTextView;
    private String weatherDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDetailsTextView = findViewById(R.id.tv_weather_details);

        Intent parentIntent = getIntent();

        if (parentIntent != null && parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
            weatherDetails = parentIntent.getStringExtra(Intent.EXTRA_TEXT);
            mWeatherDetailsTextView.setText(weatherDetails);
        }
    }
}
