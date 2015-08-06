package finalproject.poc.classloading;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


import java.security.Security;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client implements Runnable {

	private static final int PORT_NUMBER = 12346;
	private static final String HOST = "localhost";
	private Socket client;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public Client() {

	}

	private void connectToServer() throws IOException {
		/*System.setProperty("javax.net.ssl.keyStore","C:\\Users\\Phil\\Documents\\GitHub\\Final-Project\\Final Project\\ClientNonAppProofOfConcept\\testkeys");
		System.setProperty("javax.net.ssl.keyStorePassword","passphrase"); 

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		client = (SSLSocket) factory.createSocket(InetAddress.getByName(HOST), PORT_NUMBER);*/
				
		client = new Socket(InetAddress.getByName(HOST), PORT_NUMBER);
	}

	private void getStreams() throws IOException {
		input = new ObjectInputStream(client.getInputStream());
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush();
		System.out.println("Streams loaded");
	}

	private void processConnection() throws IOException {
		AbstractServerRequestHandler handler = new LoadCalculationClassRequestHandler();
		boolean processingConnection = true;

		output.reset();
		output.writeInt(ClientRequest.REQUEST_PROCESSING_CLASS);
		///output.writeObject(new RegistrationPack());
		output.flush();
		System.out.println("Request written");

		System.out.println(input.readBoolean());
	}
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			connectToServer();
			getStreams();
			processConnection();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (output != null)	output.close();
				if (input != null)	input.close();
				if (client != null)	client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
