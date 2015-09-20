/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import uk.ac.qub.finalproject.persistence.Achievements;



/**
 * This class encapsulates the logic for creating the appropriate email to send
 * to a client once they have achieved a certain processing milestone. 
 * 
 * @author Phil
 *
 */
public abstract class AbstractClientAchievementEmailFactory {
	
	public abstract String buildEmail(Achievements type);	
	public abstract String buildEmailTitle(Achievements type);	

}
