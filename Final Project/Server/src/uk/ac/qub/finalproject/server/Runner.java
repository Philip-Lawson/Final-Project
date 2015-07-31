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
		Server server = new Server();
		Thread serverThread = new Thread(server);
		
		serverThread.start();
	}

}
