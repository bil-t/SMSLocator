package net.eltayara.smslocator;

import android.content.Context;
import android.content.Intent;

public class LocationRequest extends Intent {

    public static final String REQUESTER = "REQUESTER";

    public LocationRequest(Context packageContext, String requester) {
        super(packageContext, RequestService.class);
        putExtra(REQUESTER, requester);
    }

    public String requester() {
        return getExtras().getString(REQUESTER);
    }
}
