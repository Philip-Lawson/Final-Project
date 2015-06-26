package com.example.testingagainnnn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread implements Runnable{
	private static int PORT = 12346;
	private static final String SERVER_IP = "10.0.2.2";
	
	private AbstractServerRequestHandler handler;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			InetAddress address = InetAddress.getByName(SERVER_IP);
			socket = new Socket(address, PORT);
			AbstractServerRequestHandler handler = new CalculationRequestHandler();
			handler.setNextHandler(new RegisterRequestHandler());

			getStreams();
			registerWithServer();
			processConnection();
			//closeConnection();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void getStreams() throws StreamCorruptedException,
			IOException {
		input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		output.flush();
	}
	
	public void registerWithServer() throws IOException{
		output.reset();
		output.writeInt(ClientRequest.REGISTER.getRequestNum());
		output.writeObject("Register Client");
		output.flush();
	}
	
	public void processConnection() throws IOException{	
		
		
		while (true){
			int requestNum = input.readInt();
			handler.processRequest(requestNum, input, output);					
		}
					
	}
	
	public void closeConnection() throws IOException{
		socket.close();
		input.close();
		output.close();
	}		


}
