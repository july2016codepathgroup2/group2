package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseGeoPoint;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.api_clients.ZipCodeApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FilterSearchDialogFragment extends android.support.v4.app.DialogFragment {

    private EditText etType;
    private EditText etBudget;
    private EditText etLocation;
    private Button btnSaveFilters;

    public FilterSearchDialogFragment() {

    }

    public static FilterSearchDialogFragment newInstance() {
        FilterSearchDialogFragment fragment = new FilterSearchDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_search, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etType = (EditText) view.findViewById(R.id.etType);
        etBudget = (EditText) view.findViewById(R.id.etBudget);
        etLocation = (EditText) view.findViewById(R.id.etLocation);
        btnSaveFilters = (Button) view.findViewById(R.id.btnSaveFilters);

        btnSaveFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBackFilters();
            }
        });
    }

    public void sendBackFilters() {
        String type = "";
        ParseGeoPoint location;
        double budget = -1;

        final FilterSearchDialogListener listener = (FilterSearchDialogListener) getTargetFragment();

        if (!etType.getText().toString().isEmpty()) {
            type = etType.getText().toString();
        }
        if (!etBudget.getText().toString().isEmpty()) {
            budget = Double.parseDouble(etBudget.getText().toString());
        }
        if (!etLocation.getText().toString().isEmpty()) {
            ZipCodeApiClient client = new ZipCodeApiClient();
            final String finalType = type;
            final double finalBudget = budget;
            client.getLocationForZip(etLocation.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    try {
                        ParseGeoPoint location = new ParseGeoPoint();
                        location.setLatitude(responseBody.getDouble("lat"));
                        location.setLongitude(responseBody.getDouble("lng"));
                        listener.onFinishFilterSearchDialog(finalType, location, finalBudget);
                        dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            listener.onFinishFilterSearchDialog(type, null, budget);
            dismiss();
        }
    }

    public interface FilterSearchDialogListener {
        void onFinishFilterSearchDialog(String type, ParseGeoPoint location, double budget);
    }
}
