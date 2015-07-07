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
public class DeregisterUserHandler extends AbstractClientRequestHandler {
	
	private DatabaseFacade database;
	
	public DeregisterUserHandler(){
		super();
	}
	
	public DeregisterUserHandler(DatabaseFacade database){
		this.database = database;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.DEREGISTER_USER.getRequestNum();
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		String emailAddress;
		try {
			emailAddress = (String) input.readObject();
			
			output.reset();
			output.writeInt(ServerRequest.CHANGE_CONFIRMED.getRequestNum());
			output.writeBoolean(database.deregisterUser(emailAddress));
			output.flush();	
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

}
