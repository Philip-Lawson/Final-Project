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
public class StartServerCommand implements Command {
	
	private Thread serverThread;
	
	private Server server;
	
	private AbstractClientRequestHandler requestHandlerChain;
	
	public StartServerCommand() {
		
	}
	
	public StartServerCommand(Server server){
		setServer(server);
	}
	
	public void setServer(Server server){
		this.server= server;
	}
	
	public void setRequestHandlerChain(AbstractClientRequestHandler requestHandlerChain){
		this.requestHandlerChain = requestHandlerChain;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		if (!serverThread.isAlive()){
			server.setRequestHandlers(requestHandlerChain);
			serverThread = new Thread(server);
			serverThread.start();
		}

	}

}
