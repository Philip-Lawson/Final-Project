/**
 * 
 */
package com.example.appproofofconcept;

import java.io.IOException;

import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.ResultsPacketList;
import android.content.Context;
import android.provider.Settings.Secure;

/**
 * @author Phil
 *
 */
public class SendResultClientThread extends Client {
	
	private ResultsPacketList results;
	
	public SendResultClientThread(){
		super();
	}
	
	public SendResultClientThread(Context context){
		super(context);
	}
	
	public SendResultClientThread(Context context, ResultsPacketList results){
		super(context);
		this.setResults(results);
		
	}
	
	public void setResults(ResultsPacketList results){
		this.results = results;
		results.setDeviceID(Secure.getString(getContext()
				.getContentResolver(), Secure.ANDROID_ID));
	}

	/* (non-Javadoc)
	 * @see com.example.appproofofconcept.Client#communicateWithServer()
	 */
	@Override
	protected void communicateWithServer() throws IOException {
		// TODO Auto-generated method stub
		output.reset();
		output.writeInt(ClientRequest.PROCESS_RESULT);
		output.writeObject(results);
		output.flush();

	}

}
