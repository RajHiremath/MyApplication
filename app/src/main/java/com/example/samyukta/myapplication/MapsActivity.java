package com.example.samyukta.myapplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.CursorAdapter;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MapsActivity extends BaseActivity implements ConnectionCallbacks,
        OnConnectionFailedListener,ResultCallback {
    // LogCat tag
    private static final String TAG = MapsActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int GEOFENCE_RADIUS_IN_METERS = 100;
    private static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    private TextView lblLocation;
    private Button btnShowLocation, btnStartLocationUpdates, btnLocation;
    private EditText locationText,mPhoneNumber,mMessage;


    private List <Geofence>mGeofenceList;

    private PendingIntent mGeofencePendingIntent;

    private ImageView imgRemoveGeofence;

    //Code related to List
    private ListView lv;
    private ArrayList <MyGeofenceData> mySavedList = new ArrayList<MyGeofenceData>(10);
    private CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationText = (EditText) findViewById(R.id.editText);
        mPhoneNumber = (EditText) findViewById(R.id.editTextPhone);
        mMessage = (EditText) findViewById(R.id.editTextMessage);


        btnLocation = (Button) findViewById(R.id.btnLocation);

        lblLocation = (TextView) findViewById(R.id.lblLocation);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);




        mGeofenceList = new ArrayList<>();
        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        // Show location button click listener
        btnLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddressToLocation();
            }
        });


        //Code for listView
        initSavedGeofenceList();
        lv=(ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter(this, mySavedList);
        lv.setAdapter(adapter);

    }

    /**
     * Method to conver address to lattitude and longittude
     * */
    private void AddressToLocation() {
        Log.d(TAG, "AddressToLocation: " + locationText.getText());

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        // LatLng p1 = null;
        String addressTxt = locationText.getText().toString();
        try {
            address = coder.getFromLocationName(addressTxt, 5);
            if (address == null) {
                return;
            }
            Address location = address.get(0);

            lblLocation.setText(location.getLatitude() + " , " + location.getLongitude());
            setUpGeofence(location.getLatitude(),location.getLongitude());
//            TextView textViewAddress = (TextView)findViewById(R.id.firstLine);
//            TextView textViewMessage = (TextView)findViewById(R.id.secondLine);
            Log.d(TAG,locationText.getText().toString());
//            textViewAddress.setText(locationText.getText().toString());
//            textViewMessage.setText(mMessage.getText().toString());
            //save to local
            saveValue(getNextId(),addressTxt,mPhoneNumber.getText().toString(),mMessage.getText().toString());
            updateSavedList(addressTxt,mPhoneNumber.getText().toString(),mMessage.getText().toString());
            //clear text fields
            locationText.setText("");
            mMessage.setText("");
            mPhoneNumber.setText("");

        } catch (IOException ioe) {

        }
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        // mLastLocation = LocationServices.FusedLocationApi
        //        .getLastLocation(mGoogleApiClient);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//    .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            lblLocation.setText(latitude + ", " + longitude);

        } else {

            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

private void setUpGeofence(double latitude, double longitude) {
    mGeofenceList.add(new Geofence.Builder()
            .setRequestId("myRequestId")
            .setCircularRegion(
                    latitude,
                    longitude,
                    GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                    Geofence.GEOFENCE_TRANSITION_EXIT)
            .build());
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
    }
    LocationServices.GeofencingApi.addGeofences(
            mGoogleApiClient,
            getGeofencingRequest(),
            getGeofencePendingIntent()
    ).setResultCallback(this);
}

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    public void removeGeofenceById() {
        List <String> myList = new ArrayList<>();
        myList.add(0,"myRequestId");
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,myList).setResultCallback(this);
        removeKey(getCurrentId());
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onResult(@NonNull Result result) {
       Log.d(TAG,"Result Geofence "+ result.toString()) ;
        Log.d(TAG,"Result Geofence "+ result.getStatus()) ;
    }

    public void initSavedGeofenceList(){

        int length = getCurrentId();
        for(int i=0;i<length;i++)
        {
            mySavedList.add(i,new MyGeofenceData(getAddress(i),getPhoneNumber(i),getMessage(i)));
        }
    }

    public void updateSavedList(String addr, String ph, String msg){
        mySavedList.add(mySavedList.size(),new MyGeofenceData(addr, ph, msg));
        adapter.setListData(mySavedList);
    }
    public class MyGeofenceData{
        String address;
        String phone;
        String message;

        public MyGeofenceData(String addr, String ph, String msg){
            this.address = addr;
            this.phone = ph;
            this.message = msg;
        }
    }
}