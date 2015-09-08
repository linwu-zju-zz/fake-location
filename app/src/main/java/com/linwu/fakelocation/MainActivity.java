package com.linwu.fakelocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
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
    TimerTask task1;

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Log.e("mock", "location changed" + location.toString());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onProviderEnabled(String provider) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void onProviderDisabled(String provider) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

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
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        try {
            //locationManager.addTestProvider(mockProviderName, false /*network*/, false/*satellite*/, false/*call*/, false/*moneycost*/, true/*altitude*/, true/*speed*/, true/*bearing*/, Criteria.POWER_LOW/*power*/, Criteria.ACCURACY_FINE /*accuracy*/);
            locationManager.addTestProvider(mockProviderName, false /*network*/, false/*satellite*/, false/*call*/, false/*moneycost*/, false,false,false,1,1);
            locationManager.setTestProviderEnabled(mockProviderName, true);
            locationManager.requestLocationUpdates(mockProviderName, 0, 0, listener);
            tv1.setText("Mock Locations\nEnabled: " + isMockEnabled);
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            timer1 = new Timer();
            task1 = new TimerTask() {
                @Override
                public void run() {
                    changeMockLocation();
                }
            };
            timer1.schedule(task1, delay, delay); //repeating
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

    public void changeMockLocation() {
        //Is it needed to call this from the UI-thread?
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Location mockLocation = new Location(mockProviderName);
        Random random = new Random();
        mockLocation.setAccuracy(random.nextFloat() * 50);
        mockLocation.setAltitude(random.nextDouble() * 100);
        mockLocation.setBearing(random.nextFloat() * 360);
        mockLocation.setElapsedRealtimeNanos(System.nanoTime());
        /*mockLocation.setLatitude(-90 + random.nextDouble() * (90 * 2)); //-90 till 90
        mockLocation.setLongitude(-180 + random.nextDouble() * (180 * 2)); //-180 till 180*/

        mockLocation.setLatitude(39.9167); //-90 till 90    //39.9167
        mockLocation.setLongitude(116.3833); //-180 till 180    //116.3833
        mockLocation.setSpeed(random.nextFloat() * 10); //speed in m/s
        mockLocation.setTime(System.currentTimeMillis());
        locationManager.setTestProviderLocation(mockProviderName, mockLocation);

        Log.v("Mock !","mocked");

    }

    public void stopMockLocations() {
        isMockEnabled = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(listener);
        locationManager.setTestProviderEnabled(mockProviderName, false);
        tv1.setText("Mock Locations\nEnabled: " + isMockEnabled);
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
        locationManager.removeTestProvider(mockProviderName);
    }

}
