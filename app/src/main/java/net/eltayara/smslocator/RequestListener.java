package net.eltayara.smslocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;

public class RequestListener extends BroadcastReceiver {
    public static final String LOC = "@LOC#";
    private final static String TAG = RequestListener.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive was triggered");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if (hasLocationRequest(smsMessage)) {
                    String requester = smsMessage.getOriginatingAddress();
                    Log.d(TAG, "Location request received from " + requester);
                    startService(context, requester);
                }
            }
        }
    }

    protected void startService(Context context, String requester) {
        LocationRequest locationRequest = new LocationRequest(context, requester);
        context.startService(new Intent(context, RequestService.class));
    }

    protected boolean hasLocationRequest(SmsMessage smsMessage) {
        String messageBody = smsMessage.getMessageBody();
        return messageBody.startsWith(LOC);
    }
}
