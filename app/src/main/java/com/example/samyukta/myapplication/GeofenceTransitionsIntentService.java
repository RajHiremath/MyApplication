package com.example.samyukta.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();
    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }





    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "Geo Fence Error" + geofencingEvent.getErrorCode();
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            SmsManager sm = SmsManager.getDefault();
// HERE IS WHERE THE DESTINATION OF THE TEXT SHOULD GO
            String number = "4085054870";
            Log.d(TAG,"SomeMessage");
            //sm.sendTextMessage(number, null, "Picked up Vibusha", null, null);
            //Send notification here
        } else {
            // Log the error.
            Log.e(TAG, "geofenceTransition type " + geofenceTransition);
        }
    }


}
