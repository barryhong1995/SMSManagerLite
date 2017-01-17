package com.nrp.smsmanagerlite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
    		Intent startActivityIntent = new Intent(context, MainActivity.class);
    		startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		context.startActivity(startActivityIntent);
    	}
    }
}
