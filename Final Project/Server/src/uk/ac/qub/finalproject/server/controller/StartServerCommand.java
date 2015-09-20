/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.server.networking.AbstractClientRequestHandler;
import uk.ac.qub.finalproject.server.networking.Server;

/**
 * The start server command starts the server and passes in a chain of request
 * handlers that will respond to all client requests.
 * 
 * @author Phil
 *
 */
public class StartServerCommand implements Command {

	private Thread serverThread;
	private Server server;
	private AbstractClientRequestHandler requestHandlerChain;

	/**
	 * Constructor sets the server and request handler chain references.
	 * 
	 * @param server
	 * @param requestHandlerChain
	 */
	public StartServerCommand(Server server,
			AbstractClientRequestHandler requestHandlerChain) {
		this.server = server;
		this.requestHandlerChain = requestHandlerChain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		if (null != server){
			server.setRequestHandlers(requestHandlerChain);
		}
		
		if (null == serverThread || !server.isListening()) {			
			serverThread = new Thread(server);
			serverThread.start();
		}

	}

}
