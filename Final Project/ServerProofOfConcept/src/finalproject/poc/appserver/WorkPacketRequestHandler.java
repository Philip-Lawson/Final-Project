/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.work.AbstractWorkPacketDrawer;

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
		super();
		this.workPacketDrawer = workPacketDrawer;
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
		return ClientRequest.REQUEST_WORK_PACKET.getRequestNum();
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
			
			if (workPacketDrawer.hasWorkPackets()) {
				output.writeInt(ServerRequest.NEW_CALCULATION.getRequestNum());
				output.writeObject(workPacketDrawer.getNextWorkPacket());
			} else {
				output.writeInt(ServerRequest.BECOME_DORMANT.getRequestNum());
			}			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
