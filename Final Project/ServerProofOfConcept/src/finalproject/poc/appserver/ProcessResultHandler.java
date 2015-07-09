/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.calculationclasses.AbstractResultsValidator;
import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.ResultsPacketList;
import finalproject.poc.persistence.DatabaseFacade;
import finalproject.poc.work.AbstractWorkPacketDrawer;

/**
 * @author Phil
 *
 */
public class ProcessResultHandler extends AbstractClientRequestHandler {

	private DatabaseFacade database;
	private AbstractResultsValidator validator;
	private AbstractWorkPacketDrawer packetDrawer;
	private ResultsPacketList resultsList;
	
	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.PROCESS_RESULT;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			resultsList = (ResultsPacketList) input.readObject();
			String deviceID = resultsList.getDeviceID();
			
			for(IResultsPacket result: resultsList) {
				if (validator.resultIsValid(result)){
					database.writeValidResultSent(deviceID);
				} else {
					database.writeInvalidResultSent(deviceID);
				}
			}
			
			output.reset();
			
			if (database.isDeviceBlacklisted(deviceID) || !packetDrawer.hasWorkPackets()){				
				output.writeInt(ServerRequest.BECOME_DORMANT);
			} else {
				output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
				output.writeObject(packetDrawer.getNextWorkPacket());
			}
			
			output.flush();
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
