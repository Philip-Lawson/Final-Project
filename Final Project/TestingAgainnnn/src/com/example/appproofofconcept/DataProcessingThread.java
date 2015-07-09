/**
 * 
 */
package com.example.appproofofconcept;

import android.content.Context;
import android.os.Process;
import android.provider.Settings.Secure;
import finalproject.poc.calculationclasses.IDataProcessor;
import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.ResultsPacketList;
import finalproject.poc.calculationclasses.WorkPacketList;

/**
 * @author Phil
 *
 */
public class DataProcessingThread implements Runnable {
	
	private static IDataProcessor dataProcessor;
	private WorkPacketList workPacketList;
	private ResultsPacketList resultsPacketList;
	private Context context;
	
	public DataProcessingThread(WorkPacketList workPacketList, Context context){
		this.workPacketList = workPacketList;
		this.context = context.getApplicationContext();
		this.resultsPacketList = new ResultsPacketList();		
	}

	public static void changeProcessorClass(IDataProcessor newDataProcessor){
		dataProcessor = newDataProcessor;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (IWorkPacket workPacket: workPacketList){
			resultsPacketList.add(dataProcessor.execute(workPacket));
			// write/overwrite results packet list to file
			
			updateProgress(resultsPacketList.size(), workPacketList.size());
		}
		
		//start result sender thread
		SendResultClientThread resultSenderThread = new SendResultClientThread();
		resultSenderThread.setResults(resultsPacketList);
		
		Thread resultsThread = new Thread(resultSenderThread);
		resultsThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		resultsThread.start();
		
	}
	
	public void updateProgress(int packetsProcessed, int numberOfPackets){
		
	}
	


}
