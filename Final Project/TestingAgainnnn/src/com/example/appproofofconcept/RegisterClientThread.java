package com.example.appproofofconcept;

import java.io.IOException;
import android.content.Context;
import android.provider.Settings.Secure;

public class RegisterClientThread extends Client {

	private String emailAddress;

	public RegisterClientThread() {
		super();
	}

	public RegisterClientThread(Context context) {
		super(context);
	}

	public RegisterClientThread(Context context, String emailAddress) {
		super(context);
		this.emailAddress = emailAddress;
	}

	public void communicateWithServer() throws IOException {

		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setAndroidID(Secure.getString(getContext()
				.getContentResolver(), Secure.ANDROID_ID));
		registrationPack.setEmailAddress(emailAddress);

		output.reset();
		output.writeInt(ClientRequest.REGISTER);
		output.writeObject(registrationPack);
		output.flush();
	}

}
