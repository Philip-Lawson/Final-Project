/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.persistence.UserDetailsManager;

/**
 * @author Phil
 *
 */
public class ChangeEmailRequestHandler extends AbstractClientRequestHandler {
	
	private static Logger logger = Logger.getLogger(ChangeEmailRequestHandler.class.getName());

	private UserDetailsManager userDetails;

	public ChangeEmailRequestHandler() {
		super();
	}

	public ChangeEmailRequestHandler(UserDetailsManager userDetails) {
		this();
		this.userDetails = userDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {		
		return ClientRequest.CHANGE_EMAIL;
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
		
		try {
			RegistrationPack registrationPack = (RegistrationPack) input
					.readObject();
			
			output.reset();			
			output.writeBoolean(userDetails.changeEmailAddress(registrationPack));
			output.flush();			
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem changing email address", e);
		}

	}

}
