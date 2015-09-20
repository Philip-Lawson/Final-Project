/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This catch all request handler is used as the last request handler in a chain
 * of request handlers.
 * 
 * @author Phil
 *
 */
public class CatchAllRequestHandler extends AbstractClientRequestHandler {

	@Override
	public void processRequest(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		handleHere(input, output);
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
