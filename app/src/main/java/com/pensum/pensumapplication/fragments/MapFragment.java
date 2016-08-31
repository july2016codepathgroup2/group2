package com.pensum.pensumapplication.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.FilterSearchDialogFragment.FilterSearchDialogListener;
import com.pensum.pensumapplication.helpers.SearchHelper;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by violetaria on 8/16/16.
 */
@RuntimePermissions
public class MapFragment extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FilterSearchDialogListener {

    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    static final int MAX_TASKS_TO_SHOW = 50;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private View view;
    private HashMap<String, Task> markers = new HashMap<String, Task>();
    private Map<String, Marker> taskTitleToMarkerMap = new HashMap<>();
    private SearchView searchView;
    private String typeFilter;
    private ParseGeoPoint locationFilter;
    private double budgetFilter;
    private String zipCode;

    private InfoWindowListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public interface InfoWindowListener {
        void infoWindowClicked(Task task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InfoWindowListener) {
            listener = (InfoWindowListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MapFragment.InfoWindowListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_tasks_map, container, false);
                mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap map) {
                            loadMap(map);
                            populateTasks();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mapFragment != null) {
                    populateTasks();
                }
            }
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    public void populateTasks() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereNotEqualTo("posted_by", ParseUser.getCurrentUser());
        query.whereEqualTo("status", "open");
        query.include("posted_by");
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or CACHE_ONLY
        query.findInBackground(new FindCallback<Task>() {
            public void done(List<Task> tasks, ParseException e) {
                if (e == null) {
                    // clear out all old markers ?
                    map.clear();
                    BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

                    for (int i = 0; i < tasks.size(); i++) {
                        ParseGeoPoint location = tasks.get(i).getLocation();
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        String title = tasks.get(i).getTitle();
                        Task task = tasks.get(i);
                        MarkerOptions markerOptions = new MarkerOptions().position(point).title(title).icon(defaultMarker);
                        Marker marker = map.addMarker(markerOptions);
                        taskTitleToMarkerMap.put(task.getTitle(), marker);
                        markers.put(marker.getId(), task);
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            // TODO somehow get location in activity and pass down
            MapFragmentPermissionsDispatcher.getMyLocationWithCheck(this);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(true);
            map.getUiSettings().setScrollGesturesEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                    TextView tvType = (TextView) v.findViewById(R.id.tvType);
                    TextView tvBudget = (TextView) v.findViewById(R.id.tvBudget);
                    ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);

                    Task task = markers.get(marker.getId()); //use the ID to get the info
                    tvTitle.setText(task.getTitle());
                    tvBudget.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
                    tvType.setText("#"+task.getType());
                    ParseUser postedBy = task.getPostedBy();
                    String imageUrl = postedBy.getString("profilePicUrl");
                    if (imageUrl != null) {
                        Picasso.with(getContext()).load(imageUrl).
                                transform(new CropCircleTransformation()).into(ivProfileImage);
                    } else {
                        Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                                transform(new CropCircleTransformation()).into(ivProfileImage);
                    }
                    return v;
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Task task = markers.get(marker.getId());
                    return false;
                }
            });
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Task task = markers.get(marker.getId());
                    showTaskDetailFragment(task);
                }
            });
        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(getActivity(), resultCode,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
            }

            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings("all")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            map.setMyLocationEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        setupSearchMenuItem(menu);
        setupFilterSearchMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onFinishFilterSearchDialog(String type, ParseGeoPoint location, double budget, String zipCode) {
        typeFilter = type;
        locationFilter = location;
        budgetFilter = budget;
        this.zipCode = zipCode;

        String query = searchView.getQuery().toString();
        FindCallback<Task> finishSearchCallback = new FindCallback<Task>() {
            @Override
            public void done(List<Task> tasks, ParseException e) {
                if (e == null) {
                    if (tasks.size() > 0) {
                        Task currentTask = tasks.get(0);
                        ParseGeoPoint location = currentTask.getLocation();
                        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
                        Marker marker = taskTitleToMarkerMap.get(currentTask.getTitle());
                        marker.showInfoWindow();
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        };
        SearchHelper.searchForTasksWith(query, typeFilter, locationFilter, budgetFilter, finishSearchCallback);
    }

    private void setupSearchMenuItem(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                FindCallback<Task> finishSearchCallback = new FindCallback<Task>() {
                    @Override
                    public void done(List<Task> tasks, ParseException e) {
                        if (e == null) {
                            //Right now it's only going to show the top-most result
                            if (tasks.size() > 0) {
                                Task currentTask = tasks.get(0);
                                ParseGeoPoint location = currentTask.getLocation();
                                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
                                Marker marker = taskTitleToMarkerMap.get(currentTask.getTitle());
                                marker.showInfoWindow();
                            }
                        } else {
                            Log.d("item", "Error: " + e.getMessage());
                        }
                    }
                };

                SearchHelper.searchForTasksWith(query, typeFilter, locationFilter,
                        budgetFilter, finishSearchCallback);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupFilterSearchMenuItem(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.miFilterSearch);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentManager fm = getFragmentManager();
                FilterSearchDialogFragment filterSearchDialogFragment =
                        FilterSearchDialogFragment.newInstance(typeFilter, budgetFilter, zipCode);
                filterSearchDialogFragment.setTargetFragment(MapFragment.this, 300);
                filterSearchDialogFragment.show(fm, "fragment_Filter_Search");
                return true;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        connectClient();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    /*
 * Called by Location Services when the request to connect the client
 * finishes successfully. At this point, you can request the current
 * location or start periodic updates
 */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getContext(), "Please make sure you have granted location permissions.", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Toast.makeText(getContext(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            // TODO call into map fragment to update location
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(getContext(), "Please make sure you have GPS enabled.", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getContext(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getContext(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    private void showTaskDetailFragment(Task task) {
        listener.infoWindowClicked(task);
    }
}

