/**
 * 
 */
package com.example.appproofofconcept;

import java.util.Observable;
import java.util.Observer;

import com.example.testingagainnnn.R;

import finalproject.poc.calculationclasses.ResultsPacketList;
import finalproject.poc.calculationclasses.WorkPacketList;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Process;
import android.preference.PreferenceManager;

/**
 * @author Phil
 *
 */
public class DataProcessingManager extends Observable implements Observer  {

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
		boolean canProcessData = canProcessData(context, charging, level, scale);
		boolean isProcessingData = dataProcessor.isAlive();

		if (isProcessingData) {
			if (!canProcessData)
				dataProcessor.interrupt();
		} else if (canProcessData) {
			dataProcessor.start();
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

	private boolean canProcessData(Context context, int charging, int level,
			int scale) {
		// get preferences
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		int minimumCharge = pref.getInt(
				context.getString(R.string.battery_limit_key), 0);
		boolean chargingEnabled = pref.getBoolean(
				context.getString(R.string.charging_key), true);

		if (chargingEnabled) {
			return deviceIsPluggedIn(charging, context)
					|| aboveChargingThreshold(scale, level, minimumCharge);
		} else {
			return aboveChargingThreshold(scale, level, minimumCharge);
		}
	}

	private boolean aboveChargingThreshold(int scale, int level, int threshold) {
		double currentCharge = scale / (double) level;
		double percentageThreshold = threshold / 100.0;

		return currentCharge > percentageThreshold;
	}

	private boolean deviceIsPluggedIn(int charging, Context context) {
		return charging == BatteryManager.BATTERY_PLUGGED_AC
				|| charging == BatteryManager.BATTERY_PLUGGED_USB;
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
		} else if(null!= data && data.getClass().equals(DataProcessor.ProgressPacket.class)){			
			setChanged();
			notifyObservers(data);
		}

	}
}
