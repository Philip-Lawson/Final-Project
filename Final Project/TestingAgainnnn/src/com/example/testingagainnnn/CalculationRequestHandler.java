package com.example.testingagainnnn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class CalculationRequestHandler extends AbstractServerRequestHandler {
	
	private DummyProcessor processor;
	
	public CalculationRequestHandler(){
		super();
		setServerRequest(ServerRequest.NEW_CALCULATION);
		this.processor = new DummyProcessor();
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		
		try {
			IWorkPacket packet = (IWorkPacket) input.readObject();
			IResultsPacket result = processor.execute(packet);
			
			output.reset();
			output.writeInt(ClientRequest.PROCESS_RESULT.getRequestNum());
			output.writeObject(result);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
		
	
	
	
}
