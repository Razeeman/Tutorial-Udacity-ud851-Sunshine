package com.example.android.sunshine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter for weather RecyclerView.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;

    // OnClick handles to interact with activity.
    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * Interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weather);
    }

    /**
     * Create ForecastAdapter.
     *
     * @param onClickHandler OnClick handler that will be called when item is clicked.
     */
    ForecastAdapter(ForecastAdapterOnClickHandler onClickHandler) {
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
        forecastAdapterViewHolder.mWeatherTextView.setText(String.valueOf(position));
    }

    /**
     * Returns number of views to display.
     *
     * @return number of views in RecyclerView.
     */
    @Override
    public int getItemCount() {
        if (mWeatherData == null) {
            return 0;
        } else {
            return mWeatherData.length;
        }
    }

    /**
     * Used to set weather onto the Adapter.
     *
     * @param weatherData new data to be displayed.
     */
    void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
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
            mClickHandler.onClick((String) mWeatherTextView.getText());
        }
    }
}