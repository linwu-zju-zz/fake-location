package com.linwu.fakelocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    Context context;
    boolean isEnabled = false;
    final int delay = 100 * 5; //0.5 seconds
    final int lastTime = 1000 * 2; //2 seconds
    Button startButton;
    Button stopButton;
    TextView indicatorTextView;
    EditText latitudeEditText;
    EditText longitudeEditText;
    Timer mTimer;
    LocationManager locationManager;
    double mLatitude;
    double mLongitude;

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Log.i("mock","location changed" + location.toString());
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //To change body of generated methods, choose Tools | Templates.
        context = this;
        initUI();
    }
    
    public void initUI() {
        setContentView(R.layout.activity_main);
        indicatorTextView = (TextView) findViewById(R.id.indicatorTextView);
        indicatorTextView.setText(getString(R.string.indicator_message) + isEnabled);
        latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
        longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startFakeLocations();
            }
        });
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                stopMockLocations();
            }
        });

    }

    public void startFakeLocations() {
        isEnabled = true;

        try {
            mLatitude = Double.parseDouble(latitudeEditText.getText().toString());
            mLongitude = Double.parseDouble(longitudeEditText.getText().toString());
        } catch(NumberFormatException e) {
            Toast.makeText(context, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkCoordinate(mLatitude, mLongitude)) {
            Toast.makeText(context, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        try {
            locationManager.addTestProvider(LocationManager.GPS_PROVIDER, true, false, false, false, true, false, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            indicatorTextView.setText(getString(R.string.indicator_message) + isEnabled);
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setFakeLocation(mLatitude, mLongitude);
                }
            }, delay, lastTime);
        } catch (SecurityException e) {
            e.printStackTrace();
            //Security Exception
            //User has not enabled Mock-Locations
            isEnabled = false;
            enableMockLocationsInSettings();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //probaly the 'unknown provider issue'
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            isEnabled = false;
        }

    }

    public boolean checkCoordinate(double latitude, double longitude) {
        return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
    }

    public void enableMockLocationsInSettings() {
        Toast.makeText(context, getString(R.string.warning_mock_setting_message), Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        try {
            startActivity(intent);
        } catch (Exception e) {
            //Cannot send user to right place in Settings.
        }
    }

    public void setFakeLocation(double latitude, double longitude) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(16F);
        location.setAltitude(0D);
        location.setTime(System.currentTimeMillis());
        location.setBearing(0F);
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

        Log.v("Fake !","fake location enabled");

    }

    public void stopMockLocations() {
        isEnabled = false;
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER) == null) {
                return;
            }
            locationManager.removeUpdates(listener);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            indicatorTextView.setText(getString(R.string.indicator_message) + isEnabled);
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
