/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
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
public class DeviceDetailsManager extends Observable {
	
	public static final int DEFAULT_ACTIVE_DEVICE_THRESHOLD = 10;

	private DeviceDetailsJDBC deviceDB = new DeviceDetailsJDBC();
	private UserDetailsManager userDetails;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private ConcurrentMap<String, Device> devicesMap = new ConcurrentHashMap<String, Device>();
	private List<String> blacklistedDevices = new ArrayList<String>(1000);

	private long VALID_RESULTS = 0;
	private long INVALID_RESULTS = 0;
	private int NO_OF_BLACKLISTED_DEVICES = 0;
	private int ACTIVE_DEVICE_THRESHOLD = DEFAULT_ACTIVE_DEVICE_THRESHOLD;
	private int BLACKLISTING_MIN_THRESHOLD = 10;
	private double MIN_PERCENT_INVALID_RESULTS = 30;
	private long AVERAGE_PROCESSING_TIME = 0;
	private long PROCESSING_DIVISOR = 0;

	public DeviceDetailsManager() {
		super();
	}

	public DeviceDetailsManager(UserDetailsManager userDetails) {
		this.userDetails = userDetails;
	}

	public void setUserDetailsManager(UserDetailsManager userDetails) {
		this.userDetails = userDetails;
	}

	/**
	 * Loads device details from the database. This should be called on startup.
	 * If the program is re-started it will fill the devices map with all
	 * previously registered devices. It will also fill the blacklist with the
	 * appropriate devices.
	 */
	public void loadDevices() {
		devicesMap.putAll(deviceDB.loadDevices());
		resetBlacklist();
		setChanged();
		notifyObservers();
	}

	/**
	 * Add a device to the persistence layer.
	 * 
	 * @param registrationPack
	 *            the device's registration pack.
	 * @return true if the device is added successfully.
	 */
	public boolean addDevice(RegistrationPack registrationPack) {
		String deviceID = registrationPack.getAndroidID();
		String emailAddress = registrationPack.getEmailAddress();

		if (devicesMap.containsKey(deviceID)) {
			return false;
		} else {
			devicesMap.put(deviceID, new Device(deviceID));
			deviceDB.registerDevice(registrationPack);
			
			if (null != emailAddress) {
				userDetails.registerUser(registrationPack);
			}
			
			setChanged();
			notifyObservers();

			return true;
		}
	}

	/**
	 * Add a valid result to this device in the cache and the database.
	 * 
	 * @param deviceID
	 */
	public void writeValidResultSent(String deviceID) {
		
		if (devicesMap.containsKey(deviceID)) {
			Device device = devicesMap.get(deviceID);
			device.addValidResult();
			updateValidResults();
			deviceDB.writeValidResultSent(deviceID);

			String emailAddress = device.getEmailAddress();

			if (null != emailAddress) {
				userDetails.checkForAchievementMilestone(emailAddress);
			}
			
			setChanged();
			notifyObservers();
		}

	}

	/**
	 * Add an invalid result to this device in the cache and the database.
	 * 
	 * @param deviceID
	 */
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

