package net.eltayara.smslocator;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import static android.net.Uri.Builder;

public class SMSNotifierService extends IntentService {
    private static final String TAG = SMSNotifierService.class.getSimpleName();
    private static final String REQUESTER = "REQUESTER";

    public SMSNotifierService() {
        super(TAG);
    }

    public static Intent newIntent(Context context, String requester) {
        Intent intent = new Intent(context, SMSNotifierService.class);
        // Workaround
        // Using the URI as a holder of the intent extra data, using putExtra will lead to loosing LocationResult
        // see https://stackoverflow.com/questions/32893006/android-fusedlocationproviderapi-incoming-intent-has-no-locationresult-or-locat
        Builder uriBuilder = new Builder();
        uriBuilder.scheme("http")
                .authority("workaround.com")
                .appendPath("extra")
                .appendQueryParameter(REQUESTER, requester);
        intent.setData(uriBuilder.build());
        return intent;
    }

    private String extractRequester(Intent intent) {
        Uri uri = intent.getData();
        return uri.getQueryParameter(REQUESTER);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String requester = extractRequester(intent);
        Log.d(TAG, "onHandleWork " + requester);

        boolean hasLocationResult = LocationResult.hasResult(intent);
        if (hasLocationResult) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Log.d(TAG, "location result : " + locationResult);
        } else {
            Log.d(TAG, "no location result : " + hasLocationResult);
        }
    }
}
