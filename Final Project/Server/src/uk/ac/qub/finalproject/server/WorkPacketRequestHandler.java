/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;


/**
 * @author Phil
 *
 */
public class WorkPacketRequestHandler extends AbstractClientRequestHandler {

	private AbstractWorkPacketDrawer workPacketDrawer;	

	public WorkPacketRequestHandler() {
		super();
	}

	public WorkPacketRequestHandler(AbstractWorkPacketDrawer workPacketDrawer) {
		this();
		this.workPacketDrawer = workPacketDrawer;
	}

	
	@Override
	protected int getRequestNum() {		
		return ClientRequest.REQUEST_WORK_PACKET;
	}

	
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		
		try {
			
			if (workPacketDrawer.hasWorkPackets()) {
				output.writeInt(ServerRequest.PROCESS_WORK_PACKETS);
				output.writeObject(workPacketDrawer.getNextWorkPacket());				
			} else {
				output.writeInt(ServerRequest.BECOME_DORMANT);
			}			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
