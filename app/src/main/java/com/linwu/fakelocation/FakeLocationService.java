package com.linwu.fakelocation;

import android.app.IntentService;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class FakeLocationService extends IntentService {

    LocationManager locationManager;
    static Timer mTimer;
    final int delay = 100 * 5; //0.5 seconds
    final int lastTime = 1000 * 2; //2 seconds
    double mLatitude;
    double mLongitude;
    public static final String TAG = "FakeLocation";
    static LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            Log.v(TAG, "location changed" + location.toString());
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}

    };

    public FakeLocationService() {
        super("FakeLocationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mLatitude = (double) extras.getFloat("lat");
            mLongitude = (double) extras.getFloat("long");
        }
        startFakeLocation();
    }

    public void startFakeLocation() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        try {
            locationManager.addTestProvider(LocationManager.GPS_PROVIDER, true, false, false, false, true, false, true,
                    Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
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
            //Security Exception - User has not enabled Mock-Locations
            Log.v(TAG, "Mock location is not enabled on your device");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //probably the 'unknown provider issue'
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
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

        Log.v(TAG, "fake location enabled");

    }
}
