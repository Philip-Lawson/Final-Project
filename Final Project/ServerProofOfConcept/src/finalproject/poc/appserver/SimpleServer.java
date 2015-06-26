package finalproject.poc.appserver;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer implements Runnable {
	
	private ServerSocket server;
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server = new ServerSocket(12346);
			
			while (true) {
				
					Socket connection = server.accept();
					Thread serverThread = new Thread( new ServerThread(connection,
							this));					
					
					serverThread.start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Problem in Simple Server");
		} 
		
	}

}
