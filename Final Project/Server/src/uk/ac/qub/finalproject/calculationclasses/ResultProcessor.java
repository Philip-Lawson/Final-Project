/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * The ResultsProcessor class encapsulates the methods to process result lists
 * that have been sent from a client device. It extends the Observable class,
 * and notifies observers when processing is complete, or when the system needs
 * to reload work packets.
 * 
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

	public ResultProcessor(DeviceDetailsManager deviceDetailsManager,
			ResultsPacketManager resultsPacketManager,
			IResultValidator validator, AbstractWorkPacketDrawer packetDrawer) {
		this.deviceDetailsManager = deviceDetailsManager;
		this.validator = validator;
		this.resultsPacketManager = resultsPacketManager;
		this.packetDrawer = packetDrawer;
	}

	/**
	 * 
	 * @param resultsList
	 * @param deviceID
	 */
	public void processResults(ResultsPacketList resultsList, String deviceID) {

		processIndividualPackets(resultsList, deviceID);

		long timeStamp = resultsList.getTimeStamp();

		// There is a risk that the result list may not have been
		// timestamped. If that's the case, the timestamp will default to 0
		// This would make skew the processing time data
		if (timeStamp > 0) {
			processTimeStamp(resultsList.getTimeStamp(), resultsList.size());
		}

		checkProcessingComplete();
	}

	/**
	 * Processes all the packets in the results list. The device that sent the
	 * results packets will receive a mark for each valid or invalid packet
	 * sent.<br>
	 * </br>Once all packets have been validated, the valid ones are sent to the
	 * results packet manager to be updated as a batch. This is done to help
	 * mitigate the bottleneck that occurs when inserting many results into the
	 * database.
	 * 
	 * @param resultsList
	 * @param deviceID
	 */
	public void processIndividualPackets(ResultsPacketList resultsList,
			String deviceID) {
		ArrayList<IResultsPacket> validResults = new ArrayList<IResultsPacket>(
				resultsList.size());

		for (IResultsPacket result : resultsList) {
			IWorkPacket initialData = packetDrawer.getInitialData(result
					.getPacketId());

			if (validator.resultIsPending(result.getPacketId())) {
				validator.addResultToGroup(result, initialData, deviceID);
			} else if (validator.resultIsValid(result, initialData)) {
				deviceDetailsManager.writeValidResultSent(deviceID);
				validResults.add(result);
			} else {
				deviceDetailsManager.writeInvalidResultSent(deviceID);
			}
		}

		validResults.trimToSize();

		if (validResults.size() > 0) {
			resultsPacketManager.writeResultsList(validResults);
		}
	}

	/**
	 * Processes the timestamp stored in the results list. It sends it to the
	 * device manager to update the rolling average and sends it back to its
	 * observer to alter the info on screen.
	 * 
	 * @param initialTimeStamp
	 * @param numPackets
	 */
	public void processTimeStamp(long initialTimeStamp, int numPackets) {
		long timeProcessing = new Date().getTime() - initialTimeStamp;
		deviceDetailsManager.adjustAverage(numPackets, timeProcessing);
		setChanged();
		notifyObservers(getMinutesProcessing(numPackets, timeProcessing));
	}

	/**
	 * Checks to see if all work packets have been processed. If that is the
	 * case, it will notify observers with an appropriate message i.e. if
	 * processing is complete or work packets need to be reloaded.
	 */
	public void checkProcessingComplete() {
		if (resultsPacketManager.getNumberOfPacketsProcessed() >= packetDrawer
				.numberOfDistinctWorkPackets()) {
			// need to double check in case the concurrent
			// hash map size method is inaccurate
			if (resultsPacketManager.allResultsComplete()) {
				setChanged();
				notifyObservers(PROCESSING_COMPLETE);
			} else if (!packetDrawer.hasWorkPackets()) {
				setChanged();
				notifyObservers(LOAD_MORE_WORK_PACKETS);
			}
		}

	}

	public String getMinutesProcessing(int numPackets, long millis) {
		Long minutesProcessing = TimeUnit.MILLISECONDS.toMinutes(millis
				/ numPackets);
		return minutesProcessing.toString();
	}
}
