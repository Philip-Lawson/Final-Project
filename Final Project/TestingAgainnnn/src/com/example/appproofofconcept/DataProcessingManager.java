/**
 * 
 */
package com.example.appproofofconcept;

import finalproject.poc.calculationclasses.WorkPacketList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

/**
 * @author Phil
 *
 */
public class DataProcessingManager extends BroadcastReceiver {
	
	private Thread dataProcessor;
	
	public DataProcessingManager(WorkPacketList workPacketList, Context context){
		dataProcessor = new Thread(new DataProcessingThread(workPacketList, context));
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (canProcessWorkPackets()){
			startProcessing();
		}
	}
	
	public void startProcessing(){
		dataProcessor.setPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
		dataProcessor.start();
	}
	
	public boolean canProcessWorkPackets(){
		return true;
	}

}
