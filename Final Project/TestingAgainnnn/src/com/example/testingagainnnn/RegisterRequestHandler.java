package com.example.testingagainnnn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterRequestHandler extends AbstractServerRequestHandler {

	public RegisterRequestHandler() {
		super();
		setServerRequest(ServerRequest.REGISTER_REQUEST);
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			output.reset();
			output.writeInt(ClientRequest.REGISTER.getRequestNum());
			output.writeObject("Register Client");
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {

	}

}
