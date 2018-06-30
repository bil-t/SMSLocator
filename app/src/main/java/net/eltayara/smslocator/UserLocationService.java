package net.eltayara.smslocator;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class UserLocationService extends IntentService {
    private final static String TAG = UserLocationService.class.getSimpleName();
    private static final String REQUESTER = "REQUESTER";

    public UserLocationService() {
        super(TAG);
    }

    @NonNull
    public static Intent newIntent(Context context, String requester) {
        Intent intent = new Intent(context, UserLocationService.class);
        intent.putExtra(REQUESTER, requester);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        LocationRequest locationRequest = newLocationRequest();
        String requester = intent.getStringExtra(REQUESTER);
        Intent smsNotifierServiceIntent = SMSNotifierService.newIntent(context, requester);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, smsNotifierServiceIntent, 0);

        Log.d(TAG, "onHandleWork " + requester);
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, pendingIntent);
    }

    @NonNull
    private LocationRequest newLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        return request;
    }
}
