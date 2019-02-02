package com.example.android.sunshine.data;

import android.provider.BaseColumns;

public class WeatherContract {

    /** Defines the structure of weather table */
    public static final class WeatherEntry implements BaseColumns {
        /** Name of the table */
        public static final String TABLE_NAME = "weather";

        /** UTC date */
        public static final String COLUMN_DATE = "date";

        /** Weather Id */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        /** Min and max temperature for the day in celsius degrees, stored as a float */
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        /** Humidity for the day in percentage, stored as a float */
        public static final String COLUMN_HUMIDITY = "humidity";

        /** Pressure for the day in percentage, stored as a float */
        public static final String COLUMN_PRESSURE = "pressure";

        /** Wind speed for the day, stored as a float */
        public static final String COLUMN_WIND_SPEED = "wind";

        /** Meteorological degrees (0 is north, 180 is south), stored as a float */
        public static final String COLUMN_DEGREES = "degrees";
    }
}
