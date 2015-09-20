/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;

/**
 * @author Phil
 *
 */
public class WorkPacketDrawerStub extends AbstractWorkPacketDrawer {

	private IWorkPacket initialData;
	private boolean hasWorkPackets;
	
	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#addWorkPackets(java.util.Collection)
	 */
	@Override
	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#reloadIncompletedWorkPackets()
	 */
	@Override
	public void reloadIncompletedWorkPackets() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#getNextWorkPacket()
	 */
	@Override
	public WorkPacketList getNextWorkPacket() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#setNumberOfCopies(int)
	 */
	@Override
	public void setNumberOfCopies(int numberOfCopies)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#setPacketsPerList(int)
	 */
	@Override
	public void setPacketsPerList(int packetsPerList) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#numberOfPacketsRemaining()
	 */
	@Override
	public int numberOfPacketsRemaining() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#numberOfDistinctWorkPackets()
	 */
	@Override
	public int numberOfDistinctWorkPackets() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#hasWorkPackets()
	 */
	@Override
	public boolean hasWorkPackets() {	
		return hasWorkPackets;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer#getInitialData(java.lang.String)
	 */
	@Override
	public IWorkPacket getInitialData(String packetID) {		
		return initialData;
	}
	
	public void setInitialData(IWorkPacket initialData){
		this.initialData = initialData;
	}
	

	public void setHasWorkPackets(boolean hasWorkPackets) {
		this.hasWorkPackets = hasWorkPackets;
	}

	@Override
	public void addPacketsToProcessedList(Collection<IWorkPacket> workPackets) {
		// TODO Auto-generated method stub
		
	}

}
