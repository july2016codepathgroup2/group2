package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.api_clients.ZipCodeApiClient;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.helpers.NetworkHelper;
import com.pensum.pensumapplication.models.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by violetaria on 8/21/16.
 */
public class AddTaskFragment extends Fragment {
    @BindView(R.id.etTitle) EditText etTitle;
//    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etType) EditText etType;
    @BindView(R.id.etBudget) EditText etBudget;
    @BindView(R.id.etLocation) EditText etLocation;
    @BindView(R.id.btnAddTask) Button btnAddTask;
    ParseGeoPoint location;
    private Unbinder unbinder;

    private OnTaskSavedListener listener;
    private Task task;

    public static AddTaskFragment newInstance(Task task) {
        AddTaskFragment addTaskFragment = new AddTaskFragment();

        if(task!=null) {
            Bundle args = new Bundle();
            args.putString("taskId", task.getObjectId());
            addTaskFragment.setArguments(args);
        }
        return addTaskFragment;
    }

    public AddTaskFragment(){

    }

    public interface OnTaskSavedListener {
        void onNewTaskCreated(Task task);
        void onNewTaskEdited(Task task);
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
        View view = inflater.inflate(R.layout.fragment_add_task, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if( s.length() >= 1) {
                    etBudget.removeTextChangedListener(this);
                    String formatted = FormatterHelper.formatMoney(s);
                    etBudget.setText(formatted);
                    etBudget.setSelection(formatted.length());
                    etBudget.addTextChangedListener(this);
                }
            }
        });
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask(view);
            }
        });

        if(getArguments()!=null) {
            String taskId = getArguments().getString("taskId");
            btnAddTask.setText("Edit Task");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
            query.whereEqualTo("objectId",taskId);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {
                        Log.d("Parse","Get task from parse");
                        task = (Task)objects.get(0);
                        populateView(task);
                    } else {
                        Log.d("Parse","Get task from parse error" + e.toString());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void populateView(Task task) {
        etTitle.setText(task.getTitle());
        etType.setText(task.getType());
        etBudget.setText(FormatterHelper.formatDoubleToMoney(task.getBudget()));
        // TODO might want to add extra ZipCode field in Task
    }

    public void saveTask(View view){
        ZipCodeApiClient client = new ZipCodeApiClient();

        if(etLocation.getText().toString().length() == 0)
            Toast.makeText(getContext(), "Please enter task ZipCode.", Toast.LENGTH_LONG).show();

        if (NetworkHelper.isNetworkAvailable(getActivity()) && NetworkHelper.isOnline()) {
            client.getLocationForZip(etLocation.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    try {
                        if(task == null) {
                            task = new Task();
                            task.setStatus("open");
                            task.setPostedBy(ParseUser.getCurrentUser());
                            storeTaskFields(responseBody);
                            task.saveInBackground();
                            listener.onNewTaskCreated(task);
                        } else {
                            storeTaskFields(responseBody);
                            task.saveInBackground();
                            listener.onNewTaskEdited(task);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
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

    private void storeTaskFields(JSONObject responseBody) throws ParseException, JSONException {
        task.setTitle(etTitle.getText().toString());
        //task.setDescription(etDescription.getText().toString());
        task.setType(etType.getText().toString());
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        task.setBudget(new BigDecimal(nf.parse(etBudget.getText().toString()).toString()));
        location = new ParseGeoPoint();
        location.setLatitude(responseBody.getDouble("lat"));
        location.setLongitude(responseBody.getDouble("lng"));
        task.setLocation(location);
    }
}
