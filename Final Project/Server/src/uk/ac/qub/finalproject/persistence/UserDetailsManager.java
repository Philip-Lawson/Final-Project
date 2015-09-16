/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import java.util.Observable;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;
import uk.ac.qub.finalproject.server.ClientAchievementEmailSender;

/**
 * @author Phil
 *
 */
public class UserDetailsManager extends Observable {

	private DeviceDetailsManager deviceManager;
	private UserDetailsJDBC userDB = new UserDetailsJDBC();

	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();
	private ClientAchievementEmailSender emailSender = new ClientAchievementEmailSender();

	public UserDetailsManager() {

	}

	public UserDetailsManager(DeviceDetailsManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public UserDetailsManager(DeviceDetailsManager deviceDetailsManager,
			UserDetailsJDBC userDB) {
		this(deviceDetailsManager);
		this.userDB = userDB;
	}

	public void setDeviceManager(DeviceDetailsManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public void setEmailSender(ClientAchievementEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * Returns a cllection f all the user emails stored in the database.
	 * 
	 * @return
	 */
	public Collection<String> retrieveDedicatedUserEmails() {
		return userDB.getAllUserEmails();
	}

	/**
	 * Changes a users email address.
	 * 
	 * @param registrationPack
	 * @return true if the email is valid and the email was changed
	 *         successfully. Returns false if the email is invalid or there was
	 *         an issue with the database.
	 */
	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		if (emailValidationStrategy.emailIsValid(registrationPack
				.getEmailAddress())) {
			return userDB.changeEmailAddress(registrationPack);
		} else if (registrationPack.getEmailAddress().equals("")){
			return userDB.deleteEmailAddress(registrationPack);
		} else {
			return true;
		}
	}

	/**
	 * Registers a user with a valid email address.
	 * 
	 * @param registrationPack
	 * @return true if the email address is valid and there were n issues with
	 *         the database connection.
	 */
	public boolean registerUser(RegistrationPack registrationPack) {
		if (emailValidationStrategy.emailIsValid(registrationPack
				.getEmailAddress())) {
			return userDB.registerEmail(registrationPack);
		} else {
			return false;
		}
	}

	/**
	 * Checks to see if the user should be sent a congratulatory email. If so
	 * they will be sent an email appropriate to the milestone they have
	 * achieved e.g. processing 1000 packets.
	 * 
	 * @param emailAddress
	 */
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

	/**
	 * Method not used yet. Designed to allow a user to deregister all their
	 * devices in one go.
	 * 
	 * @param emailAddress
	 * @return
	 */
	public boolean deregisterUser(String emailAddress) {
		Collection<String> userDevices = userDB.getUserDevices(emailAddress);

		if (null == userDevices) {
			return false;
		} else {
			boolean success = true;

			for (String deviceID : userDevices) {
				if (!deviceManager.deregisterDevice(deviceID)) {
					success = false;
				}
			}

			return success;
		}
	}

}
