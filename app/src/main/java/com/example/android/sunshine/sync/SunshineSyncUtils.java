package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.android.sunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class SunshineSyncUtils {

    // Interval at which to sync with the weather.
    private static final int SYNC_INTERVAL_SECONDS = 3 * 60 * 60; // 3 hours.
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized = false;

    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    /**
     * Schedules a periodic weather sync.
     *
     * @param context Used for utility classes.
     */
    static void scheduleFirebaseJobDispatcherSync(Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job job = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(job);
    }

    /**
     * Sets up first weather sync and periodic syncs.
     *
     * @param context Used for utility classes.
     */
    synchronized public static void initialize(final Context context) {
        // Initialisation performed once per app.
        if (sInitialized) return;

        sInitialized =true;

        scheduleFirebaseJobDispatcherSync(context);

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
