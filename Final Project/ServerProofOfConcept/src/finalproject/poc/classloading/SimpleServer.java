package finalproject.poc.classloading;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer implements Runnable {
	
	private ServerSocket server;
	private ExecutorService threadPool;
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server = new ServerSocket(12346);
			threadPool = Executors.newFixedThreadPool(100);
			
			while (true) {
				try {
					Socket connection = server.accept();
					threadPool.execute( new ServerThread(connection,
							this));					
					
				} catch (EOFException eofException) {
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Problem in Simple Server");
		} 
		
	}

}
