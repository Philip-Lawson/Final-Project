/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class DeviceDetailsManagerStub extends DeviceDetailsManager {
	
	private RegistrationPack registrationPack;
	
	private boolean deregisterSuccess = false;
	
	public void setDeregisterSuccess(boolean deregisterSuccess) {
		this.deregisterSuccess = deregisterSuccess;
	}
	
	@Override
	public boolean addDevice(RegistrationPack registrationPack){
		return true;
	}

	public RegistrationPack getRegistrationPack() {
		return registrationPack;
	}

	public void setRegistrationPack(RegistrationPack registrationPack) {
		this.registrationPack = registrationPack;
	}

	@Override
	public boolean deregisterDevice(String deviceID){
		return deregisterSuccess;
	}
}
