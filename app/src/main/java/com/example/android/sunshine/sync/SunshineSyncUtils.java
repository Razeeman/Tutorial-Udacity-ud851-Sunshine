package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.sunshine.data.WeatherContract;

public class SunshineSyncUtils {

    private static boolean sInitialized = false;

    synchronized public static void initialize(final Context context) {
        // Initialisation performed once per app.
        if (sInitialized) return;

        sInitialized =true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String[] projection = {WeatherContract.WeatherEntry._ID};
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();
                Cursor cursor = context.getContentResolver().query(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        projection,
                        selection,
                        null,
                        null
                );

                // Sync if content provider is empty.
                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                cursor.close();
                return null;
            }

        }.execute();


    }

    /**
     * Hepler method to perform immediate sync.
     *
     * @param context Used to create intent and start service.
     */
    public static void startImmediateSync(Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
