package uk.ac.qub.finalproject.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			e.printStackTrace();
		} finally {
			closeConnections();
		}

	}

	/**
	 * Helper method retrieves the streams used and wraps them in buffered object
	 * streams. This is necessary to allow the request handlers to read and
	 * write objects to the streams.
	 * 
	 * @throws IOException
	 *             if there is an issue with retrieving the socket's streams.
	 */
	private void getStreams() throws IOException {
		out = new ObjectOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
		out.flush();

		in = new ObjectInputStream(new BufferedInputStream(
				socket.getInputStream()));
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
			// IOEx.printStackTrace();
			System.out.println("Problem closing output stream");
		}
		
		try {
			if (in != null)
				in.close();
		} catch (IOException IOEx) {
			// IOEx.printStackTrace();
			System.out.println("Problem closing input stream");
		}
		
		try {
			if (socket != null)
				socket.close();
		} catch (IOException IOEx) {
			// IOEx.printStackTrace();
			System.out.println("Problem closing connection");
		}
	}

}
