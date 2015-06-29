package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.WorkPacket;

public class RegisterRequestHandler extends AbstractClientRequestHandler {
		

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			String clientRequest = (String) input.readObject();			
			System.out.println(clientRequest);
			IWorkPacket workPacket = new WorkPacket("DummyID", Integer.valueOf(2));
			
			output.reset();
			output.writeInt(ServerRequest.NEW_CALCULATION.getRequestNum());
			output.writeObject(workPacket);
			output.flush();
			System.out.println("Work packet sent");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.REGISTER.getRequestNum();
	}

}
