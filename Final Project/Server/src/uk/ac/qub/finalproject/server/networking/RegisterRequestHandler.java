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
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.LoggingUtils;

/**
 * This request handler responds to registration requests from client devices.
 * It sends a boolean to confirm the success of registration.
 * 
 * @author Phil
 *
 */
public class RegisterRequestHandler extends AbstractClientRequestHandler {

	private Logger logger = LoggingUtils
			.getLogger(RegisterRequestHandler.class);

	private DeviceDetailsManager deviceManager;
	private DeviceVersionManager deviceVersionManager;

	public RegisterRequestHandler() {
		super();
	}

	public RegisterRequestHandler(DeviceDetailsManager deviceManager,
			DeviceVersionManager deviceVersionManager) {
		this();
		this.deviceManager = deviceManager;
		this.deviceVersionManager = deviceVersionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		return ClientRequest.REGISTER;
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
			boolean successfulRegistration = true;

			try {
				RegistrationPack registrationPack = (RegistrationPack) input
						.readObject();
				successfulRegistration = deviceManager
						.addDevice(registrationPack);
				deviceVersionManager.saveDeviceVersion(registrationPack);
			} catch (ClassNotFoundException e) {

			}

			output.reset();
			output.writeBoolean(successfulRegistration);
			output.flush();
		} catch (IOException ex) {
			logger.log(Level.FINE, RegisterRequestHandler.class.getName()
					+ " Problem registering a device", ex);
		}

	}

}
