package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

/**
 * Adapter for weather RecyclerView.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private final Context mContext;

    // OnClick handles to interact with activity.
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * Interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(long weatherDate);
    }

    private Cursor mCursor;
    private boolean mUseTodayLayout;

    /**
     * Create ForecastAdapter.
     *
     * @param context        Used to work with UI and resources.
     * @param onClickHandler OnClick handler that will be called when item is clicked.
     */
    ForecastAdapter(Context context, ForecastAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
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
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layoutId, viewGroup, false);

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

            // Weather date.
            long date = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            String dateString = SunshineDateUtils
                    .getFriendlyDateString(mContext, date, false);
            forecastAdapterViewHolder.mWeatherDateTextView.setText(dateString);

            // Weather description.
            int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
            String descriptionString = SunshineWeatherUtils
                    .getStringForWeatherCondition(mContext, weatherId);
            forecastAdapterViewHolder.mWeatherStatusTextView.setText(descriptionString);

            // Weather icon.
            int weatherImageId;
            int viewType = getItemViewType(position);

            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    weatherImageId = SunshineWeatherUtils
                            .getLargeArtResourceIdForWeatherCondition(weatherId);
                    break;
                }

                case VIEW_TYPE_FUTURE_DAY: {
                    weatherImageId = SunshineWeatherUtils
                            .getSmallArtResourceIdForWeatherCondition(weatherId);;
                    break;
                }

                default:
                    throw new IllegalArgumentException("Invalid view type: " + viewType);
            }

            forecastAdapterViewHolder.mWeatherIconImageView.setImageResource(weatherImageId);

            // Weather high temperature.
            double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
            String highTempString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
            forecastAdapterViewHolder.mWeatherHighTempTextView.setText(highTempString);

            // Weather low temperature.
            double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
            String lowTempString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
            forecastAdapterViewHolder.mWeatherLowTempTextView.setText(lowTempString);
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
     * Used to determine type of the view to display in position.
     *
     * @param position Position on the view to be displayed.
     * @return         THe view type.
     */
    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
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

        final TextView mWeatherDateTextView;
        final TextView mWeatherStatusTextView;
        final TextView mWeatherHighTempTextView;
        final TextView mWeatherLowTempTextView;
        final ImageView mWeatherIconImageView;

        /**
         * Creates new ViewHolder.
         *
         * @param itemView View that will be used bu this ViewHolder.
         */
        ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mWeatherIconImageView = itemView.findViewById(R.id.iv_weather_icon);
            mWeatherDateTextView = itemView.findViewById(R.id.tv_weather_date);
            mWeatherStatusTextView = itemView.findViewById(R.id.tv_weather_status);
            mWeatherHighTempTextView = itemView.findViewById(R.id.tv_weather_high_temp);
            mWeatherLowTempTextView = itemView.findViewById(R.id.tv_weather_low_temp);

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
