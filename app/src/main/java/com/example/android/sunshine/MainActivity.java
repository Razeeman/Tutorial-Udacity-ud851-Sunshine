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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mWeatherDisplayTextView = findViewById(R.id.tv_weather_data);

        String[] fakeWeatherData = {
                "Today, Cold, -11°C",
                "Tomorrow, Cold, -15°C",
                "Tuesday, Cold, -7°C",
                "Wednesday, Cold, -12°C",
                "Thursday, Freezing, -17°C",
                "Friday, Freezing, -21°C",
                "Saturday, Stay at Home, -25°C",
                "Feb 3, Freezing, -14°C",
                "Feb 4, Cold, -9°C",
                "Feb 5, Cold, -5°C",
        };

        for (String weatherDay: fakeWeatherData) {
            mWeatherDisplayTextView.append(weatherDay + "\n\n\n");
        }
    }
}