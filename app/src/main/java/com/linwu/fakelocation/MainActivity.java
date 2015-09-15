package com.linwu.fakelocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;


public class MainActivity extends Activity {

    public static final String TAG = "Fake!";
    Context context;
    boolean isEnabled = false;
    Button startButton;
    Button stopButton;
    TextView indicatorTextView;
    EditText latitudeEditText;
    EditText longitudeEditText;
    Timer mTimer;
    double mLatitude;
    double mLongitude;

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Log.v(TAG,"location changed" + location.toString());
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
        Intent intent = new Intent(context, SetActivity.class);
        intent.putExtra(getString(R.string.latitude), mLatitude);
        intent.putExtra(getString(R.string.longitude), mLongitude);
        startActivity(intent);
    }

    public boolean checkCoordinate(double latitude, double longitude) {
        return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
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
            if (SetActivity.mTimer != null) {
                SetActivity.mTimer.cancel();
                SetActivity.mTimer = null;
            }
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
