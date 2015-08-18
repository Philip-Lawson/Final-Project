/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;

/**
 * @author Phil
 *
 */
public class WorkPacketDrawerImpl extends AbstractWorkPacketDrawer {

	public static final int DEFAULT_PACKETS_PER_LIST = 10;
	public static final int DEFAULT_TIMES_TO_SEND_PACKET_LIST = 10;	
	private static final int MIN_PACKETS_PER_LIST = 5;
	
	private int PACKETS_PER_LIST = DEFAULT_PACKETS_PER_LIST;
	private int TIMES_TO_SEND_PACKET_LIST = DEFAULT_TIMES_TO_SEND_PACKET_LIST;
	private int TIMES_PACKET_LIST_SENT = 0;

	private WorkPacketJDBC workPacketDB = new WorkPacketJDBC();
	private Map<String, IWorkPacket> unprocessedWorkPacketMap = new ConcurrentHashMap<String, IWorkPacket>();
	private WorkPacketList currentPacketList = new WorkPacketList();
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

		Collection<IWorkPacket> tempPacketList = new ArrayList<IWorkPacket>(workPackets.size());

		for (IWorkPacket workPacket : workPackets) {
			if (workPacketIsValid(workPacket)) {
				unprocessedWorkPacketMap.putIfAbsent(workPacket.getPacketId(),
						workPacket);
				sentPacketsMap.remove(workPacket.getPacketId());
				tempPacketList.add(workPacket);
			}
		}

		if (tempPacketList.size() > 0) {
			workPacketDB.addWorkPackets(tempPacketList);
			setChanged();
			notifyObservers();
		}

		if (!hasWorkPackets()) {
			synchronized (this) {
				transferWorkPackets();
				TIMES_PACKET_LIST_SENT = 0;
			}
		}

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
		// if the packet should be sent only one more time the next packet list
		// needs to be prepared. I don't want to send the wrong packet list, so
		// the current list is copied to a placeholder list while the packets
		// are being transferred.
		if (TIMES_PACKET_LIST_SENT >= TIMES_TO_SEND_PACKET_LIST - 1) {
			WorkPacketList listToReturn = new WorkPacketList();
			listToReturn.addAll(currentPacketList);

			transferWorkPackets();
			TIMES_PACKET_LIST_SENT = 0;
			return listToReturn;
		} else {
			++TIMES_PACKET_LIST_SENT;
			return currentPacketList;
		}		
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
		if (packetsPerList >= MIN_PACKETS_PER_LIST) {
			PACKETS_PER_LIST = packetsPerList;
		}			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#
	 * numberOfPacketsRemaining()
	 */
	@Override
	public int numberOfPacketsRemaining() {
		int packetsToTransfer = unprocessedWorkPacketMap.size()
				* TIMES_TO_SEND_PACKET_LIST;
		int remainderOfCurrentList = (TIMES_TO_SEND_PACKET_LIST - TIMES_PACKET_LIST_SENT)
				* currentPacketList.size();

		return packetsToTransfer + remainderOfCurrentList;
	}

	@Override
	public int numberOfDistinctWorkPackets() {
		return unprocessedWorkPacketMap.size() + sentPacketsMap.size();
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
		// as access to the current packet list is synchronized it will only
		// ever be empty when there are no work packets left
		return currentPacketList.size() > 0;
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
		} else if (unprocessedWorkPacketMap.containsKey(packetID)) {
			return unprocessedWorkPacketMap.get(packetID);
		} else {
			// just in case there's a problem with the cache
			return workPacketDB.getIndividualWorkPacket(packetID);
		}
	}

	/**
	 * Helper method to transfer work packets. This should always be called from
	 * within a synchronized method or with a thread-safe collection.
	 */
	public void transferWorkPackets() {
		currentPacketList.clear();
		int count = 0;

		Iterator<String> it = unprocessedWorkPacketMap.keySet().iterator();

		while (count < PACKETS_PER_LIST && it.hasNext()) {
			IWorkPacket workPacket = unprocessedWorkPacketMap.remove(it.next());
			currentPacketList.add(workPacket);
			sentPacketsMap.putIfAbsent(workPacket.getPacketId(), workPacket);
			count++;
		}
	}

	@Override
	public void reloadIncompletedWorkPackets() {
		addWorkPackets(workPacketDB.getIncompleteWorkPackets());
	}

	/**
	 * Returns the current packet list. Used fr testing purposes.
	 * 
	 * @return
	 */
	public WorkPacketList getCurrentWorkPacketList() {
		return currentPacketList;
	}

	/**
	 * Returns the unprocessed work map. Used for testing purposes.
	 * 
	 * @return
	 */
	public Map<String, IWorkPacket> getUnprocessedWorkPacketMap() {
		return unprocessedWorkPacketMap;
	}

	/**
	 * Returns the sent packets map. Used fr testing purposes.
	 * 
	 * @return
	 */
	public Map<String, IWorkPacket> getSentPacketsMap() {
		return sentPacketsMap;
	}

}
