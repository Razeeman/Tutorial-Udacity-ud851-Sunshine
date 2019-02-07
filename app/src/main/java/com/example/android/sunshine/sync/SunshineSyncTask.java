package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    /**
     * Performs a network request for weather, parses json response and inserts new data into
     * ContentProvider.
     *
     * @param context Used to access utility methods.
     */
    synchronized public static void syncWeather(Context context) {
        try {
            URL queryUrl = NetworkUtils.getUrl(context);
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(queryUrl);

            ContentValues[] contentValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonResponse);

            if (contentValues != null && contentValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                contentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        contentValues);

                boolean notificationsEnabled = SunshinePreferences
                        .areNotificationsEnabled(context);
                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);

                if (notificationsEnabled && timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
