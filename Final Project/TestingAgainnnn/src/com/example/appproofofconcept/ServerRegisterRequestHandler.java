package com.example.appproofofconcept;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.provider.Settings.Secure;

public class ServerRegisterRequestHandler extends AbstractServerRequestHandler {

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.REGISTER_REQUEST;
	}

	@Override
	protected void handleHere(ObjectInputStream input,
			ObjectOutputStream output, Client client) {
		// TODO Auto-generated method stub
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setAndroidID(Secure.getString(
				context.getContentResolver(), Secure.ANDROID_ID));

		try {
			output.reset();
			output.writeInt(ClientRequest.REGISTER);
			output.writeObject(registrationPack);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
