/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.qub.finalproject.server.networking.AbstractClientRequestHandler;

/**
 * Mock client request handler used to test that the
 * AbstractClientRequestHandler is working properly.
 * 
 * @author Phil
 *
 */
public class GenericClientRequestHandler extends AbstractClientRequestHandler {

	public static int REQUEST_NUM = 5;
	
	
	private boolean handledHere;
	private boolean delegated;	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.server.AbstractClientRequestHandler#getRequestNum
	 * ()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return REQUEST_NUM;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.server.AbstractClientRequestHandler#handleHere
	 * (java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		handledHere = true;
	}

	@Override
	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		
		super.delegate(requestNum, input, output);
		delegated = true;
	}

	public boolean getHandledHere() {
		return handledHere;
	}

	public boolean getDelegated() {
		return delegated;
	}
		
}
