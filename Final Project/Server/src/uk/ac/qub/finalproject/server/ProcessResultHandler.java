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
import uk.ac.qub.finalproject.persistence.UserDetails;
import uk.ac.qub.finalproject.server.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.server.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.server.calculationclasses.ResultsPacketList;

/**
 * @author Phil
 *
 */
public class ProcessResultHandler extends AbstractClientRequestHandler {

	private DeviceDetailsManager deviceDetailsManager;
	private ResultsPacketManager resultsPacketManager;	
	private IResultValidator validator;
	private AbstractWorkPacketDrawer packetDrawer;

	public ProcessResultHandler() {
		super();
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager) {
		super();
		this.deviceDetailsManager = deviceDetailsManager;
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager,
			IResultValidator validator) {
		this(deviceDetailsManager);
		this.validator = validator;
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager,
			ResultsPacketManager resultsPacketManager, UserDetails userDetails,
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processResults(ResultsPacketList resultsList, String deviceID) {
		for (IResultsPacket result : resultsList) {
			IWorkPacket initialData = packetDrawer.getInitialData(result
					.getPacketId());

			if (validator.isFuzzyValidator()
					&& validator.resultIsPending(result.getPacketId())) {
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
				// TODO - call the system to finish the calculation
			} else {
				// TODO - call the syste to reload incomplete work packets
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
