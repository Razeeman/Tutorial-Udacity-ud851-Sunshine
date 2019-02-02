package com.example.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    /** Name of the database. */
    private static final String DATABASE_NAME = "weather.db";

    /** Version number of the database. Incremented after each upgrade. */
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherEntry.COLUMN_DATE + " TEXT, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, " +
                WeatherEntry.COLUMN_MIN_TEMP + " REAL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL, " +
                WeatherEntry.COLUMN_HUMIDITY + " REAL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL " + ");";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not used yet.
    }
}
