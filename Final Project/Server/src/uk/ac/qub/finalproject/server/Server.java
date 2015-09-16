package uk.ac.qub.finalproject.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.persistence.LoggingUtils;

public class Server implements Runnable {

	private Logger logger = LoggingUtils.getLogger(Server.class);

	private static final int THREAD_POOL_SIZE = 200;
	private static final int PORT = 12346;

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
