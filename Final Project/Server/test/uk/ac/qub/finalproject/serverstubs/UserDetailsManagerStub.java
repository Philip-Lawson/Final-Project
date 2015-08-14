/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import uk.ac.qub.finalproject.persistence.UserDetailsManager;
import uk.ac.qub.finalproject.server.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetailsManagerStub extends UserDetailsManager {

	private boolean userIsRegistered = false;
	private boolean achievementMilestoneChecked = false;

	public boolean registerUser(RegistrationPack registrationPack) {
		userIsRegistered = true;
		return userIsRegistered;
	}

	public void setUserIsRegistered(boolean userIsRegistered) {
		this.userIsRegistered = userIsRegistered;
	}

	public boolean userIsRegistered() {
		return userIsRegistered;
	}

	@Override
	public void checkForAchievementMilestone(String emailAddress) {
		achievementMilestoneChecked = true;
	}

	public boolean isAchievementMilestoneChecked() {
		return achievementMilestoneChecked;
	}

	public void setAchievementMilestoneChecked(
			boolean achievementMilestoneChecked) {
		this.achievementMilestoneChecked = achievementMilestoneChecked;
	}

}
