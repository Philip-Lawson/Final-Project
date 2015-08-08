/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public class CatchAllRequestHandler extends AbstractClientRequestHandler {

	@Override
	public void processRequest(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {

	}

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
		return 0;
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
		// fall through

	}

}
