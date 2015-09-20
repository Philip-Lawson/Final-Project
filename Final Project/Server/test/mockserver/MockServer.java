/**
 * 
 */
package mockserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import uk.ac.qub.finalproject.calculationclasses.WorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacketList;
import uk.ac.qub.finalproject.server.networking.ClientRequest;
import uk.ac.qub.finalproject.server.networking.ServerRequest;
/**
 * This is a mock server used to test the running application. It provides stub
 * responses to registering, deleting, retrieving a work packet and next work
 * packet requests. These responses can be configured using mutator methods.
 * 
 * @author Phil
 *
 */
public class MockServer implements Runnable {

	private static boolean REGISTRATION_SUCCESSFUL = true;
	private static boolean DELETE_SUCCESSFUL = true;
	private static int SEND_WORK_PACKET_RESPONSE = ServerRequest.PROCESS_WORK_PACKETS;
	private static int PROCESS_WORK_PACKETS_RESPONSE = ServerRequest.BECOME_DORMANT;
	private static WorkPacketList packetList = generateList();

	private void processRequest(int request, ObjectInputStream input,
			ObjectOutputStream output) throws OptionalDataException,
			ClassNotFoundException, IOException {
		
		switch (request) {
		case ClientRequest.REGISTER:
			input.readObject();
			output.writeBoolean(REGISTRATION_SUCCESSFUL);
			output.flush();
			System.out.println("Registration message sent");
			break;
		case ClientRequest.DEREGISTER_DEVICE:
			input.readObject();
			output.reset();
			output.writeBoolean(DELETE_SUCCESSFUL);
			output.flush();
			System.out.println("Delete message sent.");
			break;
		case ClientRequest.REQUEST_WORK_PACKET:
			output.reset();
			output.writeInt(SEND_WORK_PACKET_RESPONSE);
			output.writeObject(packetList);	
			output.flush();
			System.out.println("Packet List sent");
			break;
		case ClientRequest.PROCESS_RESULT:
			Object object = input.readObject();
			System.out.println(object.toString());
			output.writeInt(PROCESS_WORK_PACKETS_RESPONSE);
			output.flush();
			System.out.println("Dormant Message Sent");
			break;
		default: break;
		}		
	}
	
	private static WorkPacketList generateList(){
		WorkPacketList list = new WorkPacketList();
		
		for (int count=0; count<10; count++){
			list.add(new WorkPacket(count + "", Integer.valueOf(count)));
		}
		
		list.setTimeStamp(Long.valueOf(new Date().getTime()));
		return list;
	}

	@Override
	public void run() {		
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(12346);
			
			while (true){
				Socket socket = server.accept();
				System.out.println("Connection created");
				
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				
				int clientRequest = input.readInt();
				System.out.println("Request Received: " + clientRequest);
				
				try {
					processRequest(clientRequest, input, output);
				} catch (ClassNotFoundException e) {					
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != server) server.close();
			} catch (IOException e){
				
			}
		}

	}

	public static void main(String[] args) {
		MockServer mockServer = new MockServer();
		new Thread(mockServer).start();
	}
}
