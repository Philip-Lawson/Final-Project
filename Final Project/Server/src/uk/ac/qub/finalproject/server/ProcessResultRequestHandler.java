/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;
import uk.ac.qub.finalproject.server.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.server.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.server.calculationclasses.ResultsPacketList;

/**
 * @author Phil
 *
 */
public class ProcessResultRequestHandler extends AbstractClientRequestHandler {

	public static final String LOAD_MORE_WORK_PACKETS = "Load More Work Packets";
	public static final String PROCESSING_COMPLETE = "Processing Complete!";

	private DeviceDetailsManager deviceDetailsManager;
	private ResultsPacketManager resultsPacketManager;
	private IResultValidator validator;
	private AbstractWorkPacketDrawer packetDrawer;

	public ProcessResultRequestHandler() {
		super();
	}

	public ProcessResultRequestHandler(DeviceDetailsManager deviceDetailsManager) {
		super();
		this.deviceDetailsManager = deviceDetailsManager;
	}

	public ProcessResultRequestHandler(
			DeviceDetailsManager deviceDetailsManager,
			IResultValidator validator) {
		this(deviceDetailsManager);
		this.validator = validator;
	}

	public ProcessResultRequestHandler(
			DeviceDetailsManager deviceDetailsManager,
			ResultsPacketManager resultsPacketManager,
			IResultValidator validator, AbstractWorkPacketDrawer packetDrawer) {
		this(deviceDetailsManager, validator);
		this.resultsPacketManager = resultsPacketManager;
		this.packetDrawer = packetDrawer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		return ClientRequest.PROCESS_RESULT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java
	 * .io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		try {
			ResultsPacketList resultsList = (ResultsPacketList) input
					.readObject();
			String deviceID = resultsList.getDeviceID();

			processResults(resultsList, deviceID);
			sendResponse(output, deviceID);

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	private void processResults(ResultsPacketList resultsList, String deviceID) {
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

		if (!packetDrawer.hasWorkPackets()) {
			if (resultsPacketManager.getNumberOfPacketsProcessed() == packetDrawer
					.numberOfDistinctWorkPackets()) {
				setChanged();
				notifyObservers(PROCESSING_COMPLETE);
			} else {
				setChanged();
				notifyObservers(LOAD_MORE_WORK_PACKETS);
			}
		}

	}

	private void sendResponse(ObjectOutputStream output, String deviceID)
			throws IOException {
		output.reset();

		if (deviceDetailsManager.deviceIsBlacklisted(deviceID)
				|| !packetDrawer.hasWorkPackets()) {
			output.writeInt(ServerRequest.BECOME_DORMANT);
		} else {
			output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
			output.writeObject(packetDrawer.getNextWorkPacket());
			deviceDetailsManager.updateActiveDevice(deviceID,
					new Date().getTime());
		}

		output.flush();
	}

}
