/**
 * 
 */
package com.example.appproofofconcept;

import java.io.IOException;

import finalproject.poc.calculationclasses.IResultsPacket;
import android.content.Context;

/**
 * @author Phil
 *
 */
public class SendResultClientThread extends Client {
	
	private IResultsPacket results;
	
	public SendResultClientThread(){
		super();
	}
	
	public SendResultClientThread(Context context){
		super(context);
	}
	
	public SendResultClientThread(Context context, IResultsPacket results){
		super(context);
		this.results = results;
	}
	
	public void setResults(IResultsPacket results){
		this.results = results;
	}

	/* (non-Javadoc)
	 * @see com.example.appproofofconcept.Client#communicateWithServer()
	 */
	@Override
	protected void communicateWithServer() throws IOException {
		// TODO Auto-generated method stub
		output.reset();
		output.writeInt(ClientRequest.PROCESS_RESULT.getRequestNum());
		output.writeObject(results);
		output.flush();

	}

}
