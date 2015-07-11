/**
 * 
 */
package finalproject.poc.persistence;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Phil
 *
 */
public class DeviceDetailsManager {

	private DeviceDetailsJDBC deviceDB = new DeviceDetailsJDBC();
	private ConcurrentMap<String, Long> activeDevicesMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentMap<String, Integer> validResultsMap = new ConcurrentHashMap<String, Integer>();
	private ConcurrentMap<String, Integer> invalidResultsMap = new ConcurrentHashMap<String, Integer>();
	private ConcurrentLinkedQueue<String> blacklistedDevices = new ConcurrentLinkedQueue<String>();

	private static int BLACKLISTING_MIN_THRESHOLD = 10;
	private static double MIN_PERCENT_INVALID_RESULTS = 30;
	private static long VALID_RESULTS = 0;
	private static long INVALID_RESULTS = 0;
	private static int NO_OF_BLACKLISTED_DEVICES = 0;
	private static long ACTIVE_DEVICE_THRESHOLD = 0;

	public void writeValidResultSent(String deviceID) {
		Integer validResults;

		if (validResultsMap.containsKey(deviceID)) {
			validResults = validResultsMap.get(deviceID) + 1;
		} else {
			validResults = 1;
		}

		validResultsMap.put(deviceID, validResults);
		updateValidResults();

		deviceDB.writeValidResultSent(deviceID);
	}

	public void writeInvalidResultSent(String deviceID) {
		Integer invalidResults;

		if (invalidResultsMap.containsKey(deviceID)) {
			invalidResults = invalidResultsMap.get(deviceID) + 1;
		} else {
			invalidResults = 1;
		}

		invalidResultsMap.put(deviceID, invalidResults);
		updateInvalidResults();

		deviceDB.writeInvalidResultSent(deviceID);

		if (shouldBeBlacklisted(deviceID)) {
			blacklistedDevices.add(deviceID);
			deviceDB.blacklistDevice(deviceID);
			updateBlacklistedDevices();
		}

	}

	private boolean shouldBeBlacklisted(String deviceID) {
		if (invalidResultsMap.get(deviceID) > BLACKLISTING_MIN_THRESHOLD) {
			return isAboveBlacklistingPercent(deviceID);
		} else {
			return false;
		}
	}

	private boolean isAboveBlacklistingPercent(String deviceID) {
		Integer validResults = 0;

		if (validResultsMap.containsKey(deviceID)) {
			validResults = validResultsMap.get(deviceID);
		}

		Integer invalidResults = invalidResultsMap.get(deviceID);
		Integer totalResults = invalidResults + validResults;

		return invalidResults / (double) totalResults > MIN_PERCENT_INVALID_RESULTS;
	}

	public boolean deviceIsBlacklisted(String deviceID) {
		return blacklistedDevices.contains(deviceID);
	}
	
	public void updateActiveDevice(String deviceID){
		activeDevicesMap.put(deviceID, ACTIVE_DEVICE_THRESHOLD);
	}
	
	public void deactivateDevice(String deviceID){
		activeDevicesMap.remove(deviceID);
	}

	private synchronized void updateValidResults() {
		VALID_RESULTS++;
	}

	private synchronized void updateInvalidResults() {
		INVALID_RESULTS++;
	}

	private synchronized void updateBlacklistedDevices() {
		NO_OF_BLACKLISTED_DEVICES++;
	}

	public long numberOfValidResults() {
		return VALID_RESULTS;
	}

	public long numberOfInvalidResults() {
		return INVALID_RESULTS;
	}

	public int numberOfBlacklistedDevices() {
		return NO_OF_BLACKLISTED_DEVICES;
	}	

	public int numberOfActiveDevices() {
		int devicesActive = 0;
		long lastTimeActive;
		for(String deviceID: activeDevicesMap.keySet()){
			lastTimeActive = activeDevicesMap.get(deviceID);
			
			if (deviceIsActive(lastTimeActive)){
				devicesActive++;
			}
		}
		
		return devicesActive;
		
	}
	
	private boolean deviceIsActive(long lastTimeActive){
		//TODO calculate time since last active
		return false;
	}
}
