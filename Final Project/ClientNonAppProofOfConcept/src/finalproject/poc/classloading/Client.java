package finalproject.poc.classloading;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	private static final int PORT_NUMBER = 12346;
	private static final String HOST = "localhost";
	private Socket client;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public Client() {

	}

	private void connectToServer() throws UnknownHostException, IOException {
		client = new Socket(InetAddress.getByName(HOST), PORT_NUMBER);
		
	}

	private void getStreams() throws IOException {
		input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
		output = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
		output.flush();
		System.out.println("Streams loaded");
	}

	private void processConnection() throws IOException {
		AbstractServerRequestHandler handler = new LoadCalculationClassRequestHandler();
		boolean processingConnection = true;
		
		output.reset();
		output.writeInt(0);
		output.writeObject("Register");
		System.out.println("Request written");

		while (processingConnection) {
			int requestNum = input.readInt();
			handler.processRequest(requestNum, input, output);			
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			connectToServer();
			getStreams();
			processConnection();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				client.close();
				output.close();
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
