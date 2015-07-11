package com.example.appproofofconcept;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import finalproject.poc.calculationclasses.DummyProcessor;
import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.WorkPacketList;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Process;
import android.support.v4.app.NotificationCompat;

public class CalculationRequestHandler extends AbstractServerRequestHandler {
	
		
	public CalculationRequestHandler(){
		super();		
	}
	
	public CalculationRequestHandler(Context context){
		super(context);		
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output, Client client) {
		// TODO Auto-generated method stub
		
		try {
			WorkPacketList workPacketList = (WorkPacketList) input.readObject();
			DataProcessingManager.getInstance().startProcessing(workPacketList); 
			client.cancelConnection();
			//IResultsPacket result = processor.execute(packet);
			
			/*output.reset();
			output.writeInt(ClientRequest.PROCESS_RESULT.getRequestNum());
			//output.writeObject(result);
			output.flush();*/
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

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.PROCESS_WORK_PACKETS;
	}
		
	
	
	
}
