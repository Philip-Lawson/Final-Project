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
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;

/**
 * @author Phil
 *
 */
public class CalculationFinishedRequestHandler extends
		AbstractClientRequestHandler {

	private static Logger logger = Logger
			.getLogger(CalculationFinishedRequestHandler.class.getName());

	private DeviceDetailsManager deviceDetailsManager;
	private DeviceVersionManager deviceVersionManager;
	private UserDetailsManager userDetailsManager;

	public CalculationFinishedRequestHandler() {
		super();
	}

	public CalculationFinishedRequestHandler(
			DeviceDetailsManager deviceDetailsManager,
			DeviceVersionManager deviceVersionManager,
			UserDetailsManager userDetailsManager) {
		super();
		this.deviceDetailsManager = deviceDetailsManager;
		this.deviceVersionManager = deviceVersionManager;
		this.userDetailsManager = userDetailsManager;
	}

	@Override
	public void processRequest(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		readClientMessage(requestNum, input, output);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.server.AbstractClientRequestHandler#getRequestNum
	 * ()
	 */
	@Override
	protected int getRequestNum() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.server.AbstractClientRequestHandler#handleHere
	 * (java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {

	}

	private void readClientMessage(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		switch (requestNum) {
		case ClientRequest.CHANGE_EMAIL:
			changeEmail(input, output);
			break;
		case ClientRequest.DEREGISTER_DEVICE:
			deleteAccount(input, output);
			break;
		case ClientRequest.PROCESS_RESULT:
			processResult(input, output);
			break;
		case ClientRequest.REGISTER:
			registerDevice(input, output);
			break;
		default:
			sendBecomeDormantMessage(output);
		}
	}

	private void sendBecomeDormantMessage(ObjectOutputStream output) {
		try {
			output.reset();
			output.writeInt(ServerRequest.BECOME_DORMANT);
			output.flush();
		} catch (IOException e) {
			logger.log(Level.FINE, "Problem sending dormant message", e);
		}
	}

	private void changeEmail(ObjectInputStream input, ObjectOutputStream output) {
		RegistrationPack registrationPack;
		try {
			registrationPack = (RegistrationPack) input.readObject();
			output.reset();
			output.writeBoolean(userDetailsManager
					.changeEmailAddress(registrationPack));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem changing email address", e);
		}
	}

	private void deleteAccount(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			String deviceID = (String) input.readObject();

			output.reset();
			output.writeBoolean(deviceDetailsManager.deregisterDevice(deviceID));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem deleting account", e);
		}
	}

	private void processResult(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			input.readObject();
			sendBecomeDormantMessage(output);
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem processing result", e);
		}
	}

	private void registerDevice(ObjectInputStream input,
			ObjectOutputStream output) {
		RegistrationPack registrationPack;
		try {
			registrationPack = (RegistrationPack) input.readObject();
			output.reset();
			deviceVersionManager.saveDeviceVersion(registrationPack);
			output.writeBoolean(deviceDetailsManager
					.addDevice(registrationPack));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE, "Problem registering device", e);
		}
	}

}
