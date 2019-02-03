/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.sunshine.ForecastAdapter.ForecastAdapterOnClickHandler;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.FakeDataUtils;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int LOADER_ID = 22;

    // Column names what are needed for our purposes.
    private static final String[] COLUMN_NAMES = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    // Indexes of the COLUMN_NAMES for more convenient access.
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        FakeDataUtils.insertFakeData(this);

        mRecyclerView = findViewById(R.id.rv_forecast);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Improve performance, but child count can't be changed.
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mProgressBar = findViewById(R.id.pb_loading_data);

        showLoading();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            // Reset RecyclerView before querying for new data.
            showLoading();
            mForecastAdapter.setWeatherCursor(null);
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            return true;
        } else if (itemId == R.id.action_map) {
            showMap();
            return true;
        } else if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will fire off an implicit Intent to view a location on a map.
     */
    private void showMap() {
        String addressString = SunshinePreferences.getPreferredWeatherLocation(this);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo").appendQueryParameter("q", addressString);
        Uri addressUri = builder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(addressUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + addressUri.toString()
                    + ", no receiving apps installed!");
        }
    }

    /**
     * Shows weather data and hides loading progress bar.
     */
    private void showWeatherDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows loading progress bar and hides data.
     */
    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Handler for clicks on views in RecyclerVIew.
     *
     * @param weatherDate data for the the view that was clicked.
     */
    @Override
    public void onClick(long weatherDate) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Uri queryUri = WeatherContract.WeatherEntry.buildWeatherUriWithDate(weatherDate);
        intent.setData(queryUri);
        startActivity(intent);
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
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable final Bundle bundle) {
        switch (loaderId) {
            case LOADER_ID: {
                // All rows of weather.
                Uri queryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                // Sort by date.
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(
                        this,
                        queryUri,
                        COLUMN_NAMES,
                        selection,
                        null,
                        sortOrder);
            }

            default:
                throw new RuntimeException("This loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor weatherCursor) {
        mForecastAdapter.setWeatherCursor(weatherCursor);

        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerView.smoothScrollToPosition(mPosition);

        if (weatherCursor.getCount() > 0) {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mForecastAdapter.setWeatherCursor(null);
    }
}