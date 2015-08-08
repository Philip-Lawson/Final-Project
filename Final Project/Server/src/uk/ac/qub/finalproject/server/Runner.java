/**
 * 
 */
package uk.ac.qub.finalproject.server;

/**
 * @author Phil
 *
 */
public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AbstractClientRequestHandler requestHandler = new RegisterRequestHandler();
		AbstractClientRequestHandler deleteAccountRequestHandler = new DeleteAccountRequestHandler();
		requestHandler.setNextHandler(deleteAccountRequestHandler);	
		
		Server server = new Server();		
		server.setRequestHandlers(requestHandler);
		
		Thread serverThread = new Thread(server);
		
		serverThread.start();
	}

}
