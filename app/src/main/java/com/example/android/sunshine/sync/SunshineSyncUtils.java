package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;

public class SunshineSyncUtils {

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
