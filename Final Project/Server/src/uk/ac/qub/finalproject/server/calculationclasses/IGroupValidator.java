package uk.ac.qub.finalproject.server.calculationclasses;

import java.util.Map;

public interface IGroupValidator {
	
	public IResultsPacket compareGroupResults(Map<String, IResultsPacket> pendingResults, IWorkPacket initalData);
	
	public Map<String, Boolean> getDeviceStatistics(Map<String, IResultsPacket> pendingResults, IResultsPacket exemplar);

}
