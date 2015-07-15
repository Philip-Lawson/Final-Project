/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import finalproject.poc.calculationclasses.AbstractResultsValidator;
import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.ResultsPacketList;
import finalproject.poc.persistence.AbstractWorkPacketDrawer;
import finalproject.poc.persistence.DatabaseFacade;
import finalproject.poc.persistence.DeviceDetailsManager;
import finalproject.poc.persistence.ResultsPacketManager;

/**
 * @author Phil
 *
 */
public class ProcessResultHandler extends AbstractClientRequestHandler {

	private DeviceDetailsManager deviceDetailsManager;
	private ResultsPacketManager resultsPacketManager;
	private AbstractResultsValidator validator;
	private AbstractWorkPacketDrawer packetDrawer;

	public ProcessResultHandler() {
		super();
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager) {
		super();
		this.deviceDetailsManager = deviceDetailsManager;
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager,
			AbstractResultsValidator validator) {
		this(deviceDetailsManager);
		this.validator = validator;
	}

	public ProcessResultHandler(DeviceDetailsManager deviceDetailsManager,
			AbstractResultsValidator validator,
			AbstractWorkPacketDrawer packetDrawer) {
		this(deviceDetailsManager, validator);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		try {
			ResultsPacketList resultsList = (ResultsPacketList) input
					.readObject();
			String deviceID = resultsList.getDeviceID();

			processResults(resultsList);
			sendResponse(output, deviceID);

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processResults(ResultsPacketList resultsList) {
		String deviceID = resultsList.getDeviceID();
		boolean isValid;		

		for (IResultsPacket result : resultsList) {			
			isValid = validator.resultIsValid(result);

			if (isValid) {
				resultsPacketManager.writeResult(result);
				deviceDetailsManager.writeValidResultSent(deviceID);
			} else {
				deviceDetailsManager.writeInvalidResultSent(deviceID);
			}
		}
	}

	private void sendResponse(ObjectOutputStream output, String deviceID)
			throws IOException {
		output.reset();

		if (deviceDetailsManager.deviceIsBlacklisted(deviceID)
				|| !packetDrawer.hasWorkPackets()) {
			output.writeInt(ServerRequest.BECOME_DORMANT);
			deviceDetailsManager.deregisterDevice(deviceID);
		} else {
			output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
			output.writeObject(packetDrawer.getNextWorkPacket());
			deviceDetailsManager.updateActiveDevice(deviceID, new Date().getTime());
		}

		output.flush();
	}

}
