package finalproject.poc.persistence;

import java.util.Collection;

import finalproject.poc.appserver.RegistrationPack;
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
	public void addDevice(RegistrationPack registrationPack);
	public boolean changeEmailAddress(RegistrationPack registrationPack);
	public boolean deregisterDevice(String androidID);
	public boolean deregisterUser(String emailAddress);
	

}
