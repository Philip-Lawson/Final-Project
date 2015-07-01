package finalproject.poc.persistence;

import java.util.Collection;

import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.IWorkPacket;

public interface DatabaseFacade {
	
	public void writeResults(Collection<IResultsPacket> results);
	public void writeWorkPackets(Collection<IWorkPacket> workPackets);
	public void writeValidResultSent(String deviceID);
	public void writeInvalidResultSent(String deviceId);
	
	public Collection<IWorkPacket> retrieveIncompleteWorkPackets();	
	public Collection<String> retrieveDedicatedUserEmails();
	public boolean isDeviceBlacklisted(String deviceID);
	
	

}
