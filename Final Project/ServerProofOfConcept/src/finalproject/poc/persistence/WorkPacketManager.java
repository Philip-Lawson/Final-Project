/**
 * 
 */
package finalproject.poc.persistence;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.WorkPacketList;

/**
 * Concrete implementation of the AbstractWorkPacketDrawer. <br>
 * </br>s
 * 
 * Uses a reentrant read and write lock to synchronise read and write access to
 * the work packet list. The fairness setting has been set to true to avoid
 * unnecessary delays to a thread that wishes to send a work packet to a client.
 * 
 * @author Phil
 *
 */
public class WorkPacketManager extends AbstractWorkPacketDrawer {

	private static int DEFAULT_NUMBER_OF_COPIES = 6;
	private static int MIN_NUMBER_OF_COPIES = 3;
	private static int MAX_NUMBER_OF_COPIES = 100;

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private Deque<IWorkPacket> workPacketList = new ArrayDeque<IWorkPacket>();
	private int numberOfCopies = DEFAULT_NUMBER_OF_COPIES;
	private int packetsPerList = 5;
	private int timesCurrentPacketSent = numberOfCopies;
	private WorkPacketList currentWorkPacketList;

	private WorkPacketJDBC workPacketDB;

	@Override
	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		lock.writeLock().lock();

		List<IWorkPacket> validWorkPackets = new ArrayList<IWorkPacket>(
				workPackets.size());

		try {
			for (IWorkPacket workPacket : workPackets) {
				if (workPacketIsValid(workPacket)) {
					workPacketList.addLast(workPacket);
					validWorkPackets.add(workPacket);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}

		workPacketDB.addWorkPackets(validWorkPackets);
	}

	@Override
	public WorkPacketList getNextWorkPacket() {
		lock.writeLock().lock();

		try {
			if (timesCurrentPacketSent >= numberOfCopies) {

				// loading method doesn't need to be synchronized as it
				// is only called from this method. syncing it could cause
				// deadlock.
				currentWorkPacketList = loadNextPacketList();
				timesCurrentPacketSent = 1;
			} else {
				timesCurrentPacketSent++;
			}

			return currentWorkPacketList;
		} finally {
			lock.writeLock().unlock();
		}

	}

	@Override
	public void setNumberOfCopies(int numberOfCopies)
			throws IllegalArgumentException {
		if (numberOfCopies < MIN_NUMBER_OF_COPIES
				|| numberOfCopies > MAX_NUMBER_OF_COPIES) {
			throw new IllegalArgumentException(
					"Invalid number of work packet copies");
		} else {
			this.numberOfCopies = numberOfCopies;
		}
	}

	@Override
	public int numberOfPacketsRemaining() {
		lock.readLock().lock();

		try {
			// the multiple of packets within the list left to send and the
			// number of times the current packet should be sent
			return (workPacketList.size() * numberOfCopies)
					+ (numberOfCopies - timesCurrentPacketSent);
		} finally {
			lock.readLock().unlock();
		}

	}

	@Override
	public boolean hasWorkPackets() {
		return numberOfPacketsRemaining() > 0;
	}

	@Override
	public void setPacketsPerList(int packetsPerList) {
		if (packetsPerList > 0) {
			this.packetsPerList = packetsPerList;
		}
	}

	/**
	 * Helper method to load the next work packet list. This is currently called
	 * from the middle of a synchronized method. If that changes this method
	 * will need to be synchronized!
	 * 
	 * @return the next work packet list.
	 */
	private WorkPacketList loadNextPacketList() {

		currentWorkPacketList.clear();

		for (int count = 0; count < packetsPerList; count++) {
			if (!workPacketList.isEmpty()) {
				currentWorkPacketList.add(workPacketList.removeFirst());
			} else {
				break;
			}
		}

		return currentWorkPacketList;
	}

	public void loadIncompleteWorkPackets() {
		Collection<IWorkPacket> workPackets = workPacketDB
				.getIncompleteWorkPackets();
		
		lock.writeLock().lock();

		try {
			for (IWorkPacket workPacket : workPackets) {
				if (workPacketIsValid(workPacket)) 
					workPacketList.addLast(workPacket);					
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

}
