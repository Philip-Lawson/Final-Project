/**
 * 
 */
package finalproject.poc.appserver;

/**
 * @author Phil
 *
 */
public class ServerRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread serverThread = new Thread(new SimpleServer());
		serverThread.start();
	}

}
