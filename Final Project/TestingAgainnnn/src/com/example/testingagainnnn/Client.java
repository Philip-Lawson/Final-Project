package com.example.testingagainnnn;

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
	private static final String HOST = "10.0.2.2";
	private Socket client;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	protected boolean processingConnection = true;

	public Client() {

	}
	
	public void cancelConnection(){
		processingConnection = false;
	}

	private void connectToServer() throws UnknownHostException, IOException {
		client = new Socket(InetAddress.getByName(HOST), PORT_NUMBER);		
		
	}

	private void getStreams() throws IOException {
		input = new ObjectInputStream(client.getInputStream());
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush();		
	}
	
	private void registerWithServer() throws IOException {
		output.reset();
		output.writeInt(ClientRequest.REGISTER.getRequestNum());
		output.writeObject("Register");
		output.flush();		
	}

	private void processConnection() throws IOException {
		AbstractServerRequestHandler handler = new CalculationRequestHandler();
		handler.setNextHandler(new BecomeDormantRequestHandler());
		processingConnection = true;		

		while (processingConnection) {
			int requestNum = -1;
			requestNum = input.readInt();
			handler.processRequest(requestNum, input, output, this);			
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			connectToServer();
			getStreams();
			registerWithServer();
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
