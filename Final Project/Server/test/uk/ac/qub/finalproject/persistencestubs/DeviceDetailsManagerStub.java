/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class DeviceDetailsManagerStub extends DeviceDetailsManager {

	private RegistrationPack registrationPack;

	private boolean deregisterSuccess = false;
	private boolean validResultSent;
	private boolean invalidResultSent;
	private boolean averageAdjusted;
	private String deviceID;

	public void setDeregisterSuccess(boolean deregisterSuccess) {
		this.deregisterSuccess = deregisterSuccess;
	}

	@Override
	public void adjustAverage(int list, long time) {
		averageAdjusted = true;
	}

	@Override
	public boolean addDevice(RegistrationPack registrationPack) {
		return true;
	}

	public RegistrationPack getRegistrationPack() {
		return registrationPack;
	}

	public void setRegistrationPack(RegistrationPack registrationPack) {
		this.registrationPack = registrationPack;
	}

	@Override
	public boolean deregisterDevice(String deviceID) {
		return deregisterSuccess;
	}

	@Override
	public void writeValidResultSent(String deviceID) {
		this.deviceID = deviceID;
		validResultSent = true;
	}

	@Override
	public void writeInvalidResultSent(String deviceID) {
		this.deviceID = deviceID;
		invalidResultSent = true;
	}

	public boolean isValidResultSent() {
		return validResultSent;
	}

	public void setValidResultSent(boolean validResultSent) {
		this.validResultSent = validResultSent;
	}

	public boolean isInvalidResultSent() {
		return invalidResultSent;
	}

	public void setInvalidResultSent(boolean invalidResultSent) {
		this.invalidResultSent = invalidResultSent;
	}

	public boolean isAverageAdjusted() {
		return averageAdjusted;
	}

	public void setAverageAdjusted(boolean averageAdjusted) {
		this.averageAdjusted = averageAdjusted;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}
