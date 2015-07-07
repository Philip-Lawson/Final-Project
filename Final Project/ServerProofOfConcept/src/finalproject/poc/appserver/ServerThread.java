package finalproject.poc.appserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread implements Runnable {

	private Socket socket;
	private SimpleServer server;

	private ObjectOutputStream out;
	private ObjectInputStream in;

	public ServerThread(Socket socket, SimpleServer server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			getStreams();
			processConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnections();
		}

	}

	private void getStreams() throws IOException {
		out = new ObjectOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
		out.flush();

		in = new ObjectInputStream(new BufferedInputStream(
				socket.getInputStream()));
	}

	private void processConnection() {
		AbstractClientRequestHandler registerHandler = new POCRegisterRequestHandler();
		AbstractClientRequestHandler resultsHandler = new POCProcessResultHandler();

		registerHandler.setNextHandler(resultsHandler);

		while (true) {
			try {
				System.out.println("Processing request");
				int requestNum = in.readInt();		
				System.out.println("Request read");
				registerHandler.processRequest(requestNum, in, out);

			} catch (IOException e) {
				// TODO Auto-generated catch block		
				System.out.println("Connection closed");
				break;
			}
		}

	}

	private void closeConnections() {
		try {
			if (out != null) out.close();
			if (in != null) in.close();			
			if (socket != null) socket.close();

		} catch (IOException IOEx) {
			// IOEx.printStackTrace();
			System.out.println("Problem closing connection");
		}
	}

}
