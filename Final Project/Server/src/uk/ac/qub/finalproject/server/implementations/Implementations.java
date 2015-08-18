/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import uk.ac.qub.finalproject.calculationclasses.DummyGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.DummyValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;
import uk.ac.qub.finalproject.persistence.DummyWorkPacketLoader;
import uk.ac.qub.finalproject.server.AbstractClientAchievementEmailFactory;
import uk.ac.qub.finalproject.server.ClientAchievementEmailFactory;

/**
 * @author Phil
 *
 */
public class Implementations {
			
	public static AbstractWorkPacketLoader getWorkPacketLoader(AbstractWorkPacketDrawer workPacketDrawer){
		return new DummyWorkPacketLoader(workPacketDrawer);
	}
	
	public static AbstractResultsTransferManager getResultsTransferManager(){
		return null;
	}
		
	
	public static IValidationStrategy getValidationStrategy(){
		return new DummyValidationStrategy();
	}
	
	public static IGroupValidationStrategy getGroupValidationStrategy(){
		return new DummyGroupValidationStrategy();
	}
	
	public static AbstractClientAchievementEmailFactory getEmailFactory(){
		return new ClientAchievementEmailFactory();
	}
	
	public static String getEmailAddress(){
		return null;
	}
	
	public static String getEmailPassword(){
		return null;
	}
	
	public static String getServerScreenTitle(){
		return "Volunteer Science";
	}
}
