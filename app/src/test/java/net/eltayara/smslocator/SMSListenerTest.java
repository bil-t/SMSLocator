package net.eltayara.smslocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class SMSListenerTest {

    public static final String SENDER = "0623699761";

    static {
        ShadowLog.stream = System.out;
    }

    @Before
    public void setup() {
    }

    @Test
    public void request_listener_should_be_registered() {
        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();

        Assert.assertFalse(registeredReceivers.isEmpty());

        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = SMSListener.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }

        Assert.assertTrue(receiverFound);
    }

    @Test
    public void incoming_sms_location_request_should_start_location_service() {
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        Context context = shadowApplication.getApplicationContext();
        Intent intent = new SMSIntentBuilder().sender(SENDER).context(context).build();

        Assert.assertTrue("Should listen to SMS received action", shadowApplication.hasReceiverForIntent(intent));

        List<BroadcastReceiver> receiversForIntent = shadowApplication.getReceiversForIntent(intent);
        Assert.assertEquals("Only one broadcast receiver shoudl be registered", 1, receiversForIntent.size());

        SMSListener receiver = (SMSListener) receiversForIntent.get(0);
        receiver.onReceive(shadowApplication.getApplicationContext(), intent);

        Intent serviceIntent = shadowApplication.peekNextStartedService();
        Assert.assertEquals("Expected the UserLocationService service to be invoked", UserLocationService.class.getCanonicalName(), serviceIntent.getComponent().getClassName());
    }
}
