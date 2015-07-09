/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.persistence.DatabaseFacade;

/**
 * @author Phil
 *
 */
public class DeregisterDeviceHandler extends AbstractClientRequestHandler {

	private DatabaseFacade database;
	
	public DeregisterDeviceHandler(){
		super();
	}
	
	public DeregisterDeviceHandler(DatabaseFacade database){
		super();
		this.database = database;
	}
	

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.DEREGISTER_DEVICE;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		String androidID;
		
		try {
			androidID = (String)input.readObject();
			
			output.reset();
			output.writeInt(ServerRequest.CHANGE_CONFIRMED);
			output.writeBoolean(database.deregisterDevice(androidID));
			output.flush();	
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
