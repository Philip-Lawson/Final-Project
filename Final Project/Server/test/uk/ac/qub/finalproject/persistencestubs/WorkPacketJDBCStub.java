/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.persistence.WorkPacketJDBC;

/**
 * @author Phil
 *
 */
public class WorkPacketJDBCStub extends WorkPacketJDBC {
	
	private Collection<IWorkPacket> workPackets;
	private IWorkPacket workPacket;
	private boolean workPacketsAdded;
	private boolean incompleteWorkPacketsReloaded = false;
	
	@Override
	public void addWorkPackets(Collection<IWorkPacket> workPackets){
		workPacketsAdded = true;
	}
	
	@Override
	public Collection<IWorkPacket> getIncompleteWorkPackets(){
		incompleteWorkPacketsReloaded = true;
		return workPackets;
	}
	
	public void setWorkPackets(Collection<IWorkPacket> workPackets){
		this.workPackets = workPackets;
	}
	
	@Override
	public IWorkPacket getIndividualWorkPacket(String workPacketID){
		return workPacket;
	}
	
	public void setWorkPacket(IWorkPacket workPacket){
		this.workPacket = workPacket;
	}

	public boolean isWorkPacketsAdded() {
		return workPacketsAdded;
	}

	public void setWorkPacketsAdded(boolean workPacketsAdded) {
		this.workPacketsAdded = workPacketsAdded;
	}

	public boolean isIncompleteWorkPacketsReloaded() {
		return incompleteWorkPacketsReloaded;
	}

	public void setIncompleteWorkPacketsReloaded(
			boolean incompleteWorkPacketsReloaded) {
		this.incompleteWorkPacketsReloaded = incompleteWorkPacketsReloaded;
	}

}
