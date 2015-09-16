/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;
import uk.ac.qub.finalproject.persistence.UserDetailsJDBC;

/**
 * @author Phil
 *
 */
public class UserDetailsJDBCStub extends UserDetailsJDBC {

	private boolean emailChanged;
	private boolean getUserEmailsCalled = false;
	private boolean emailRegistered;
	private int validResults = 0;
	private Collection<String> devices = new ArrayList<String>();
	
	public void setEmailChanged(boolean emailChanged){
		this.emailChanged = emailChanged;
	}
	
	@Override
	public boolean changeEmailAddress(RegistrationPack registrationPack){
		return emailChanged;
	}
	
	@Override
	public Collection<String> getAllUserEmails(){
		getUserEmailsCalled = true;
		return null;
	}
	
	@Override
	public Collection<String> getUserDevices(String deviceID){
		if (devices.contains(deviceID)){
			return devices;
		}
		
		return null;
	}
	
	@Override
	public int getUserValidResults(String deviceID){
		return validResults;
	}
	
	@Override
	public boolean registerEmail(RegistrationPack registrationPack){
		return emailRegistered;
	}
	
	public void setEmailRegistered(boolean emailRegistered){
		this.emailRegistered = emailRegistered;
	}
	
	public void setValidResults(int validResults){
		this.validResults = validResults;
	}

	public boolean isGetUserEmailsCalled() {
		return getUserEmailsCalled;
	}

	public void setGetUserEmailsCalled(boolean getUserEmailsCalled) {
		this.getUserEmailsCalled = getUserEmailsCalled;
	}
}
