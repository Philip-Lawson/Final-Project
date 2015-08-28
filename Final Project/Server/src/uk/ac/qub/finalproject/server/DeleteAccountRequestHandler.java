/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;

/**
 * @author Phil
 *
 */
public class DeleteAccountRequestHandler extends AbstractClientRequestHandler {
	
	private static Logger logger = Logger.getLogger(DeleteAccountRequestHandler.class.getName());
	
	private DeviceDetailsManager deviceDetailsManager;
	
	public DeleteAccountRequestHandler(){
		super();
	}
	
	public DeleteAccountRequestHandler(DeviceDetailsManager deviceDetailsManager){
		this.deviceDetailsManager = deviceDetailsManager;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {			
		return ClientRequest.DEREGISTER_DEVICE;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		try {
			String deviceID = (String) input.readObject();			;
			output.writeBoolean(deviceDetailsManager.deregisterDevice(deviceID));			
			output.flush();			
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem deleting account", e);
		}		

	}
	
}
