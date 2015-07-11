/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.calculationclasses.DataProcessorClassWriter;
import finalproject.poc.persistence.DatabaseFacade;

/**
 * @author Phil
 *
 */
public class RegisterRequestHandler extends AbstractClientRequestHandler {

	private DatabaseFacade database;
	private DataProcessorClassWriter classWriter;
	
	public RegisterRequestHandler(){
		super();
	}
	
	public RegisterRequestHandler(DatabaseFacade database){
		this();
		this.database = database;
	}
	
	public RegisterRequestHandler(DatabaseFacade database, DataProcessorClassWriter classWriter){
		this(database);
		this.classWriter = classWriter;
	}
	
	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.REGISTER;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			RegistrationPack registrationPack = (RegistrationPack) input.readObject();
			database.addDevice(registrationPack);
			
			output.reset();
			output.writeInt(ServerRequest.LOAD_PROCESSING_CLASS);
			output.writeObject(classWriter.getClassBytes());
			output.flush();
		} catch (IOException | ClassNotFoundException ex){
			
		}
		

	}

}
