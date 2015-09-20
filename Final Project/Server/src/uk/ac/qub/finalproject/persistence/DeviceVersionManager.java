/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;

/**
 * This is the system's point of access to information regarding a device's
 * version number.
 * 
 * @author Phil
 *
 */
public class DeviceVersionManager {

	private ConcurrentMap<Integer, List<String>> versionMap = new ConcurrentHashMap<Integer, List<String>>();
	private DeviceVersionJDBC versionDB = new DeviceVersionJDBC();

	public DeviceVersionManager() {

	}

	public DeviceVersionManager(DeviceVersionJDBC versionDB) {
		this.versionDB = versionDB;
	}

	/**
	 * Returns a reference to the map with all device version information.
	 * 
	 * @return
	 */
	public ConcurrentMap<Integer, List<String>> getVersionMap() {
		return versionMap;
	}

	/**
	 * Loads all device information in the database to the cache.
	 */
	public void loadDeviceVersions() {
		versionMap.clear();
		versionMap.putAll(versionDB.getDeviceVersions());
	}

	/**
	 * Saves the device version to the system.
	 * 
	 * @param registrationPack
	 */
	public void saveDeviceVersion(RegistrationPack registrationPack) {
		Integer versionCode = registrationPack.getVersionCode();
		String deviceID = registrationPack.getAndroidID();

		if (deviceIsStored(deviceID)) {
			updateDeviceVersion(versionCode, deviceID);
			versionDB.updateDeviceVersion(versionCode, deviceID);
		} else {
			saveDevice(versionCode, deviceID);
			versionDB.saveDeviceVersion(versionCode, deviceID);
		}

	}

	/**
	 * Returns true if a device has the correct version of the app installed.
	 * 
	 * @param versionCode
	 * @param deviceID
	 * @return
	 */
	public boolean deviceIsVersionOrAbove(Integer versionCode, String deviceID) {
		for (Integer version : versionMap.keySet()) {
			if (version >= versionCode
					&& versionMap.get(version).contains(deviceID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks to see if the device is stored in the cache.
	 * 
	 * @param deviceID
	 * @return
	 */
	private boolean deviceIsStored(String deviceID) {
		for (Integer version : versionMap.keySet()) {
			if (versionMap.get(version).contains(deviceID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Saves the device version information to the cache.
	 * 
	 * @param versionCode
	 * @param deviceID
	 */
	public void saveDevice(Integer versionCode, String deviceID) {
		if (!versionMap.containsKey(versionCode)) {
			versionMap.put(versionCode, new Vector<String>());
		}

		versionMap.get(versionCode).add(deviceID);
	}

	/**
	 * Updates the device version in the cache.
	 * 
	 * @param versionCode
	 * @param deviceID
	 */
	public void updateDeviceVersion(Integer versionCode, String deviceID) {
		for (Integer version : versionMap.keySet()) {
			versionMap.get(version).remove(deviceID);
		}

		saveDevice(versionCode, deviceID);
	}

}
