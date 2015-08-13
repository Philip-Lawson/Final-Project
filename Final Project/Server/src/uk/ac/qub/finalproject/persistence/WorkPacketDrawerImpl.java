/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;

/**
 * @author Phil
 *
 */
public class WorkPacketDrawerImpl extends AbstractWorkPacketDrawer {

	private static int PACKETS_PER_LIST = 5;
	private static final int MIN_PACKETS_PER_LIST = 5;
	private static int TIMES_TO_SEND_PACKET_LIST = 5;
	private static int TIMES_PACKET_LIST_SENT = -1;

	private WorkPacketJDBC workPacketDB = new WorkPacketJDBC();
	private List<IWorkPacket> unprocessedWorkPacketList = new Vector<IWorkPacket>();
	private WorkPacketList currentPacketList;
	private Map<String, IWorkPacket> sentPacketsMap = new ConcurrentHashMap<String, IWorkPacket>();

	public WorkPacketDrawerImpl() {

	}
	
	public WorkPacketDrawerImpl(WorkPacketJDBC workPacketDB) {
		this.workPacketDB = workPacketDB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#addWorkPackets
	 * (java.util.Collection)
	 */
	@Override
	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		List<IWorkPacket> validPackets = new ArrayList<IWorkPacket>(
				workPackets.size());

		for (IWorkPacket workPacket : workPackets) {
			if (workPacketIsValid(workPacket))
				validPackets.add(workPacket);
		}

		unprocessedWorkPacketList.addAll(validPackets);

		setChanged();
		notifyObservers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#getNextWorkPacket
	 * ()
	 */
	@Override
	public synchronized WorkPacketList getNextWorkPacket() {
		if (TIMES_PACKET_LIST_SENT < TIMES_TO_SEND_PACKET_LIST
				&& TIMES_PACKET_LIST_SENT > 0) {
			++TIMES_PACKET_LIST_SENT;
		} else {
			transferWorkPackets();
			TIMES_PACKET_LIST_SENT = 1;
		}

		return currentPacketList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#setNumberOfCopies
	 * (int)
	 */
	@Override
	public synchronized void setNumberOfCopies(int numberOfCopies)
			throws IllegalArgumentException {
		if (numberOfCopies < 5 || numberOfCopies > 100) {
			throw new IllegalArgumentException("Invalid Number of Copies");
		} else {
			TIMES_TO_SEND_PACKET_LIST = numberOfCopies;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#setPacketsPerList
	 * (int)
	 */
	@Override
	public synchronized void setPacketsPerList(int packetsPerList) {
		if (packetsPerList >= MIN_PACKETS_PER_LIST)
			PACKETS_PER_LIST = packetsPerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#
	 * numberOfPacketsRemaining()
	 */
	@Override
	public int numberOfPacketsRemaining() {
		int packetsToTransfer = unprocessedWorkPacketList.size()
				* TIMES_TO_SEND_PACKET_LIST;
		int remainderOfCurrentList = (TIMES_TO_SEND_PACKET_LIST - TIMES_PACKET_LIST_SENT)
				* PACKETS_PER_LIST;

		return packetsToTransfer + remainderOfCurrentList;
	}

	@Override
	public int numberOfDistinctWorkPackets() {
		return unprocessedWorkPacketList.size() + sentPacketsMap.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#hasWorkPackets
	 * ()
	 */
	@Override
	public synchronized boolean hasWorkPackets() {
		return currentPacketList.size() > 0
				|| TIMES_PACKET_LIST_SENT < TIMES_TO_SEND_PACKET_LIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#getInitialData
	 * (java.lang.String)
	 */
	@Override
	public IWorkPacket getInitialData(String packetID) {
		if (sentPacketsMap.containsKey(packetID)) {
			return sentPacketsMap.get(packetID);
		} else {
			// just in case there's a problem with the map
			return workPacketDB.getIndividualWorkPacket(packetID);
		}
	}

	/**
	 * Helper method to transfer work packets. This should always be called from
	 * within a synchronized method.
	 */
	private void transferWorkPackets() {
		currentPacketList.clear();
		int count = 0;

		while (count < PACKETS_PER_LIST && unprocessedWorkPacketList.size() > 0) {
			IWorkPacket workPacket = unprocessedWorkPacketList.remove(0);
			currentPacketList.add(workPacket);
			sentPacketsMap.putIfAbsent(workPacket.getPacketId(), workPacket);
		}

	}

	@Override
	public void reloadIncompletedWorkPackets() {
		addWorkPackets(workPacketDB.getIncompleteWorkPackets());
	}

}
