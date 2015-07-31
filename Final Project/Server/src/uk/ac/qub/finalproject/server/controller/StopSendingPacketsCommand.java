/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.server.AbstractClientRequestHandler;
import uk.ac.qub.finalproject.server.Server;

/**
 * @author Phil
 *
 */
public class StopSendingPacketsCommand implements Command {
	
	private Server server;
	
	private AbstractClientRequestHandler pauseServiceRequestHandler;
	
	public StopSendingPacketsCommand(){
		
	}
	
	public StopSendingPacketsCommand(Server server){
		setServer(server);
	}
	
	public StopSendingPacketsCommand(Server server, AbstractClientRequestHandler pauseServiceRequestHandler){
		setServer(server);
		setRequestHandler(pauseServiceRequestHandler);
	}
	
	public void setServer(Server server){
		this.server = server;
	}
	
	public void setRequestHandler(AbstractClientRequestHandler pauseServiceRequestHandler){
		this.pauseServiceRequestHandler = pauseServiceRequestHandler;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		server.setRequestHandlers(pauseServiceRequestHandler);

	}

}
