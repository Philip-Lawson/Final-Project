/**
 * 
 */
package uk.ac.qub.finalproject.server;

import uk.ac.qub.finalproject.persistence.Achievements;



/**
 * @author Phil
 *
 */
public abstract class AbstractClientAchievementEmailFactory {
	
	public abstract String buildEmail(Achievements type);	
	public abstract String buildEmailTitle(Achievements type);	

}
