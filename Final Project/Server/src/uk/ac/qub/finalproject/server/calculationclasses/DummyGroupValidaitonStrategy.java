/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

import java.util.Map;

/**
 * @author Phil
 *
 */
public class DummyGroupValidaitonStrategy implements IGroupValidationStrategy {

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.calculationclasses.IGroupValidationStrategy#compareGroupResults(java.util.Map, uk.ac.qub.finalproject.server.calculationclasses.IWorkPacket)
	 */
	@Override
	public IResultsPacket compareGroupResults(
			Map<String, IResultsPacket> pendingResults, IWorkPacket initalData) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.calculationclasses.IGroupValidationStrategy#getDeviceStatistics(java.util.Map, uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket)
	 */
	@Override
	public Map<String, Boolean> getDeviceStatistics(
			Map<String, IResultsPacket> pendingResults, IResultsPacket exemplar) {
		// TODO Auto-generated method stub
		return null;
	}

}
