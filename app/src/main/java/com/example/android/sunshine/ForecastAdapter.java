package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

/**
 * Adapter for weather RecyclerView.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    // OnClick handles to interact with activity.
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * Interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(long weatherDate);
    }

    /**
     * Create ForecastAdapter.
     *
     * @param context        Used to work with UI and resources.
     * @param onClickHandler OnClick handler that will be called when item is clicked.
     */
    ForecastAdapter(Context context, ForecastAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    /**
     * Gets called every time Adapter creates new views when RecyclerView is getting populated.
     * After that new ViewHolders are not created but recycled.
     *
     * @param viewGroup parent view of created ViewHolders.
     * @param viewType  view type if views have different types.
     * @return          new ViewHolder that holds the view.
     */
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Inflating list item layout.
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.forecast_list_item, viewGroup, false);

        return new ForecastAdapterViewHolder(view);
    }

    /**
     * Called when Adapter recycles a view.
     *
     * @param forecastAdapterViewHolder ViewHolder that is to be recycled.
     * @param position                  position of the view.
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);

            long date = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            String dateString = SunshineDateUtils.getFriendlyDateString(mContext, date, false);

            int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
            String descriptionString = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);

            double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
            double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
            String temperaturesString =
                    SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);

            String weatherSummary = dateString + " - " + descriptionString + " - " + temperaturesString;
            forecastAdapterViewHolder.mWeatherTextView.setText(weatherSummary);
        }
    }

    /**
     * Returns number of views to display.
     *
     * @return number of views in RecyclerView.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Used to set weather date onto the Adapter.
     *
     * @param cursor Cursor with new data to be displayed.
     */
    void setWeatherCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder to cache view in Adapter
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mWeatherTextView;

        /**
         * Creates new ViewHolder.
         *
         * @param itemView View that will be used bu this ViewHolder.
         */
        ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        /**
         * Gets called after view is clicked.
         *
         * @param v View that was clicked.
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            long date = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(date);
        }
    }
}
