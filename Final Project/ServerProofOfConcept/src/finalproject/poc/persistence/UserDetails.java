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

	public Collection<String> retrieveDedicatedUserEmails(){
		return null;
	}
	
	public boolean changeEmailAddress(RegistrationPack registrationPack){
		return true;
	}
	
	public boolean deregisterUser(String emailAddress){
		return true;
	}
		
}
