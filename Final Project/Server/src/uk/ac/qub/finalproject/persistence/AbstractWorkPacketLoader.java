/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

/**
 * The AbstractWorkPacketLoader encapsulates the logic and work needed to create
 * work packets from a persistence layer and add them directly to the work
 * packet drawer.
 * 
 * <br>
 * </br> It is the responsibility of the implementor to write the logic for
 * retrieval of work packets within the context of the persistence system used.
 * 
 * <br>
 * </br>Work packets can be created with a specified packet ID or a default
 * auto-generated packet ID. If a specified packet ID is needed, the two
 * argument constructor should be used.
 * 
 * @author Phil
 *
 */
public abstract class AbstractWorkPacketLoader {

	/**
	 * The work packet drawer that contains the work packets to be sent to
	 * clients.
	 */
	private AbstractWorkPacketDrawer workPacketDrawer;

	/**
	 * The DAO that loads results packets from the database.
	 */
	private ResultsPacketJDBC resultsDB = new ResultsPacketJDBC();

	/**
	 * Constructs a work packet loader with a work packet drawer to load .
	 * 
	 * @param workPacketDrawer
	 *            the work packet drawer
	 */
	public AbstractWorkPacketLoader(AbstractWorkPacketDrawer workPacketDrawer) {
		this.workPacketDrawer = workPacketDrawer;
	}

	/**
	 * Loads work packets to the work packet drawer. This method should be used
	 * when the session is started. Note that it will not load work packets that
	 * have been successfully processed already. <br>
	 * The retrieval of work packets from a persistence layer is left to
	 * concrete implementations of retrieveWorkPackets().
	 */
	public final void loadWorkPackets() {
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>();
		Collection<String> resultsList = resultsDB.getAllResultsPacketID();
		Collection<IWorkPacket> processedWorkPackets = new ArrayList<IWorkPacket>(
				resultsList.size());
		

		for (IWorkPacket workPacket : retrieveInitialWorkPackets()) {
			if (!resultsList.contains(workPacket.getPacketId())) {
				workPackets.add(workPacket);
			} else {
				processedWorkPackets.add(workPacket);
			}
		}

		workPacketDrawer.addWorkPackets(workPackets);
		workPacketDrawer.addPacketsToProcessedList(processedWorkPackets);
	}

	/**
	 * Loads additional work packets to the packet drawer. The retrieval of
	 * additional work packets from a persistence layer is left to concrete
	 * implementations of retrieveAdditionalWorkPackets().
	 */
	public final void loadAdditionalWorkPackets() {
		workPacketDrawer.addWorkPackets(retrieveAdditionalWorkPackets());
	}

	/**
	 * This method encapsulates the logic to return the initial collection of
	 * work packets when starting the server.
	 * 
	 * 
	 * @return a collection of work packets.
	 */
	protected abstract Collection<IWorkPacket> retrieveInitialWorkPackets();

	/**
	 * The method encapsulates the logic for returning an additional collection
	 * of work packets should the user to decide to add more work to the system.
	 * 
	 * @return
	 */
	protected abstract Collection<IWorkPacket> retrieveAdditionalWorkPackets();

}
