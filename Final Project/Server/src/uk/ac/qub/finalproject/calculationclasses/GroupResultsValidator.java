/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * @author Phil
 *
 */
public class GroupResultsValidator implements IResultValidator {

	private static final int MIN_RESULTS_NEEDED_FOR_COMPARISON = 5;

	private int resultsNeeded = MIN_RESULTS_NEEDED_FOR_COMPARISON;
	private ResultsPacketManager resultsDrawer;
	private DeviceDetailsManager deviceDetailsDrawer;
	private IValidationStrategy validationStrategy;
	private IGroupValidationStrategy groupValidator;
	private Map<String, Map<String, IResultsPacket>> pendingResultsMap = new ConcurrentHashMap<String, Map<String, IResultsPacket>>();

	public GroupResultsValidator(ResultsPacketManager resultsDrawer) {
		this.resultsDrawer = resultsDrawer;
	}

	public GroupResultsValidator(ResultsPacketManager resultsPacketManager,
			DeviceDetailsManager deviceDetailsManager) {
		this(resultsPacketManager);
		this.setDeviceDetailsManager(deviceDetailsManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.calculationclasses.IResultValidator#
	 * setValidationStrategy
	 * (uk.ac.qub.finalproject.server.calculationclasses.IValidationStrategy)
	 */
	@Override
	public void setValidationStrategy(IValidationStrategy validationStrategy) {
		if (null != validationStrategy) {
			this.validationStrategy = validationStrategy;
		}
	}

	@Override
	public void setGroupValidationStrategy(
			IGroupValidationStrategy groupValidationStrategy) {
		if (null != groupValidationStrategy) {
			this.groupValidator = groupValidationStrategy;
		}
	}

	public void setResultsDrawer(ResultsPacketManager resultsDrawer) {
		this.resultsDrawer = resultsDrawer;
	}

	public void setDeviceDetailsManager(
			DeviceDetailsManager deviceDetailsManager) {
		this.deviceDetailsDrawer = deviceDetailsManager;
	}

	/**
	 * Sets the number of results needed for a group comparison to occur. If the
	 * number is less than the minimum needed (5) the change will not be
	 * effected.
	 * 
	 * @param resultsNeeded
	 */
	public void setResultsNeededForComparison(int resultsNeeded) {
		if (resultsNeeded >= MIN_RESULTS_NEEDED_FOR_COMPARISON) {
			this.resultsNeeded = resultsNeeded;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.calculationclasses.IResultValidator#
	 * resultIsValid
	 * (uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket)
	 */
	@Override
	public boolean resultIsValid(IResultsPacket resultsPacket,
			IWorkPacket initialData) {
		String packetID = resultsPacket.getPacketId();

		if (resultsDrawer.resultIsSaved(packetID)) {
			IResultsPacket savedResult = resultsDrawer
					.getResultForComparison(packetID);
			return validationStrategy.compareWithSavedResult(resultsPacket,
					savedResult);
		} else {
			return false;
		}
	}

	@Override
	public boolean resultIsPending(String resultsPacketID) {
		if (pendingResultsMap.containsKey(resultsPacketID)) {
			return pendingResultsMap.get(resultsPacketID).size() < resultsNeeded;
		} else {
			return true;
		}
	}

	@Override
	public void addResultToGroup(IResultsPacket resultPacket,
			IWorkPacket initialData, String deviceID) {
		String resultsPacketID = resultPacket.getPacketId();

		if (pendingResultsMap.containsKey(resultsPacketID)) {
			Map<String, IResultsPacket> pendingResults = pendingResultsMap
					.get(resultsPacketID);

			if (pendingResults.size() == MIN_RESULTS_NEEDED_FOR_COMPARISON - 1) {
				pendingResults.put(deviceID, resultPacket);
				processGroupResult(pendingResults, initialData);
			} else if (pendingResults.size() < MIN_RESULTS_NEEDED_FOR_COMPARISON) {
				pendingResults.put(deviceID, resultPacket);
			}

		} else {
			Map<String, IResultsPacket> newResultMap = new ConcurrentHashMap<String, IResultsPacket>();
			newResultMap.put(deviceID, resultPacket);
			pendingResultsMap.put(resultsPacketID, newResultMap);
		}

	}

	public void processGroupResult(Map<String, IResultsPacket> pendingResults,
			IWorkPacket initialData) {
		IResultsPacket bestResult = groupValidator.compareGroupResults(
				pendingResults, initialData);
		resultsDrawer.writeResult(bestResult);

		Map<String, Boolean> deviceStatistics = groupValidator
				.getDeviceStatistics(pendingResults, bestResult);

		for (String deviceID : deviceStatistics.keySet()) {
			if (deviceStatistics.get(deviceID)) {
				deviceDetailsDrawer.writeValidResultSent(deviceID);
			} else {
				deviceDetailsDrawer.writeInvalidResultSent(deviceID);
			}
		}
	}

}
