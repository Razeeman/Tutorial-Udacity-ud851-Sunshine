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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Getting user location and querying for weather data
     */
    private void loadWeatherData() {
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new WeatherAsyncTask().execute(location);
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
        startActivity(intent);
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];
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
        protected void onPostExecute(String[] weatherData) {
            mProgressBar.setVisibility(View.INVISIBLE);

            if (weatherData != null) {
                showWeatherDataView();
                // Update RecyclerView with new data.
                mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }
}