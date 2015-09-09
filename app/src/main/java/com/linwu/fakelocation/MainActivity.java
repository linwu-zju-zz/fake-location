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

/**
 *
 * @author FrankkieNL
 */
public class MainActivity extends Activity {

    Context context;
    String mockProviderName = "gps";
    boolean isMockEnabled = false;
    int delay = 100 * 2; //0.2 seconds
    Button btn1;
    Button btn2;
    TextView tv1;
    EditText ed1;
    Timer timer1;
    LocationManager locationManager;

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Log.e("mock","location changed" + location.toString());
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

    /**
     * UI boilerplate
     */
    public void initUI() {
        setContentView(R.layout.activity_main);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setText("Mock Locations\nEnabled: " + isMockEnabled);
        ed1 = (EditText) findViewById(R.id.ed1);
        //ed1.setVisibility(View.GONE); //not needed, just use the name 'mock'
        ed1.setText(mockProviderName);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startMockLocations();
            }
        });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                stopMockLocations();
            }
        });

    }

    public void startMockLocations() {
        isMockEnabled = true;
        mockProviderName = ed1.getText().toString();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        try {
            locationManager.addTestProvider(LocationManager.GPS_PROVIDER, true, false, false, false, true, false, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            tv1.setText("Mock Locations\nEnabled: " + isMockEnabled);
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    setMockLocation();
                }
            }, 500, 2000);
        } catch (SecurityException e) {
            e.printStackTrace();
            //Security Exception
            //User has not enabled Mock-Locations
            isMockEnabled = false;
            enableMockLocationsInSettings();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //probaly the 'unknown provider issue'
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            isMockEnabled = false;
        }

    }

    public void enableMockLocationsInSettings() {
        Toast.makeText(context, "Please Enable Mock Locations in Settings", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        try {
            startActivity(intent);
        } catch (Exception e) {
            //Apparantly something went wrong here.. Cannot send user to right place in Settings.
        }
    }

    public void setMockLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        location.setLatitude(39.9167);
        location.setLongitude(116.3833);
        location.setAccuracy(16F);
        location.setAltitude(0D);
        location.setTime(System.currentTimeMillis());
        location.setBearing(0F);
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

        Log.v("Mock !","mocked");

    }

    public void stopMockLocations() {
        isMockEnabled = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(listener);
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
        tv1.setText("Mock Locations\nEnabled: " + isMockEnabled);
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
    }

}
