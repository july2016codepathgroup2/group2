package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.api_clients.ZipCodeApiClient;
import com.pensum.pensumapplication.helpers.NetworkHelper;
import com.pensum.pensumapplication.models.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;

/**
 * Created by violetaria on 8/21/16.
 */
public class AddTaskFragment extends Fragment {
    private EditText etTitle;
    private EditText etDescription;
    private EditText etType;
    private EditText etBudget;
    private EditText etLocation;
    private Button btnAddTask;
    ParseGeoPoint location;

    private OnTaskSavedListener listener;

    public AddTaskFragment(){

    }

    public interface OnTaskSavedListener {
        public void onNewTaskCreated(Task task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskSavedListener) {
            listener = (OnTaskSavedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddTaskFragment.OnTaskSavedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        etType = (EditText) view.findViewById(R.id.etType);
        etBudget = (EditText) view.findViewById(R.id.etBudget);
        etLocation = (EditText) view.findViewById(R.id.etLocation);
        btnAddTask = (Button) view.findViewById(R.id.btnAddTask);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask(view);
            }
        });
    }

    public void saveTask(View view){
        ZipCodeApiClient client = new ZipCodeApiClient();

        if (NetworkHelper.isNetworkAvailable(getActivity()) && NetworkHelper.isOnline()) {
            client.getLocationForZip(etLocation.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    try {
                        Task task = new Task();
                        task.setStatus("open");
                        task.setPostedBy(ParseUser.getCurrentUser());
                        task.setTitle(etTitle.getText().toString());
                        task.setDescription(etDescription.getText().toString());
                        task.setType(etType.getText().toString());
                        task.setBudget(new BigDecimal(etBudget.getText().toString()));
                        location = new ParseGeoPoint();
                        location.setLatitude(responseBody.getDouble("lat"));
                        location.setLongitude(responseBody.getDouble("lng"));
                        task.setLocation(location);
                        task.saveInBackground();
                        listener.onNewTaskCreated(task);
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
            // Todo maybe call the listener method and check for null there
            Toast.makeText(getContext(), "You're offline task not saved.", Toast.LENGTH_LONG).show();
        }
    }
}
