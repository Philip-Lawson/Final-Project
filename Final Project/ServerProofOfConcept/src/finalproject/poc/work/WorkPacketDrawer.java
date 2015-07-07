/**
 * 
 */
package finalproject.poc.work;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.WorkPacketList;

/**
 * Concrete implementation of the AbstractWorkPacketDrawer.
 * <br></br>s
 * 
 * Uses a reentrant read and write lock to synchronise read and write access to
 * the work packet list. The fairness setting has been set to true to avoid
 * unnecessary delays to a thread that wishes to send a work packet to a client.
 * 
 * @author Phil
 *
 */
public class WorkPacketDrawer extends AbstractWorkPacketDrawer {

	private static int DEFAULT_NUMBER_OF_COPIES = 6;
	private static int MIN_NUMBER_OF_COPIES = 3;
	private static int MAX_NUMBER_OF_COPIES = 100;

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private Deque<IWorkPacket> workPacketList = new ArrayDeque<IWorkPacket>();
	private int numberOfCopies = DEFAULT_NUMBER_OF_COPIES;
	private int packetsPerList = 5;
	private int timesCurrentPacketSent = numberOfCopies;	
	private WorkPacketList currentWorkPacketList;

	@Override
	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
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
	
	@Override
	public WorkPacketList getNextWorkPacket(){
		// TODO work out method to avoid pulling from an empty list
		lock.writeLock().lock();
		
		try {
			if (timesCurrentPacketSent >= numberOfCopies) {
				currentWorkPacketList = loadNextPacketList();
				timesCurrentPacketSent = 1;
			} else {
				timesCurrentPacketSent++;
			}
		} finally {
			lock.writeLock().unlock();
		}

		return currentWorkPacketList;

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
		int packetsRemaining = 0;
		lock.readLock().lock();
		
		try {
			// the multiple of packets within the list left to send and the number
			// of times the current packet should be sent
			packetsRemaining = (workPacketList.size() * numberOfCopies)
					+ (numberOfCopies - timesCurrentPacketSent);
		} finally {
			lock.readLock().unlock();
		}

		return packetsRemaining;
	}

	@Override
	public boolean hasWorkPackets() {
		return numberOfPacketsRemaining() > 0;
	}
	
	public void setPacketsPerList(int packetsPerList){
		if (packetsPerList > 0){
			this.packetsPerList = packetsPerList;
		}
	}
	
	public WorkPacketList loadNextPacketList() {
		lock.writeLock().lock();
		
		try {
			currentWorkPacketList.clear();
			
			for (int count=0; count<packetsPerList; count++){
				if (!workPacketList.isEmpty()){
					currentWorkPacketList.add(workPacketList.removeFirst());
				} else {
					break;
				}						
			}
			
		} finally {
			lock.writeLock().unlock();
		}
		
		return currentWorkPacketList;
	}

}
