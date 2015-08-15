/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.util.Date;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * @author Phil
 *
 */
public class ResultProcessor extends Observable {
	
	public static final String LOAD_MORE_WORK_PACKETS = "Load More Work Packets";
	public static final String PROCESSING_COMPLETE = "Processing Complete!";

	private DeviceDetailsManager deviceDetailsManager;
	private ResultsPacketManager resultsPacketManager;
	private IResultValidator validator;
	private AbstractWorkPacketDrawer packetDrawer;
	
	public ResultProcessor(
			DeviceDetailsManager deviceDetailsManager,
			ResultsPacketManager resultsPacketManager,
			IResultValidator validator, AbstractWorkPacketDrawer packetDrawer) {
		this.deviceDetailsManager = deviceDetailsManager;
		this.validator =validator;
		this.resultsPacketManager = resultsPacketManager;
		this.packetDrawer = packetDrawer;
	}
	

	public void processResults(ResultsPacketList resultsList, String deviceID){
		processIndividualPackets(resultsList, deviceID);
		processTimeStamp(resultsList.getTimeStamp(),resultsList.size());
		checkProcessingComplete();
		
	}
	
	public void processIndividualPackets(ResultsPacketList resultsList, String deviceID){
		for (IResultsPacket result : resultsList) {
			IWorkPacket initialData = packetDrawer.getInitialData(result
					.getPacketId());

			if (validator.resultIsPending(result.getPacketId())) {
				validator.addResultToGroup(result, initialData, deviceID);
			} else if (validator.resultIsValid(result, initialData)) {
				deviceDetailsManager.writeValidResultSent(deviceID);
				resultsPacketManager.writeResult(result);
			} else {
				deviceDetailsManager.writeInvalidResultSent(deviceID);
			}
		}
	}
	
	public void processTimeStamp(long initialTimeStamp, int numPackets){
		long timeProcessing = new Date().getTime() - initialTimeStamp;
		deviceDetailsManager.adjustAverage(numPackets, timeProcessing);
		setChanged();
		notifyObservers(getMinutesProcessing(numPackets, timeProcessing));
	}
	
	public void checkProcessingComplete(){
		if (!packetDrawer.hasWorkPackets()) {			
			if (resultsPacketManager.allResultsComplete()) {
				setChanged();
				notifyObservers(PROCESSING_COMPLETE);
			} else {
				setChanged();
				notifyObservers(LOAD_MORE_WORK_PACKETS);
			}
		}
	}
	
	public String getMinutesProcessing(int numPackets, long millis){
		Long minutesProcessing = TimeUnit.MILLISECONDS.toMinutes(millis/numPackets);
		return minutesProcessing.toString();
	}
}
