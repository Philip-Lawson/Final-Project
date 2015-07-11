/**
 * 
 */
package com.example.appproofofconcept;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * @author Phil
 *
 */
public class BatteryLevelBroadcastReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int charging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
		
		DataProcessingManager.getInstance().sendBatteryInfo(context, charging, level, scale);

	}

}
