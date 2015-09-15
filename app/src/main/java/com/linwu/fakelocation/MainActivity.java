package com.linwu.fakelocation;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    public static final String TAG = "Fake!";
    static boolean isEnabled = false;
    Button startButton;
    Button stopButton;
    static TextView indicatorTextView;
    EditText latitudeEditText;
    EditText longitudeEditText;
    double mLatitude;
    double mLongitude;

    static LocationListener listener = new LocationListener() {

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
                stopFakeLocations();
            }
        });

    }

    public void startFakeLocations() {
        isEnabled = true;
        try {
            mLatitude = Double.parseDouble(latitudeEditText.getText().toString());
            mLongitude = Double.parseDouble(longitudeEditText.getText().toString());
        } catch(NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }
        if(!checkCoordinate(mLatitude, mLongitude)) {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, SetActivity.class);
        intent.putExtra(getString(R.string.latitude), mLatitude);
        intent.putExtra(getString(R.string.longitude), mLongitude);
        startActivity(intent);
    }

    public boolean checkCoordinate(double latitude, double longitude) {
        return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
    }

    public void stopFakeLocations() {
        isEnabled = false;
        Intent intent = new Intent(this, StopActivity.class);
        startActivity(intent);
    }
}
