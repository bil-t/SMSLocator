package net.eltayara.smslocator;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class RequestService extends IntentService {
    private final static String TAG = IntentService.class.getSimpleName();

    public RequestService() {
        super(RequestService.class.getCanonicalName());
        Log.d(TAG, "RequestService created");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent was triggered");

        if (intent instanceof LocationRequest) {
            LocationRequest locationRequest = (LocationRequest) intent;

            Log.d(TAG, "Received location request from " + locationRequest.requester());
        }
    }
}
