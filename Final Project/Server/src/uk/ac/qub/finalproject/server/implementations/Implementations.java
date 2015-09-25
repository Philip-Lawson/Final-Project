/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;
import uk.ac.qub.finalproject.server.networking.AbstractClientAchievementEmailFactory;

/**
 * This is the virtual configuration file of the server. All domain
 * implementation classes, server details and database details are accessed from
 * here.
 * 
 * @author Phil
 *
 */
public class Implementations {

	public static AbstractWorkPacketLoader getWorkPacketLoader(
			AbstractWorkPacketDrawer workPacketDrawer) {
		return new DummyWorkPacketLoader(workPacketDrawer);
	}

	public static AbstractResultsTransferManager getResultsTransferManager() {
		return new DummyResultsTransferManager();
	}

	public static boolean groupValidationNeeded() {
		return false;
	}

	public static IValidationStrategy getValidationStrategy() {
		return new DummyValidationStrategy();
	}

	public static IGroupValidationStrategy getGroupValidationStrategy() {
		return new DummyGroupValidationStrategy();
	}

	public static AbstractClientAchievementEmailFactory getEmailFactory() {
		return new ClientAchievementEmailFactory();
	}

	public static int getServerPort() {
		return 12346;
	}

	public static String getDatabaseURL() {
		return "jdbc:mysql://web2.eeecs.qub.ac.uk/40143289";
	}

	public static String getDatabaseUser() {
		return "40143289";
	}

	public static String getDatabasePassword() {
		return "FMA4237";
	}

	public static String getEmailAddress() {
		return "citizensciencedemo@gmail.com";
	}

	public static String getEmailPassword() {
		return "demoapplicat10n";
	}

	public static String getServerScreenTitle() {
		return "Citizen Science";
	}

	/**
	 * This must be changed before use. Otherwise files stored in the database
	 * will not be secure. Ensure that it contains 16 bytes.
	 * 
	 * @return
	 */
	public static byte[] getSecretKeySpec() {
		return new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, };
	}
}
