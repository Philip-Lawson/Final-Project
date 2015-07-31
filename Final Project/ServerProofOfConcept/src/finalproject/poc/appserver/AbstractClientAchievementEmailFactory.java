/**
 * 
 */
package finalproject.poc.appserver;

import finalproject.poc.persistence.Achievements;

/**
 * @author Phil
 *
 */
public abstract class AbstractClientAchievementEmailFactory {
	
	public abstract String buildEmail(Achievements type);	
	public abstract String buildEmailTitle(Achievements type);	

}
