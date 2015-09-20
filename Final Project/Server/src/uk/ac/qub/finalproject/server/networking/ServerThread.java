package uk.ac.qub.finalproject.server.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.persistence.LoggingUtils;

/**
 * A server thread is created for each conneciton that is established with the
 * server. Each server thread processes and responds to requests from a client.
 * Each request is processed by the request handler which has access to the
 * underlying system. In this system the server continues listening until the
 * client closes the connection.
 * 
 * @author Phil
 *
 */
public class ServerThread implements Runnable {

	private Logger logger = LoggingUtils.getLogger(ServerThread.class);

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private AbstractClientRequestHandler requestHandler;

	public ServerThread(Socket socket,
			AbstractClientRequestHandler requestHandler) {
		this.socket = socket;
		this.requestHandler = requestHandler;
	}

	@Override
	public void run() {

		try {
			getStreams();
			processConnection();
		} catch (IOException e) {
			logger.log(Level.FINE, ServerThread.class.getName()
					+ " Problem getting the streams", e);
		} finally {
			closeConnections();
		}

	}

	/**
	 * Helper method retrieves the streams used and wraps them in buffered
	 * object streams. This is necessary to allow the request handlers to read
	 * and write objects to the streams.
	 * 
	 * @throws IOException
	 *             if there is an issue with retrieving the socket's streams.
	 */
	private void getStreams() throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}

	/**
	 * Helper method reads in the client's request and passes it on to the
	 * request handler for further processing.
	 */
	private void processConnection() {

		while (true) {
			try {
				int requestNum = in.readInt();
				requestHandler.processRequest(requestNum, in, out);
			} catch (IOException e) {
				// this will usually occur when the client has closed its
				// connection
				break;
			}
		}

	}

	/**
	 * Helper method closes the connections once client/server communication has
	 * ceased.
	 */
	private void closeConnections() {
		try {
			if (out != null)
				out.close();
		} catch (IOException IOEx) {
			logger.log(Level.FINE, ServerThread.class.getName()
					+ " Problem closing the output stream", IOEx);
		}

		try {
			if (in != null)
				in.close();
		} catch (IOException IOEx) {
			logger.log(Level.FINE, ServerThread.class.getName()
					+ " Problem closing the input stream", IOEx);
		}

		try {
			if (socket != null)
				socket.close();
		} catch (IOException IOEx) {
			logger.log(Level.FINE, ServerThread.class.getName()
					+ " Problem closing the socket", IOEx);
		}
	}

}
