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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.ForecastAdapter.ForecastAdapterOnClickHandler;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int LOADER_ID = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

    private static boolean sPreferenceUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView = findViewById(R.id.rv_forecast);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        // Improve performance, but child count can't be changed.
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mProgressBar = findViewById(R.id.pb_loading_data);

        loadWeatherData();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if preferences were changes - update the data
        if (sPreferenceUpdated) {
            loadWeatherData();
            sPreferenceUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
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
     * Getting user location and querying for weather data
     */
    private void loadWeatherData() {
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, location);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> searchLoader = loaderManager.getLoader(LOADER_ID);
        if (searchLoader == null) {
            loaderManager.initLoader(LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, queryBundle, this);
        }
    }

    /**
     * Showing weather data and hiding error message
     */
    private void showWeatherDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Showing error message and hiding weather data
     */
    private void showErrorMessage () {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Handler for clicks on views in RecyclerVIew.
     *
     * @param weather data for the the view that was clicked.
     */
    @Override
    public void onClick(String weather) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weather);
        startActivity(intent);
    }

    // Warning suppressed because tutorial specifically use AsyncTaskLoader.
    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherDataCache;

            @Override
            protected void onStartLoading() {
                if (bundle == null) {
                    return;
                }
                if (mWeatherDataCache != null) {
                    deliverResult(mWeatherDataCache);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                if (bundle == null) {
                    return null;
                }
                String location = bundle.getString(SEARCH_QUERY_URL_EXTRA);
                if (location == null || location.isEmpty()) {
                    return null;
                }

                URL url = NetworkUtils.buildUrl(location);
                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(url);
                    String[] weatherResponse = OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonResponse);
                    return weatherResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mWeatherDataCache = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] weatherData) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (weatherData != null) {
            showWeatherDataView();
            // Update RecyclerView with new data.
            mForecastAdapter.setWeatherData(weatherData);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {
        // Not used.
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Set the flag if preferences were changed.
        sPreferenceUpdated = true;
    }
}