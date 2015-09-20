/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.ResultProcessor;
import uk.ac.qub.finalproject.calculationclasses.ResultsPacketList;
import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.LoggingUtils;

/**
 * This request handler responds to client requests to process a list of results
 * packets. In return it will either send another list of work packets or a
 * request to become dormant.
 * 
 * @author Phil
 *
 */
public class ProcessResultRequestHandler extends AbstractClientRequestHandler {

	private Logger logger = LoggingUtils
			.getLogger(ProcessResultRequestHandler.class);

	private static int MIN_VERSION_CODE = 1;

	private DeviceDetailsManager deviceDetailsManager;
	private DeviceVersionManager deviceVersionManager;
	private ResultProcessor resultProcessor;
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
			DeviceVersionManager deviceVersionManager,
			AbstractWorkPacketDrawer packetDrawer,
			ResultProcessor resultProcessor) {
		this(deviceDetailsManager);
		this.packetDrawer = packetDrawer;
		this.resultProcessor = resultProcessor;
		this.deviceVersionManager = deviceVersionManager;
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

			resultProcessor.processResults(resultsList, deviceID);
			sendResponse(output, deviceID);

		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, ProcessResultRequestHandler.class.getName()
					+ " Problem processing result", e);
		}

	}

	/**
	 * Sends an appropriate response to the client. This will either be another
	 * list of packets or a request to become dormant.
	 * 
	 * @param output
	 * @param deviceID
	 * @throws IOException
	 */
	private void sendResponse(ObjectOutputStream output, String deviceID)
			throws IOException {
		output.reset();

		if (canSendWorkPacket(deviceID)) {
			long timeStamp = new Date().getTime();
			deviceDetailsManager.updateActiveDevice(deviceID, timeStamp);

			WorkPacketList workPacketList = packetDrawer.getNextWorkPacket();
			workPacketList.setTimeStamp(timeStamp);

			output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
			output.writeObject(workPacketList);
		} else {
			output.writeInt(ServerRequest.BECOME_DORMANT);
		}

		output.flush();
	}

	/**
	 * Checks to see if it is appropriate to send another list of work packets
	 * to the client.
	 * 
	 * @param deviceID
	 * @return
	 */
	private boolean canSendWorkPacket(String deviceID) {
		return !deviceDetailsManager.deviceIsBlacklisted(deviceID)
				&& packetDrawer.hasWorkPackets()
				&& deviceVersionManager.deviceIsVersionOrAbove(
						MIN_VERSION_CODE, deviceID);
	}

}
