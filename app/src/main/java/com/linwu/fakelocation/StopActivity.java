package com.linwu.fakelocation;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class StopActivity extends Activity {

    public static final String TAG = "Fake!";

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
            locationManager.removeUpdates(MainActivity.listener);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            MainActivity.indicatorTextView.setText(getString(R.string.indicator_message) + MainActivity.isEnabled);
            Log.v(TAG, "fake location disabled");
            if (FakeLocationService.mTimer != null) {
                FakeLocationService.mTimer.cancel();
                FakeLocationService.mTimer = null;
            }
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.stop_fake_location_exception), Toast.LENGTH_LONG).show();
        }
    }
}
