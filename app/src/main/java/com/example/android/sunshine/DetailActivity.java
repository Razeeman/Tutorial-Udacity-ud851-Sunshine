package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 44;

    // Column names what are needed for our purposes.
    private static final String[] COLUMN_NAMES = new String[] {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };
    // Indexes of the COLUMN_NAMES for more convenient access.
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    private String mWeatherSummary;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mHighTemperatureTextView;
    private TextView mLowTemperatureTextView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDateTextView = findViewById(R.id.tv_date);
        mDescriptionTextView = findViewById(R.id.tv_description);
        mHighTemperatureTextView = findViewById(R.id.tv_high_temperature);
        mLowTemperatureTextView = findViewById(R.id.tv_low_temperature);
        mHumidityTextView = findViewById(R.id.tv_humidity);
        mWindTextView = findViewById(R.id.tv_wind);
        mPressureTextView = findViewById(R.id.tv_pressure);

        mUri = getIntent().getData();

        if(mUri == null) {
            throw new NullPointerException();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
        } else if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
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
                .setText(mWeatherSummary)
                .startChooser();
    }

    /**
     * Called by the LoaderManager when a new Loader needs to be created.
     *
     * @param loaderId The loader ID for which we need to create a loader.
     * @param bundle   Any arguments supplied by the caller.
     * @return         A new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        switch (loaderId) {
            case LOADER_ID: {
                return new CursorLoader(
                        this,
                        mUri,
                        COLUMN_NAMES,
                        null,
                        null,
                        null);
            }

            default:
                throw new RuntimeException("This loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) return;

        long date = cursor.getLong(INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(this, date, true);
        mDateTextView.setText(dateString);

        int weatherId = cursor.getInt(INDEX_WEATHER_CONDITION_ID);
        String weatherIdString = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
        mDescriptionTextView.setText(weatherIdString);

        double highTemp = cursor.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highTempString = SunshineWeatherUtils.formatTemperature(this, highTemp);
        mHighTemperatureTextView.setText(highTempString);

        double lowTemp = cursor.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowTempString = SunshineWeatherUtils.formatTemperature(this, lowTemp);
        mLowTemperatureTextView.setText(lowTempString);

        float humidity = cursor.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        mHumidityTextView.setText(humidityString);

        float windSpeed = cursor.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = cursor.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        mWindTextView.setText(windString);

        float pressure = cursor.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        mPressureTextView.setText(pressureString);

        mWeatherSummary = String.format("%s - %s - %s/%s",
                dateString, weatherIdString, highTempString, lowTempString);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Not used at the moment.
    }
}
