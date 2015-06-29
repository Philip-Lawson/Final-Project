package com.example.testingagainnnn;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BecomeDormantRequestHandler extends AbstractServerRequestHandler {

	@Override
	protected void handleHere(ObjectInputStream input,
			ObjectOutputStream output, Client client) {
		// TODO Auto-generated method stub
		client.cancelConnection();
		
	}

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.BECOME_DORMANT.getRequestNum();
	}

}
