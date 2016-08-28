package com.pensum.pensumapplication.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.api_clients.ZipCodeApiClient;
import com.pensum.pensumapplication.helpers.BitmapScaler;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.helpers.NetworkHelper;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by violetaria on 8/21/16.
 */
public class AddTaskFragment extends Fragment {

    public final String APP_TAG = "Pensum App";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    @BindView(R.id.etTitle) EditText etTitle;
//    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etType) EditText etType;
    @BindView(R.id.etBudget) EditText etBudget;
    @BindView(R.id.etLocation) EditText etLocation;
    @BindView(R.id.btnAddTask) Button btnAddTask;
    @BindView(R.id.ivTaskPicPreview) ImageView ivTaskPicPreview;
    ParseGeoPoint location;
    private Unbinder unbinder;

    private OnTaskSavedListener listener;
    private Task task;
    private Bitmap resizedBitmap;

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

            ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
            query.getInBackground(taskId,new GetCallback<Task>() {
                @Override
                public void done(Task object, com.parse.ParseException e) {
                    if (e == null) {
                        Log.d("Parse","Get task from parse");
                        task = object;
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

        if(resizedBitmap!=null) {
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            String temp = etTitle.getText().toString() + etType.getText().toString();
            String taskPicName = temp.replaceAll("\\s+", "");
            Log.d("Save pic",taskPicName);
            byte[] data = stream.toByteArray();
            ParseFile parseFile = new ParseFile(taskPicName + "_taskPic.jpg", data);
            parseFile.saveInBackground();
            task.setTaskPic(parseFile);
        }
    }

    @OnClick(R.id.ibAddPhoto)
    public void addPhoto(ImageButton button) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // RESIZE BITMAP, see section below
                resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 300);

                // Load the taken image into a preview
                Picasso.with(getContext()).load(takenPhotoUri).resize(300,0).into(ivTaskPicPreview);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
