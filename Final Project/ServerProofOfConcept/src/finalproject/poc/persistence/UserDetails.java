/**
 * 
 */
package finalproject.poc.persistence;

import java.util.Collection;

import finalproject.poc.appserver.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetails {
	
	private DeviceDetailsManager deviceManager;
	private UserDetailsJDBC userDB;
	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();

	public Collection<String> retrieveDedicatedUserEmails(){
		return null;
	}
	
	public boolean changeEmailAddress(RegistrationPack registrationPack){
		if (emailValidationStrategy.emailIsValid(registrationPack.getEmailAddress())){
			return userDB.changeEmailAddress(registrationPack);
		} else {
			return false;
		}
	}
	
	public boolean deregisterUser(String emailAddress){
		//TODO JDBC code catch exception that returns false
		Collection<String> userDevices = userDB.getUserDevices(emailAddress);
		
		if (null == userDevices){
			return false;
		} else {
			for (String deviceID : userDevices){
				deviceManager.deregisterDevice(deviceID);
			}
			
			return true;
		}		
	}
		
}
