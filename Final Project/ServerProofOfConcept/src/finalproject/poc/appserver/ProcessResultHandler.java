package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.calculationclasses.IResultsPacket;

public class ProcessResultHandler extends AbstractClientRequestHandler {

	public ProcessResultHandler() {
		super();
		setClientRequest(ClientRequest.PROCESS_RESULT);
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			IResultsPacket result = (IResultsPacket) input.readObject();
			System.out.println("ID: " + result.getPacketId());
			System.out.println("Result: " + result.getResult());
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {

	}

}
