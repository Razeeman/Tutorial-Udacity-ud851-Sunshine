package com.example.android.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.sunshine.utilities.SunshineDateUtils;

public class WeatherContract {

    // Name of the content provider.
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine";

    // Base uri for all contacts with content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths from base uri.
    public static final String PATH_WEATHER = "weather";

    // Defines the structure of weather table
    public static final class WeatherEntry implements BaseColumns {
        // The base CONTENT_URI used to query the Weather table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        // Name of the table.
        public static final String TABLE_NAME = "weather";

        // UTC date.
        public static final String COLUMN_DATE = "date";

        // Weather Id.
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Min and max temperature for the day in celsius degrees, stored as a float.
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity for the day in percentage, stored as a float.
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure for the day in percentage, stored as a float.
        public static final String COLUMN_PRESSURE = "pressure";

        // Wind speed for the day, stored as a float.
        public static final String COLUMN_WIND_SPEED = "wind";

        // Meteorological degrees (0 is north, 180 is south), stored as a float.
        public static final String COLUMN_DEGREES = "degrees";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date.
         *
         * @param date Normalized date in milliseconds.
         * @return     Uri to query details about a single weather entry.
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}
