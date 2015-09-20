package uk.ac.qub.finalproject.server.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.persistence.LoggingUtils;
import uk.ac.qub.finalproject.server.implementations.Implementations;

/**
 * The server is the main point of access for the client. It listens out for
 * client connections and spawns a server thread for each new connection with a
 * reference to its chain of request handlers. Note that the server has no
 * knowledge of the underlying system.
 * 
 * @author Phil
 *
 */
public class Server implements Runnable {

	private Logger logger = LoggingUtils.getLogger(Server.class);

	private static final int THREAD_POOL_SIZE = 200;
	private static final int PORT = Implementations.getServerPort();

	private ExecutorService threadPool;
	private AbstractClientRequestHandler requestHandler;
	private boolean listening = true;
	private ServerSocket server;

	/**
	 * This helper method creates a socket. It is used so that a secure socket
	 * implementation can be added later.
	 * 
	 * @param port
	 * @return
	 * @throws IOException
	 */
	private ServerSocket getServerSocket(int port) throws IOException {
		return new ServerSocket(port);
	}

	public boolean isListening() {
		return listening;
	}

	/**
	 * Allows the server to be stopped externally.
	 */
	public void stopServer() {
		listening = false;
		try {
			if (null != server)
				server.close();
		} catch (IOException e) {
			logger.log(Level.FINE, Server.class.getName()
					+ " Problem closing the server", e);
		}
	}

	/**
	 * Sets the chain of request handlers used by the server. Used to
	 * dynamically change the server's response to certain client requests.
	 * 
	 * @param requestHandler
	 */
	public void setRequestHandlers(AbstractClientRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	@Override
	public void run() {
		try {
			server = getServerSocket(PORT);
			listening = true;
			threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

			while (listening) {
				Socket connection = server.accept();
				threadPool
						.execute(new ServerThread(connection, requestHandler));
			}

		} catch (IOException e) {
			logger.log(Level.FINE, Server.class.getName()
					+ " Closing the server", e);
		}
	}

}
