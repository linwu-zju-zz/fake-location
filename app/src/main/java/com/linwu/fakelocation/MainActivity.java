package com.linwu.fakelocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    static boolean isEnabled = false;
    Button startButton;
    Button stopButton;
    EditText latitudeEditText;
    EditText longitudeEditText;
    float mLatitude;
    float mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }
    
    public void initUI() {
        setContentView(R.layout.activity_main);
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
            mLatitude = Float.parseFloat(latitudeEditText.getText().toString());
            mLongitude = Float.parseFloat(longitudeEditText.getText().toString());
        } catch(NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }
        if(!checkCoordinate(mLatitude, mLongitude)) {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, FakeLocationService.class);
        intent.putExtra(getString(R.string.latitude), mLatitude);
        intent.putExtra(getString(R.string.longitude), mLongitude);
        startService(intent);
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