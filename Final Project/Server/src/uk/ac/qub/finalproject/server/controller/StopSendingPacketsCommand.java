/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.server.networking.AbstractClientRequestHandler;
import uk.ac.qub.finalproject.server.networking.Server;

/**
 * This command changes the request handlers used by the server to one that only
 * responds fully to account requests. All processing requests will be ignored.
 * 
 * @author Phil
 *
 */
public class StopSendingPacketsCommand implements Command {
	
	private Server server;
	private AbstractClientRequestHandler pauseServiceRequestHandler;

	public StopSendingPacketsCommand(Server server,
			AbstractClientRequestHandler pauseServiceRequestHandler) {
		this.server = server;
		this.pauseServiceRequestHandler = pauseServiceRequestHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		server.setRequestHandlers(pauseServiceRequestHandler);
	}

}
