/**
 * 
 */
package com.example.appproofofconcept;

import java.util.Observable;
import java.util.Observer;

import finalproject.poc.calculationclasses.ResultsPacketList;
import finalproject.poc.calculationclasses.WorkPacketList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Process;
import android.preference.PreferenceManager;

/**
 * @author Phil
 *
 */
public class DataProcessingManager implements Observer {

	private Thread dataProcessor = new Thread(new DataProcessor());

	private static DataProcessingManager uniqueInstance;

	private DataProcessingManager() {

	}

	public static DataProcessingManager getInstance() {
		if (null == uniqueInstance) {
			uniqueInstance = new DataProcessingManager();
		}

		return uniqueInstance;
	}

	public void sendBatteryInfo(Context context, int charging, int level,
			int scale) {
		if (dataProcessor.isAlive()) {
			if (canContinueProcessing(context, charging, level, scale) == false) {
				dataProcessor.interrupt();
			}
		} else if (canStartProcessing(context, charging, level, scale)) {
			startProcessing();
		}

	}

	public void startProcessing() {
		dataProcessor.setPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
		dataProcessor.start();
	}

	public void startProcessing(WorkPacketList workPacketList) {
		if (dataProcessor.isAlive()) {
			// write request to log and work packet list to file
		} else {
			DataProcessor.setWorkPacketList(workPacketList);
			dataProcessor.start();
		}
	}

	private boolean canContinueProcessing(Context context, int charging,
			int level, int scale) {

		int chargeThreshold = PreferenceManager.getDefaultSharedPreferences(
				context).getInt("MaxCharge", 0);

		return deviceIsPluggedIn(charging, context)
				|| aboveChargingThreshold(scale, level, chargeThreshold);
	}

	private boolean canStartProcessing(Context context, int charging,
			int level, int scale) {

		int minimumCharge = PreferenceManager.getDefaultSharedPreferences(
				context).getInt("MinCharge", 0);

		return deviceIsPluggedIn(charging, context)
				|| aboveChargingThreshold(scale, level, minimumCharge);
	}

	private boolean aboveChargingThreshold(int scale, int level, int threshold) {
		double currentCharge = scale / (double) level;
		double percentageThreshold = threshold / 100.0;

		return currentCharge > percentageThreshold;
	}

	private boolean deviceIsPluggedIn(int charging, Context context) {
		boolean chargingEnabled = PreferenceManager
				.getDefaultSharedPreferences(context).getBoolean(
						"ChargingEnabled", false);
		boolean isCharging = charging == BatteryManager.BATTERY_PLUGGED_AC
				|| charging == BatteryManager.BATTERY_PLUGGED_USB;

		return chargingEnabled && isCharging;
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (null != data && data.getClass().equals(ResultsPacketList.class)) {
			ResultsPacketList resultsPacketList = (ResultsPacketList) data;
			SendResultClientThread resultSenderThread = new SendResultClientThread();
			resultSenderThread.setResults(resultsPacketList);

			Thread resultsThread = new Thread(resultSenderThread);
			resultsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
			resultsThread.start();
		}

	}

}
