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

    public static FilterSearchDialogFragment newInstance(String type, double budget, String location) {
        FilterSearchDialogFragment fragment = new FilterSearchDialogFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putDouble("budget", budget);
        args.putString("location", location);
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
        String type = getArguments().getString("type");
        etType.setText(type);

        etBudget = (EditText) view.findViewById(R.id.etBudget);
        Double budget = getArguments().getDouble("budget");
        if (budget > 0) {
            etBudget.setText(Double.toString(budget));
        }

        etLocation = (EditText) view.findViewById(R.id.etLocation);
        String location = getArguments().getString("location");
        etLocation.setText(location);

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
        double budget = -1;

        final FilterSearchDialogListener listener = (FilterSearchDialogListener) getTargetFragment();

        if (!etType.getText().toString().isEmpty()) {
            type = etType.getText().toString();
        }
        if (!etBudget.getText().toString().isEmpty()) {
            budget = Double.parseDouble(etBudget.getText().toString());
        }
        final String zipCode = etLocation.getText().toString();
        if (!zipCode.isEmpty()) {
            ZipCodeApiClient client = new ZipCodeApiClient();
            final String finalType = type;
            final double finalBudget = budget;
            client.getLocationForZip(zipCode, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    try {
                        ParseGeoPoint location = new ParseGeoPoint();
                        location.setLatitude(responseBody.getDouble("lat"));
                        location.setLongitude(responseBody.getDouble("lng"));
                        listener.onFinishFilterSearchDialog(finalType, location, finalBudget, zipCode);
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
            listener.onFinishFilterSearchDialog(type, null, budget, zipCode);
            dismiss();
        }
    }

    public interface FilterSearchDialogListener {
        void onFinishFilterSearchDialog(String type, ParseGeoPoint location, double budget, String zipCode);
    }
}
