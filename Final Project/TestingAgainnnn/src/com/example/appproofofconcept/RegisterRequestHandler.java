package com.example.appproofofconcept;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterRequestHandler extends AbstractServerRequestHandler {

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output, Client client) {
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
			ObjectOutputStream output, Client client) {
		// fall through for now
	}

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.REGISTER_REQUEST.getRequestNum();
	}

}
