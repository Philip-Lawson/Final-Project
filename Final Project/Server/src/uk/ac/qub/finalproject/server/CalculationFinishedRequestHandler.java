/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;

/**
 * @author Phil
 *
 */
public class CalculationFinishedRequestHandler extends
		AbstractClientRequestHandler {

	private DeviceDetailsManager deviceDetailsManager;
	private UserDetailsManager userDetailsManager;

	public CalculationFinishedRequestHandler() {
		super();
	}

	public CalculationFinishedRequestHandler(
			DeviceDetailsManager deviceDetailsManager,
			UserDetailsManager userDetailsManager) {
		super();
		this.deviceDetailsManager = deviceDetailsManager;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteAccount(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			String deviceID = (String) input.readObject();
			;
			output.writeBoolean(deviceDetailsManager.deregisterDevice(deviceID));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processResult(ObjectInputStream input,
			ObjectOutputStream output) {
		try {
			input.readObject();
			sendBecomeDormantMessage(output);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void registerDevice(ObjectInputStream input,
			ObjectOutputStream output) {
		RegistrationPack registrationPack;
		try {
			registrationPack = (RegistrationPack) input.readObject();
			output.reset();
			output.writeBoolean(deviceDetailsManager
					.addDevice(registrationPack));
			output.flush();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
