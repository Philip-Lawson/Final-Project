/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;
import uk.ac.qub.finalproject.persistence.LoggingUtils;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;

/**
 * This request handler processes client requests to change their email address.
 * It sends back a boolean confirming the change.
 * 
 * @author Phil
 *
 */
public class ChangeEmailRequestHandler extends AbstractClientRequestHandler {

	private Logger logger = LoggingUtils
			.getLogger(ChangeEmailRequestHandler.class);

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
			output.writeBoolean(userDetails
					.changeEmailAddress(registrationPack));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, ChangeEmailRequestHandler.class.getName()
					+ " Problem changing email address", e);
		}

	}

}
