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
import uk.ac.qub.finalproject.persistence.UserDetailsManager;

/**
 * This request handler processes all client requests that relate to account
 * changes and provides a become dormant response to clients sending messages
 * about processing packets.<br>
 * </br>This request handler should be used when processing is either paused or
 * completed.
 * 
 * @author Phil
 *
 */
public class CalculationFinishedRequestHandler extends
		AbstractClientRequestHandler {

	private Logger logger = LoggingUtils
			.getLogger(CalculationFinishedRequestHandler.class);

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
		processClientMessage(requestNum, input, output);
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

	/**
	 * Processes a client's request.
	 * 
	 * @param requestNum
	 * @param input
	 * @param output
	 */
	private void processClientMessage(int requestNum, ObjectInputStream input,
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

	/**
	 * Helper method for sending a become dormant message to a client device.
	 * 
	 * @param output
	 */
	private void sendBecomeDormantMessage(ObjectOutputStream output) {
		try {
			output.reset();
			output.writeInt(ServerRequest.BECOME_DORMANT);
			output.flush();
		} catch (IOException e) {
			logger.log(Level.FINE,
					CalculationFinishedRequestHandler.class.getName()
							+ " Problem sending dormant message", e);
		}
	}

	/**
	 * Responds to a client request to change their email.
	 * 
	 * @param input
	 * @param output
	 */
	private void changeEmail(ObjectInputStream input, ObjectOutputStream output) {
		RegistrationPack registrationPack;
		try {
			registrationPack = (RegistrationPack) input.readObject();
			output.reset();
			output.writeBoolean(userDetailsManager
					.changeEmailAddress(registrationPack));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE,
					CalculationFinishedRequestHandler.class.getName()
							+ " Problem changing email address", e);
		}
	}

	/**
	 * Responds to a client request to delete their account.
	 * 
	 * @param input
	 * @param output
	 */
	private void deleteAccount(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			String deviceID = (String) input.readObject();

			output.reset();
			output.writeBoolean(deviceDetailsManager.deregisterDevice(deviceID));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE,
					CalculationFinishedRequestHandler.class.getName()
							+ " Problem deleting account", e);
		}
	}

	/**
	 * Reads in a result and sends back a message to become dormant.
	 * 
	 * @param input
	 * @param output
	 */
	private void processResult(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			input.readObject();
			sendBecomeDormantMessage(output);
		} catch (ClassNotFoundException | IOException e) {
			logger.log(Level.FINE,
					CalculationFinishedRequestHandler.class.getName()
							+ " Problem processing result", e);
		}
	}

	/**
	 * Registers a device in the system.
	 * 
	 * @param input
	 * @param output
	 */
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
			logger.log(Level.FINE,
					CalculationFinishedRequestHandler.class.getName()
							+ " Problem registering device", e);
		}
	}

}
