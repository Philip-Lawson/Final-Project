/**
 * 
 */
package com.example.appproofofconcept;

import com.example.testingagainnnn.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * @author Phil
 *
 */
public class NetworkInfoReceiver extends BroadcastReceiver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String networkKey = context.getString(R.string.network_key);

		if (isConnected(pref, networkKey, networkInfo.getType())) {
			//TODO start task service
		}

	}

	private boolean isConnected(SharedPreferences pref, String networkKey,
			int networkInfo) {

		if (pref.getBoolean(networkKey, true))
			return networkInfo == ConnectivityManager.TYPE_WIFI;
		else
			return networkInfo == ConnectivityManager.TYPE_WIFI
					|| networkInfo == ConnectivityManager.TYPE_MOBILE;
	}

}
