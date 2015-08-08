/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import java.util.Observable;

import uk.ac.qub.finalproject.server.ClientAchievementEmailSender;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetailsManager extends Observable {

	private DeviceDetailsManager deviceManager;
	private UserDetailsJDBC userDB = new UserDetailsJDBC();

	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();
	private ClientAchievementEmailSender emailSender = new ClientAchievementEmailSender();
	private static int NUM_USERS_WITH_EMAIL = 0;

	public UserDetailsManager() {

	}

	public UserDetailsManager(DeviceDetailsManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public void setDeviceManager(DeviceDetailsManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public synchronized int getNumUsersWithEmail() {
		return NUM_USERS_WITH_EMAIL;
	}

	private synchronized void addUser() {
		NUM_USERS_WITH_EMAIL++;
		setChanged();
		notifyObservers();
	}

	private synchronized void removeUser() {
		NUM_USERS_WITH_EMAIL--;
		setChanged();
		notifyObservers();
	}

	public Collection<String> retrieveDedicatedUserEmails() {
		return userDB.getAllUserEmails();
	}

	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		if (emailValidationStrategy.emailIsValid(registrationPack
				.getEmailAddress())) {
			return userDB.changeEmailAddress(registrationPack);
		} else {
			return false;
		}
	}

	public boolean registerUser(RegistrationPack registrationPack) {
		if (userDB.registerEmail(registrationPack)) {
			addUser();
			return true;
		} else {
			return false;
		}
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
		Collection<String> userDevices = userDB.getUserDevices(emailAddress);

		if (null == userDevices) {
			return false;
		} else {
			for (String deviceID : userDevices) {
				deviceManager.deregisterDevice(deviceID);
			}
			
			removeUser();
			return true;
		}
	}

}
