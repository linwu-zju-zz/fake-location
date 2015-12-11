package com.linwu.fakelocation;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class StopActivity extends Activity {

    public static final String TAG = "FakeLocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stopFakeLocations();
    }

    public void stopFakeLocations() {
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER) == null) {
                return;
            }
            locationManager.removeUpdates(FakeLocationService.listener);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            Log.v(TAG, "fake location disabled");
            if (FakeLocationService.mTimer != null) {
                FakeLocationService.mTimer.cancel();
                FakeLocationService.mTimer = null;
            }
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
