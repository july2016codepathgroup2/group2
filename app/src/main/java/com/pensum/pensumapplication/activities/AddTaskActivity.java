package com.pensum.pensumapplication.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
 * Created by violetaria on 8/20/16.
 */
public class AddTaskActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etDescription;
    private EditText etType;
    private EditText etBudget;
    private EditText etLocation;
    private Button btnAddTask;
    ParseGeoPoint location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etType = (EditText) findViewById(R.id.etType);
        etBudget = (EditText) findViewById(R.id.etBudget);
        etLocation = (EditText) findViewById(R.id.etLocation);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask(view);
            }
        });
    }

    public void saveTask(View view){
        ZipCodeApiClient client = new ZipCodeApiClient();

        if (NetworkHelper.isNetworkAvailable(this) && NetworkHelper.isOnline()) {
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
            Toast.makeText(this, "You're offline task not saved.", Toast.LENGTH_LONG).show();
        }
        this.finish();
    }
}
