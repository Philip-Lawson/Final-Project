/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import uk.ac.qub.finalproject.server.networking.AbstractClientRequestHandler;

/**
 * @author Phil
 *
 */
public class FallThroughRequestHandler extends AbstractClientRequestHandler {
	
	private boolean fallThrough;

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		fallThrough = true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		
	}
	
	public boolean hasFallenThrough(){
		return fallThrough;
	}

}
