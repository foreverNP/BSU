package com.example.task3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends Activity {
    private TextView gpsStatus;
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView altitude;
    private Button startButton;
    private Button stopButton;
    private LocationManager locationManager;
    private PendingIntent pendingIntent;
    private boolean isTracking = false;
    private BroadcastReceiver locationUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        gpsStatus = findViewById(R.id.gpsStatus);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        speed = findViewById(R.id.speed);
        altitude = findViewById(R.id.altitude);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create PendingIntent for location updates
        Intent intent = new Intent(this, LocationReceiver.class);
        intent.setAction(LocationReceiver.ACTION_LOCATION_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Initialize location update receiver
        // Initialize location update receiver
        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    double lat = intent.getDoubleExtra("latitude", 0.0);
                    double lon = intent.getDoubleExtra("longitude", 0.0);
                    float spd = intent.getFloatExtra("speed", 0.0f);
                    double alt = intent.getDoubleExtra("altitude", 0.0);

                    // Лог для проверки обновления данных
                    Log.d("MainActivity", "Location updated: lat=" + lat + ", lon=" + lon + ", speed=" + spd + ", alt=" + alt);

                    latitude.setText(String.format("%.6f", lat));
                    longitude.setText(String.format("%.6f", lon));
                    speed.setText(String.format("%.2f m/s", spd));
                    altitude.setText(String.format("%.2f m", alt));
                }
            }
        };


        // Register the receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            locationUpdateReceiver,
            new IntentFilter(LocationReceiver.ACTION_LOCATION_UPDATE)
        );

        // Set up button listeners
        startButton.setOnClickListener(v -> startTracking());
        stopButton.setOnClickListener(v -> stopTracking());

        // Check GPS status
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsStatus.setText("GPS is turned on");
        } else {
            gpsStatus.setText("GPS is not turned on");
        }
    }

    private void startTracking() {
        if (checkLocationPermission()) {
            isTracking = true;
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000, // minTime (1 секунда)
                        1,    // minDistance (1 метр)
                        pendingIntent
                );
                Toast.makeText(this, "Started tracking location", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Log.e("MainActivity", "Ошибка: " + e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void stopTracking() {
        isTracking = false;
        if (locationManager != null && pendingIntent != null) {
            locationManager.removeUpdates(pendingIntent);
            Toast.makeText(this, "Stopped tracking location", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null && pendingIntent != null) {
            locationManager.removeUpdates(pendingIntent);
        }
        // Unregister the receiver
        if (locationUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver);
        }
    }
}