/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;


/**
 * @author Phil
 *
 */
public class RegisterRequestHandler extends AbstractClientRequestHandler {

	private DeviceDetailsManager deviceManager;
	
	public RegisterRequestHandler(){
		super();
	}
	
	public RegisterRequestHandler(DeviceDetailsManager deviceManager){
		this();
		this.deviceManager = deviceManager;
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
			boolean successfulRegistration = true;
			 
			try {
				RegistrationPack registrationPack = (RegistrationPack) input.readObject();
				successfulRegistration = deviceManager.addDevice(registrationPack);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				
			}		
									
			output.reset();			
			output.writeBoolean(successfulRegistration);
			output.flush();
		} catch (IOException   ex){
			
		}
		

	}
	
	@Override
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		
	}

}
