package net.eltayara.smslocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class LocationProvider {
    private static final String TAG = LocationProvider.class.getSimpleName();
    private static final int TIME_DIFFERENCE_THRESHOLD = 60 * 1000;
    private static final long MIN_TIME = (long) (5 * 1000);
    private static final long MIN_DISTANCE = 10L;
    private final Context context;
    private final LocationManager locationManager;
    private final LocationListener locationListener;
    private final LocationCallback callback;
    private Location bestKnownLocation;

    public LocationProvider(Context context, LocationCallback callback) {
        this.context = context;
        this.callback = callback;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = newLocationListener();
    }

    private Location getBestKnownLocation() {
        return bestKnownLocation;
    }

    private void setBestKnownLocation(Location location) {
        bestKnownLocation = location;
    }

    public void stop() {
        Log.d(TAG, "Location provider stopped");
        locationManager.removeUpdates(locationListener);
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        if (CheckPermissions()) {
            String providerName = getProviderName(context, locationManager);
            Log.d(TAG, "Get location update from : " + providerName);
            setBestKnownLocation(locationManager.getLastKnownLocation(providerName));
            registerLocationListener(locationManager, providerName);
        }
    }

    private boolean CheckPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "Locaton premisisons not granted");
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void registerLocationListener(LocationManager locationManager, String providerName) {
        locationManager.requestLocationUpdates(providerName, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    private LocationListener newLocationListener() {
        return new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Provider status changed : " + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Provider disabled: " + provider);
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location changed : " + location);
                callback.onNewLocationAvailable(updateBestKnownLocation(location, context));
                stop();
            }
        };
    }

    private Location updateBestKnownLocation(Location location, Context context) {
        if (isBetterLocation(getBestKnownLocation(), location)) {
            setBestKnownLocation(location);
            Log.d(TAG, "Better location found: " + location);
        }
        return getBestKnownLocation();
    }

    private boolean isBetterLocation(Location oldLocation, Location newLocation) {
        if (oldLocation == null) {
            return true;
        }

        boolean isNewer = newLocation.getTime() > oldLocation.getTime();
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            return true;
        } else if (isMoreAccurate && !isNewer) {
            long timeDifference = newLocation.getTime() - oldLocation.getTime();
            return timeDifference > -TIME_DIFFERENCE_THRESHOLD;
        }

        return false;
    }

    private String getProviderName(Context context, LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        return locationManager.getBestProvider(criteria, true);
    }

    public interface LocationCallback {
        void onNewLocationAvailable(Location location);
    }
}
