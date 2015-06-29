package com.example.appproofofconcept;

import com.example.testingagainnnn.R;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class BatteryInfoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int charge = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		//Toast.makeText(context.getApplicationContext(), charge, Toast.LENGTH_LONG).show();
		//context.getApplicationContext()
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Battery level " + charge);
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(001, mBuilder.build());
	}

}
