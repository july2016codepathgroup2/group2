package com.pensum.pensumapplication.adapters;

/**
 * Created by violetaria on 8/18/16.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.pensum.pensumapplication.R;

/**
 * Created by violetaria on 8/17/16.
 */
public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflator;

    public CustomWindowAdapter(LayoutInflater i) {
        inflator = i;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = inflator.inflate(R.layout.custom_info_window, null);

        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        title.setText(marker.getTitle());

        TextView description = (TextView) view.findViewById(R.id.tvDescription);
        description.setText(marker.getSnippet());

        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
