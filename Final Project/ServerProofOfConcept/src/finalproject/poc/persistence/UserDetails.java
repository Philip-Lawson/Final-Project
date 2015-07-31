/**
 * 
 */
package finalproject.poc.persistence;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import finalproject.poc.appserver.ClientAchievementEmailSender;
import finalproject.poc.appserver.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetails {

	private DeviceDetailsManager deviceManager;
	private UserDetailsJDBC userDB;
	private ConcurrentMap<String, Integer> usersMap = new ConcurrentHashMap<String, Integer>();
	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();
	private ClientAchievementEmailSender emailSender = new ClientAchievementEmailSender();

	public Collection<String> retrieveDedicatedUserEmails() {
		return usersMap.keySet();
	}

	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		if (emailValidationStrategy.emailIsValid(registrationPack
				.getEmailAddress())) {
			return userDB.changeEmailAddress(registrationPack);
		} else {
			return false;
		}
	}

	public boolean registerUser(String emailAddress) {
		usersMap.putIfAbsent(emailAddress, 0);
		return true;
	}

	public void checkForAchievementMilestone(String emailAddress) {
		int resultsProcessed = userDB.getUserValidResults(emailAddress);		
		
		if (resultsProcessed == Achievements.GOLD.getAwardThreshold()) {
			emailSender.sendEmail(emailAddress, Achievements.GOLD);
		} else if (resultsProcessed == Achievements.SILVER.getAwardThreshold()) {
			emailSender.sendEmail(emailAddress, Achievements.SILVER);
		} else if (resultsProcessed == Achievements.BRONZE.getAwardThreshold()) {
			emailSender.sendEmail(emailAddress, Achievements.BRONZE);
		}

	}

	public boolean deregisterUser(String emailAddress) {
		// TODO JDBC code catch exception that returns false
		Collection<String> userDevices = userDB.getUserEmails(emailAddress);

		if (null == userDevices) {
			return false;
		} else {
			for (String deviceID : userDevices) {
				deviceManager.deregisterDevice(deviceID);
			}

			return true;
		}
	}

}
