package uk.ac.qub.finalproject.calculationclasses;

import java.util.Map;

/**
 * This represents the contract required from all group validation strategies.
 * Each group validation strategy must be able to compare a group of results,
 * returning the most accurate result and a comparison method that returns a map
 * containing each device ID and a boolean determining if their result was good
 * enough to be considered valid.
 * 
 * @author Phil
 *
 */
public interface IGroupValidationStrategy {

	/**
	 * Compares a list of pending results and returns the most accurate result.
	 * 
	 * @param pendingResults
	 * @param initalData
	 * @return
	 */
	public IResultsPacket compareGroupResults(
			Map<String, IResultsPacket> pendingResults, IWorkPacket initalData);

	/**
	 * Returns a key value pair of device ID and a boolean representing whether
	 * a device's result was valid or not.
	 * 
	 * @param pendingResults
	 * @param exemplar
	 * @return
	 */
	public Map<String, Boolean> getDeviceStatistics(
			Map<String, IResultsPacket> pendingResults, IResultsPacket exemplar);

}
