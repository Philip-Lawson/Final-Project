/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class DeviceDetailsManager {

	private DeviceDetailsJDBC deviceDB = new DeviceDetailsJDBC();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private ConcurrentMap<String, Device> devicesMap = new ConcurrentHashMap<String, Device>();
	private List<String> blacklistedDevices = new ArrayList<String>(1000);

	private static long VALID_RESULTS = 0;
	private static long INVALID_RESULTS = 0;
	private static int NO_OF_BLACKLISTED_DEVICES = 0;
	private static long ACTIVE_DEVICE_THRESHOLD = 0;
	private static int BLACKLISTING_MIN_THRESHOLD = 10;
	private static double MIN_PERCENT_INVALID_RESULTS = 30;

	public boolean addDevice(RegistrationPack registrationPack) {
		String deviceID = registrationPack.getAndroidID();

		if (devicesMap.containsKey(deviceID)) {
			return false;
		} else {
			devicesMap.put(deviceID, new Device(deviceID));
			deviceDB.registerDevice(registrationPack);
			return true;
		}
	}

	public void writeValidResultSent(String deviceID) {

		if (devicesMap.containsKey(deviceID)) {
			devicesMap.get(deviceID).addValidResult();
		}

		updateValidResults();
		deviceDB.writeValidResultSent(deviceID);
	}

	public void writeInvalidResultSent(String deviceID) {

		if (devicesMap.containsKey(deviceID)) {
			devicesMap.get(deviceID).addInvalidResult();

			updateInvalidResults();
			deviceDB.writeInvalidResultSent(deviceID);

			if (shouldBeBlacklisted(deviceID)) {
				lock.writeLock().lock();

				try {
					blacklistedDevices.add(deviceID);
				} finally {
					lock.writeLock().unlock();
				}

				updateBlacklistedDevices();
			}
		}

	}

	private boolean shouldBeBlacklisted(String deviceID) {
		if (devicesMap.get(deviceID).getInvalidResults() > BLACKLISTING_MIN_THRESHOLD) {
			return isAboveBlacklistingPercent(deviceID);
		} else {
			return false;
		}
	}

	private boolean isAboveBlacklistingPercent(String deviceID) {
		Integer validResults = 0;

		if (devicesMap.containsKey(deviceID)) {
			validResults = devicesMap.get(deviceID).getValidResults();
		}

		Integer invalidResults = devicesMap.get(deviceID).getInvalidResults();
		Integer totalResults = invalidResults + validResults;
		double percentInvalid = (invalidResults / totalResults) * 100.0;

		return percentInvalid > MIN_PERCENT_INVALID_RESULTS;
	}

	public boolean deviceIsBlacklisted(String deviceID) {
		lock.readLock().lock();

		try {
			return blacklistedDevices.contains(deviceID);
		} finally {
			lock.readLock().unlock();
		}

	}

	public void updateActiveDevice(String deviceID, long lastTimeActive) {
		if (devicesMap.containsKey(deviceID)) {
			devicesMap.get(deviceID).setLastTimeActive(lastTimeActive);
		}
	}

	public void deregisterDevice(String deviceID) {
		devicesMap.remove(deviceID);
		deviceDB.deregisterDevice(deviceID);
	}

	private synchronized void updateValidResults() {
		++VALID_RESULTS;
	}

	private synchronized void updateInvalidResults() {
		++INVALID_RESULTS;
	}

	private synchronized void updateBlacklistedDevices() {
		++NO_OF_BLACKLISTED_DEVICES;
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
		long currentTime = new Date().getTime();

		for (String deviceID : devicesMap.keySet()) {
			lastTimeActive = devicesMap.get(deviceID).getLastTimeActive();

			if (deviceIsActive(lastTimeActive, currentTime)) {
				devicesActive++;
			}
		}

		return devicesActive;

	}

	private boolean deviceIsActive(long lastTimeActive, long currentTime) {
		long timeDelay = TimeUnit.MINUTES.toMillis(ACTIVE_DEVICE_THRESHOLD);
		return currentTime - lastTimeActive < timeDelay;
	}

	public int setActiveDeviceThreshold(int minutes) {
		ACTIVE_DEVICE_THRESHOLD = Math.abs(minutes);
		return numberOfActiveDevices();
	}

	public void setBlacklistingThreshold(int invalidResults) {
		BLACKLISTING_MIN_THRESHOLD = Math.abs(invalidResults);
	}

	public void setMinPercentInvalidResults(int percent) {
		if (percent <= 100 && percent > 0) {
			MIN_PERCENT_INVALID_RESULTS = percent;
			resetBlacklist();
		}
	}
	
	private void resetBlacklist() {
		lock.writeLock().lock();

		try {
			// reset list of blacklisted devices
			blacklistedDevices.clear();

			for (String deviceID : devicesMap.keySet()) {
				if (shouldBeBlacklisted(deviceID)) {
					blacklistedDevices.add(deviceID);
				}
			}

			NO_OF_BLACKLISTED_DEVICES = blacklistedDevices.size();
		} finally {
			lock.writeLock().unlock();
		}
		
	}
}
