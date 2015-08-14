/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.persistence.Device;
import uk.ac.qub.finalproject.persistence.DeviceDetailsJDBC;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class DeviceDetailsJDBCStub extends DeviceDetailsJDBC {
	
	private boolean deviceDeregistered = false;
	private boolean isRegistered = false;
	private boolean validResultWritten = false;
	private boolean invalidResultWritten = false;
	
	private ConcurrentMap<String, Device> deviceMap;
	
	@Override
	public boolean deregisterDevice(String deviceID){
		return deviceDeregistered;
	}
	
	public boolean getDeviceRegistered(){
		return isRegistered;
	}
	
	public void setDeviceDeregistered(boolean deviceDeregistered){
		this.deviceDeregistered = deviceDeregistered;
	}
	
	@Override
	public ConcurrentMap<String, Device> loadDevices(){
		return deviceMap;
	}
	
	public void setDeviceMap(List<Device> devices){
		deviceMap = new ConcurrentHashMap<String, Device>();
		for (Device device : devices){
			deviceMap.put(device.getDeviceID(), device);
		}
	}
	
	public boolean registerDevice(RegistrationPack registrationPack){
		isRegistered = true;
		return isRegistered;
	}
	
	public void setIsRegistered(boolean isRegistered){
		this.isRegistered = isRegistered;
	}
	
	@Override
	public void writeInvalidResultSent(String deviceID){
		invalidResultWritten = true;
	}
	
	@Override
	public void writeValidResultSent(String deviceID){
		validResultWritten = true;
	}

	public boolean isValidResultWritten() {
		return validResultWritten;
	}

	public void setValidResultWritten(boolean validResultWritten) {
		this.validResultWritten = validResultWritten;
	}

	public boolean isInvalidResultWritten() {
		return invalidResultWritten;
	}

	public void setInvalidResultWritten(boolean invalidResultWritten) {
		this.invalidResultWritten = invalidResultWritten;
	}

}
