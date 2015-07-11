/**
 * 
 */
package com.example.appproofofconcept;

import java.util.Observable;

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
public class DataProcessor extends Observable implements Runnable {

	private static IDataProcessor processorClass;
	private static WorkPacketList workPacketList;
	private ResultsPacketList resultsPacketList;
	private Context context;

	public DataProcessor() {

	}

	public static void changeProcessorClass(IDataProcessor newDataProcessor) {
		processorClass = newDataProcessor;
	}

	public static void setWorkPacketList(WorkPacketList newWorkPacketList) {
		workPacketList = newWorkPacketList;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (null != processorClass && null != workPacketList) {
			resultsPacketList = new ResultsPacketList();
			for (IWorkPacket workPacket : workPacketList) {
				if (!Thread.currentThread().isInterrupted()) {
					resultsPacketList.add(processorClass.execute(workPacket));
					// write/overwrite results packet list to file

					updateProgress(resultsPacketList.size(),
							workPacketList.size());
				} else {
					break;
				}
			}

			setChanged();
			notifyObservers(resultsPacketList);
			
			this.resultsPacketList = null;
			workPacketList = null;
			
		} else if (null == processorClass) {
			// log request
			GetProcessingClassClientThread getClass = new GetProcessingClassClientThread();
			Thread getClassThread = new Thread(getClass);
			getClassThread.setPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
			getClassThread.start();
		}

	}

	public void updateProgress(int packetsProcessed, int numberOfPackets) {

	}

}
