/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The abstract base class of all request handlers that handle the interaction
 * with client threads. This follows the Chain of Responsibility pattern found
 * in the GoF book. Each request handler has a request number and a handler. If
 * the request number matches the client request the request will be processed,
 * otherwise it will be delegated to the next handler. To avoid a null pointer
 * or other bugs the last handler in a chain must be a catch all handler.
 * 
 * @author Phil
 *
 */
public abstract class AbstractClientRequestHandler {

	private AbstractClientRequestHandler nextHandler;

	/**
	 * Default constructor. Before being used the setNextHandler request must be
	 * called, unless it is a fall through request handler.
	 */
	public AbstractClientRequestHandler() {

	}

	/**
	 * Sets the next handler in the system.
	 * 
	 * @param nextHandler
	 *            the next client request handler.
	 */
	public void setNextHandler(AbstractClientRequestHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	/**
	 * Processes the client's request. If the request can be handled within this
	 * handler it will, otherwise the request will be delegated to the next
	 * handler.
	 * 
	 * @param requestNum
	 * @param input
	 * @param output
	 */
	public void processRequest(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {

		if (requestNum == getRequestNum()) {
			handleHere(input, output);
		} else {
			delegate(requestNum, input, output);
		}
	}

	/**
	 * Returns the client request associated with this handler.
	 * 
	 * @return the client request
	 */
	protected abstract int getRequestNum();

	/**
	 * Handles the request in this handler, usually by reading from the client
	 * and writing information to the client.
	 * 
	 * @param input
	 *            the client's input stream.
	 * @param output
	 *            the client's output stream.
	 */
	protected abstract void handleHere(ObjectInputStream input,
			ObjectOutputStream output);

	/**
	 * Delegates the request to the next handler if it cannot be handled here.
	 * 
	 * @param requestNum
	 *            the client request.
	 * @param input
	 *            the client's input stream.
	 * @param output
	 *            the client's output stream.
	 */
	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {
		nextHandler.processRequest(requestNum, input, output);
	}

}
