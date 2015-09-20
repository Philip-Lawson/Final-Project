/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import uk.ac.qub.finalproject.persistence.Achievements;
import uk.ac.qub.finalproject.server.networking.AbstractClientAchievementEmailFactory;

/**
 * This class encapsulates the logic for creating the appropriate email to send
 * to a client once they have achieved a certain processing milestone. 
 * 
 * @author Phil
 *
 */
public class ClientAchievementEmailFactory extends
		AbstractClientAchievementEmailFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.AbstractClientAchievementEmailFactory#
	 * buildEmail(uk.ac.qub.finalproject.persistence.Achievements)
	 */
	@Override
	public String buildEmail(Achievements type) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.AbstractClientAchievementEmailFactory#
	 * buildEmailTitle(uk.ac.qub.finalproject.persistence.Achievements)
	 */
	@Override
	public String buildEmailTitle(Achievements type) {
		// TODO Auto-generated method stub
		return null;
	}

}
