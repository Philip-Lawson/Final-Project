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

import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.LoggingUtils;

/**
 * This request handler responds to client requests for work packet lists. It
 * will send a work packet list to the client if there are packets available and
 * the client is not considered fraudulent. Otherwise it sends the client a
 * message to become dormant.
 * 
 * @author Phil
 *
 */
public class WorkPacketRequestHandler extends AbstractClientRequestHandler {

	private Logger logger = LoggingUtils
			.getLogger(WorkPacketRequestHandler.class);

	private static int MIN_VERSION_CODE = 1;

	private AbstractWorkPacketDrawer workPacketDrawer;
	private DeviceDetailsManager deviceDetailsManager;
	private DeviceVersionManager deviceVersionManager;

	public WorkPacketRequestHandler() {
		super();
	}

	public WorkPacketRequestHandler(AbstractWorkPacketDrawer workPacketDrawer,
			DeviceDetailsManager deviceDetailsManager,
			DeviceVersionManager deviceVersionManager) {
		this();
		this.workPacketDrawer = workPacketDrawer;
		this.deviceDetailsManager = deviceDetailsManager;
		this.deviceVersionManager = deviceVersionManager;
	}

	@Override
	protected int getRequestNum() {
		return ClientRequest.REQUEST_WORK_PACKET;
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {

		try {
			String deviceID = (String) input.readObject();
			output.reset();

			if (canSendPackets(deviceID)) {
				long timeStamp = new Date().getTime();
				deviceDetailsManager.updateActiveDevice(deviceID, timeStamp);

				WorkPacketList workPacketList = workPacketDrawer
						.getNextWorkPacket();
				workPacketList.setTimeStamp(timeStamp);

				output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
				output.writeObject(workPacketList);

			} else {
				output.writeInt(ServerRequest.BECOME_DORMANT);
			}

			output.flush();

		} catch (IOException | ClassNotFoundException e) {
			logger.log(Level.FINE, WorkPacketRequestHandler.class.getName()
					+ " Problem sending a work packet", e);
		}

	}

	/**
	 * Helper method checks to see if it is appropriate to send work packets to
	 * this device.
	 * 
	 * @param deviceID
	 * @return
	 */
	private boolean canSendPackets(String deviceID) {
		return workPacketDrawer.hasWorkPackets()
				&& deviceVersionManager.deviceIsVersionOrAbove(
						MIN_VERSION_CODE, deviceID)
				&& !deviceDetailsManager.deviceIsBlacklisted(deviceID);
	}

}
