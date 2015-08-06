/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.server.ClientAchievementEmailSender;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetails {

	private DeviceDetailsManager deviceManager;
	private UserDetailsJDBC userDB;
	private Collection<String> userSet = new TreeSet<String>();
	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();
	private ClientAchievementEmailSender emailSender = new ClientAchievementEmailSender();

	public Collection<String> retrieveDedicatedUserEmails() {
		return null;
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
		if (emailValidationStrategy.emailIsValid(emailAddress)) {
			userSet.add(emailAddress);
		}

		return true;
	}

	public void checkForAchievementMilestone(String emailAddress) {
		if (emailValidationStrategy.emailIsValid(emailAddress)) {
			int resultsProcessed = userDB.getUserValidResults(emailAddress);

			if (resultsProcessed == Achievements.GOLD.getAwardThreshold()) {
				emailSender.sendEmail(emailAddress, Achievements.GOLD);
			} else if (resultsProcessed == Achievements.SILVER
					.getAwardThreshold()) {
				emailSender.sendEmail(emailAddress, Achievements.SILVER);
			} else if (resultsProcessed == Achievements.BRONZE
					.getAwardThreshold()) {
				emailSender.sendEmail(emailAddress, Achievements.BRONZE);
			}
		}

	}

	public boolean deregisterUser(String emailAddress) {		
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
