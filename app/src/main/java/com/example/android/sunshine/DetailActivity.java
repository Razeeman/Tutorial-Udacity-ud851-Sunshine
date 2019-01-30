package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mWeatherDetailsTextView;
    private String mWeatherDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDetailsTextView = findViewById(R.id.tv_weather_details);

        Intent parentIntent = getIntent();

        if (parentIntent != null && parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
            mWeatherDetails = parentIntent.getStringExtra(Intent.EXTRA_TEXT);
            mWeatherDetailsTextView.setText(mWeatherDetails);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_share) {
            shareWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method shares text and allows the user to select which app they would like to use to
     * share the text. The chooser builder will create a chooser when more than one app on the
     * device can handle the Intent. If only one Activity on the phone can handle the Intent,
     * it will automatically be launched.
     */
    private void shareWeather() {
        ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setChooserTitle("Sharing weather details")
                .setText(mWeatherDetails)
                .startChooser();
    }
}
