package com.example.project2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Button;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvCoordinates, tvGpxContent;
    private Button btnStart, btnStop, btnSave, btnLoad;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean tracking = false;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private List<Location> routePoints = new ArrayList<>();
    private final String GPX_FILENAME = "route.gpx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvGpxContent = findViewById(R.id.tvGpxContent);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Слушатель GPS
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                routePoints.add(location);
                String coords = "Lat: " + location.getLatitude() + "  Lon: " + location.getLongitude();
                tvCoordinates.setText(coords);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        btnStart.setOnClickListener(view -> startTracking());
        btnStop.setOnClickListener(view -> stopTracking());
        btnSave.setOnClickListener(view -> saveRouteToGPX());
        btnLoad.setOnClickListener(view -> loadRouteFromGPX());
    }

    private void startTracking() {
        if (!tracking) {
            try {
                routePoints.clear();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                tracking = true;
                tvCoordinates.setText("Отслеживание запущено...");
            } catch (SecurityException e) {
                e.printStackTrace();
                tvCoordinates.setText("Нет разрешения на местоположение!");
            }
        }
    }

    private void stopTracking() {
        if (tracking) {
            locationManager.removeUpdates(locationListener);
            tracking = false;
            tvCoordinates.setText("Отслеживание остановлено.");
        }
    }

    private void saveRouteToGPX() {
        StringBuilder gpxBuilder = new StringBuilder();
        gpxBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        gpxBuilder.append("<gpx version=\"1.1\" creator=\"RunTracker\">\n");
        gpxBuilder.append("  <trk>\n");
        gpxBuilder.append("    <name>Run Tracker Route</name>\n");
        gpxBuilder.append("    <trkseg>\n");

        for (Location loc : routePoints) {
            gpxBuilder.append("      <trkpt lat=\"")
                    .append(loc.getLatitude())
                    .append("\" lon=\"")
                    .append(loc.getLongitude())
                    .append("\"></trkpt>\n");
        }

        gpxBuilder.append("    </trkseg>\n");
        gpxBuilder.append("  </trk>\n");
        gpxBuilder.append("</gpx>");

        try (FileOutputStream fos = openFileOutput(GPX_FILENAME, MODE_PRIVATE)) {
            fos.write(gpxBuilder.toString().getBytes());
            tvCoordinates.setText("Маршрут сохранён в " + GPX_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
            tvCoordinates.setText("Ошибка сохранения маршрута.");
        }
    }

    private void loadRouteFromGPX() {
        try (FileInputStream fis = openFileInput(GPX_FILENAME)) {
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            String gpxContent = new String(buffer);

            tvGpxContent.setText(gpxContent);
        } catch (Exception e) {
            e.printStackTrace();
            tvGpxContent.setText("Ошибка загрузки маршрута или файл не найден.");
        }
    }
}
