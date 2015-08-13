/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.server.RegistrationPack;

/**
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
	
	public ConcurrentMap<Integer, List<String>> getVersionMap(){
		return versionMap;
	}
	
	public void loadDeviceVersions(){
		versionMap.clear();
		versionMap.putAll(versionDB.getDeviceVersions());
	}

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

	public boolean deviceIsVersionOrAbove(Integer versionCode, String deviceID) {
		for (Integer version : versionMap.keySet()) {
			if (version >= versionCode
					&& versionMap.get(version).contains(deviceID)) {
				return true;
			}
		}

		return false;
	}

	private boolean deviceIsStored(String deviceID) {
		for (Integer version : versionMap.keySet()) {
			if (versionMap.get(version).contains(deviceID)) {
				return true;
			}
		}

		return false;
	}

	public void saveDevice(Integer versionCode, String deviceID) {
		if (!versionMap.containsKey(versionCode)) {
			versionMap.put(versionCode, new Vector<String>());
		}

		versionMap.get(versionCode).add(deviceID);
	}

	public void updateDeviceVersion(Integer versionCode, String deviceID) {
		for (Integer version : versionMap.keySet()) {
			versionMap.get(version).remove(deviceID);
		}
		
		saveDevice(versionCode, deviceID);		
	}

}
