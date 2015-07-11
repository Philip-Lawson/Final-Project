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
public abstract class AbstractClientRequestHandler {

	private AbstractClientRequestHandler nextHandler;

	public AbstractClientRequestHandler() {

	}

	public void setNextHandler(AbstractClientRequestHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public void processRequest(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		
		if (requestNum == getRequestNum()) {
			handleHere(input, output);
		} else {
			delegate(requestNum, input, output);
		}
	}

	protected abstract int getRequestNum();

	protected abstract void handleHere(ObjectInputStream input,
			ObjectOutputStream output);

	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		nextHandler.processRequest(requestNum, input, output);
	}

}