			setChanged();
			notifyObservers();
		}

	}

	/**
	 * Helper method to determine if a device should be blacklisted.
	 * 
	 * @param deviceID
	 *            the device's unique ID.
	 * @return true if the device should be blacklisted.
	 */
	private boolean shouldBeBlacklisted(String deviceID) {
		if (devicesMap.get(deviceID).getInvalidResults() > BLACKLISTING_MIN_THRESHOLD) {
			Integer validResults = 0;

			if (devicesMap.containsKey(deviceID)) {
				validResults = devicesMap.get(deviceID).getValidResults();
			}

			Integer invalidResults = devicesMap.get(deviceID)
					.getInvalidResults();
			Integer totalResults = invalidResults + validResults;
			double percentInvalid = (invalidResults / (double)totalResults) * 100.0;

			return percentInvalid > MIN_PERCENT_INVALID_RESULTS;
		} else {
			return false;
		}
	}

	/**
	 * Determines if a device is currently blacklisted.
	 * 
	 * @param deviceID
	 *            the device's unique ID.
	 * @return true if the device is blacklisted.
	 */
	public boolean deviceIsBlacklisted(String deviceID) {
		lock.readLock().lock();

		try {
			return blacklistedDevices.contains(deviceID);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Updates a device to the latest time they were active.
	 * 
	 * @param deviceID
	 *            the device's unique ID.
	 * @param lastTimeActive
	 *            the last time active in milliseconds.
	 */
	public void updateActiveDevice(String deviceID, long latestTimeActive) {
		if (devicesMap.containsKey(deviceID)) {
			devicesMap.get(deviceID).setLastTimeActive(latestTimeActive);
		}
	}

	/**
	 * Deregister a device from the system. Deregisters the device from the
	 * cache and the database.
	 * 
	 * @param deviceID
	 *            the device's unique ID.
	 */
	public boolean deregisterDevice(String deviceID) {
		// the device details should be kept in the blacklist
		// in case they re-register
		devicesMap.remove(deviceID);
		return deviceDB.deregisterDevice(deviceID);
	}

	/**
	 * Increments the valid results counter.
	 */
	private synchronized void updateValidResults() {
		++VALID_RESULTS;		
	}

	/**
	 * Increments the invalid results counter.
	 */
	private synchronized void updateInvalidResults() {
		++INVALID_RESULTS;
	}

	/**
	 * Increments the blacklisted devices counter.
	 */
	private synchronized void updateBlacklistedDevices() {
		++NO_OF_BLACKLISTED_DEVICES;
	}

	public synchronized int numberOfDevices() {
		return devicesMap.size();
	}

	/**
	 * The number of valid results stored during this computation session.
	 * 
	 * @return the number of valid results.
	 */
	public synchronized long numberOfValidResults() {
		return VALID_RESULTS;
	}

	/**
	 * The number of invalid results during this computation session.
	 * 
	 * @return the number of invalid results.
	 */
	public synchronized long numberOfInvalidResults() {
		return INVALID_RESULTS;
	}

	/**
	 * The number of devices that are currently blacklisted.
	 * 
	 * @return the number of devices that are currently blacklisted.
	 */
	public synchronized int numberOfBlacklistedDevices() {
		return NO_OF_BLACKLISTED_DEVICES;
	}

	public synchronized void adjustAverage(int listSize,
			long timeSpentProcessing) {
		long difference = timeSpentProcessing - AVERAGE_PROCESSING_TIME;
		PROCESSING_DIVISOR += listSize;
		AVERAGE_PROCESSING_TIME += difference / PROCESSING_DIVISOR;
	}

	public synchronized long getAverageProcessingTime() {
		return AVERAGE_PROCESSING_TIME;
	}

	/**
	 * Returns the number of devices that are currently active.
	 * 
	 * @return the number of devices that are currently active.
	 */
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

	/**
	 * Helper method to determine if a device is considered active.
	 * 
	 * @param lastTimeActive
	 *            the last time the device was active (in milliseconds).
	 * @param currentTime
	 *            the current time.
	 * @return true if the device is within the active device threshold.
	 */
	private boolean deviceIsActive(long lastTimeActive, long currentTime) {
		long timeDelay = TimeUnit.MINUTES.toMillis(ACTIVE_DEVICE_THRESHOLD);
		return currentTime - lastTimeActive < timeDelay;
	}

	/**
	 * Changes the active device threshold. Converts minus numbers to natural
	 * numbers.
	 * 
	 * @param minutes
	 *            the number of minutes after which a device is considered
	 *            inactive.
	 * @return the number of devices active after the change in threshold.
	 */
	public int changeActiveDeviceThreshold(int minutes) {
		ACTIVE_DEVICE_THRESHOLD = Math.abs(minutes);
		return numberOfActiveDevices();
	}

	/**
	 * A device is allowed a certain grace period before it will be considered
	 * for blacklisting. This method changes the number of results needed before
	 * a device can be considered for blacklisting e.g. 10 results.<br>
	 * </br> This avoids a device being prematurely blacklisted for having 1
	 * invalid result and no valid results i.e. a 100% invalid result rate.
	 * 
	 * @param invalidResults
	 *            the number of invalid results needed
	 */
	public void setBlacklistingThreshold(int invalidResults) {
		BLACKLISTING_MIN_THRESHOLD = Math.abs(invalidResults);
		resetBlacklist();
		setChanged();
		notifyObservers();
	}

	/**
	 * A device will be blacklisted if more than n % of its results are invalid.
	 * This method sets that threshold. Once this method is called the list of
	 * blacklisted devices is altered to account for the change.
	 * 
	 * @param percent
	 */
	public void setMinPercentInvalidResults(int percent) {
		if (percent <= 100 && percent > 0) {
			MIN_PERCENT_INVALID_RESULTS = percent;
			resetBlacklist();
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Helper method to reset the device blacklist when the blacklisting
	 * threshold changes.
	 */
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

	/**
	 * Retrieves the map used to store device details. Used for testing purposes
	 * only.
	 * 
	 * @return
	 */
	public ConcurrentMap<String, Device> getDeviceMap() {
		return devicesMap;
	}

	/**
	 * Retrieves the list of blacklisted devices. Used for testing purposes
	 * only.
	 * 
	 * @return
	 */
	public List<String> getBlacklistedList() {
		return blacklistedDevices;
	}
	
	public void setDeviceDB(DeviceDetailsJDBC deviceDB){
		this.deviceDB = deviceDB;
	}

}
