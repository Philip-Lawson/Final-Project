/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import java.util.Observable;

import uk.ac.qub.finalproject.server.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.server.calculationclasses.WorkPacketList;

/**
 * This is the abstract representation of a class that stores the work packets
 * to be sent out. The logic to retrieve work packets, add work packets, set the
 * number of copies of each work packet to be sent out is encapsulated within
 * this class and must be implemented.
 * 
 * @author Phil
 *
 */
public abstract class AbstractWorkPacketDrawer extends Observable {

	/**
	 * Appends a collection of work packets to the list of work packets stored
	 * in the packet drawer. Adds the work packets in the same priority they
	 * were entered into the collection.
	 * 
	 * @param workPackets
	 *            the collection of work packets to be appended.
	 */
	public abstract void addWorkPackets(Collection<IWorkPacket> workPackets);

	/**
	 * Retrieves the next work packet from the drawer, taking into account the
	 * number of copies of each work packet to be sent out.
	 * 
	 * @return the next list of work packets to be sent.
	 * @:throws NullPointerException if the drawer is accessed when it doesn't
	 *          contain any work packets.
	 */
	public abstract WorkPacketList getNextWorkPacket();

	/**
	 * Sets the number of copies of each work packet to be sent out. This can be
	 * used to change the number of copies to be sent out dynamically as well as
	 * initially.
	 * 
	 * @param numberOfCopies
	 *            the number of copies of each work packet to be sent out.
	 * @throws IllegalArgumentException
	 *             if the number of copies is not within the maximum and minimum
	 *             threshold set.
	 */
	public abstract void setNumberOfCopies(int numberOfCopies)
			throws IllegalArgumentException;

	/**
	 * Sets the number of work packets to be sent out in each transmission to a
	 * client device.
	 * 
	 * @param packetsPerList
	 *            the number of work packets in each list.
	 */
	public abstract void setPacketsPerList(int packetsPerList);

	/**
	 * Returns the number of work packets that still need to be sent out. This
	 * must account the number of copies of each work packet to be sent out.
	 * 
	 * @return the number of work packets that still need to be sent out.
	 */
	public abstract int numberOfPacketsRemaining();

	/**
	 * Returns the number of individual work packets loaded.
	 * 
	 * @return the number of individual packets loaded.
	 */
	public abstract int numberOfDistinctWorkPackets();

	/**
	 * Determines whether there are any packets remaining to be sent.
	 * 
	 * @return true if there are still packets to be sent.
	 */
	public abstract boolean hasWorkPackets();

	/**
	 * Returns the work packet referenced by the packet ID.
	 * 
	 * @param packetID
	 *            the unique ID of the work packet.
	 * @return the initial data
	 */
	public abstract IWorkPacket getInitialData(String packetID);

	/**
	 * Helper method to confirm that the workPacket is not null and that it has
	 * data and a packet ID
	 * 
	 * @param workPacket
	 *            the work packet that must be validated
	 * @return true if the work packet is not null and has data and a packet ID
	 */
	protected final boolean workPacketIsValid(IWorkPacket workPacket) {
		return null != workPacket && null != workPacket.getInitialData()
				&& null != workPacket.getPacketId();
	}

}
