package uk.ac.qub.finalproject.calculationclassstubs;

import java.util.Map;

import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

public class GroupValidationStrategyStub implements IGroupValidationStrategy {

	private Map<String, Boolean> deviceStats;
	private IResultsPacket bestResult;
	
	@Override
	public IResultsPacket compareGroupResults(
			Map<String, IResultsPacket> pendingResults, IWorkPacket initalData) {
		return bestResult;
	}

	@Override
	public Map<String, Boolean> getDeviceStatistics(
			Map<String, IResultsPacket> pendingResults, IResultsPacket exemplar) {
		return deviceStats;
	}

	public Map<String, Boolean> getDeviceStats() {
		return deviceStats;
	}

	public void setDeviceStats(Map<String, Boolean> deviceStats) {
		this.deviceStats = deviceStats;
	}

	public IResultsPacket getBestResult() {
		return bestResult;
	}

	public void setBestResult(IResultsPacket bestResult) {
		this.bestResult = bestResult;
	}

}
