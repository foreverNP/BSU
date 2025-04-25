package com.example.task3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationReceiver extends BroadcastReceiver {
    public static final String ACTION_LOCATION_UPDATE = "com.example.task3.LOCATION_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ACTION_LOCATION_UPDATE)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Location location = extras.getParcelable(LocationManager.KEY_LOCATION_CHANGED);
                if (location != null) {

                    Log.d("LocationReceiver", "Location received: " + location.getLatitude() + ", " + location.getLongitude());
                    // Create new intent with location data
                    Intent locationIntent = new Intent(ACTION_LOCATION_UPDATE);
                    locationIntent.putExtra("latitude", location.getLatitude());
                    locationIntent.putExtra("longitude", location.getLongitude());
                    locationIntent.putExtra("speed", location.getSpeed());
                    locationIntent.putExtra("altitude", location.getAltitude());
                    
                    // Send broadcast using LocalBroadcastManager
                    LocalBroadcastManager.getInstance(context).sendBroadcast(locationIntent);
                }
            }
        }
    }
} 