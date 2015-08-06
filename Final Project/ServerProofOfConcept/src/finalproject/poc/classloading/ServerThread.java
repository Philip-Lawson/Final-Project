package finalproject.poc.classloading;

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
	private Class<?> classToSend = ConcreteCalculator.class;
	private byte[] classBytes;

	public ServerThread(Socket socket, SimpleServer server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			getStreams();
			sendClass();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnections();
		}

	}

	private void getStreams() throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();

		in = new ObjectInputStream(socket.getInputStream());
	}

	private void processConnection() {

	}

	private void writeClassToBytes() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytes);
		
		oos.flush();
		bytes.flush();
		oos.writeObject(classToSend);
		System.out.println("Class written");

		classBytes = bytes.toByteArray();
		System.out.println("Converted to byte array");
		bytes.close();
		oos.close();
	}

	public void changeClass(Class<?> classToStore) throws IOException {
		this.classToSend = classToStore;
		writeClassToBytes();
	}

	private void closeConnections() {
		try {
			if (out != null) {
				out.close();
			}

			if (in != null) {
				in.close();
			}

		} catch (IOException IOEx) {
			// IOEx.printStackTrace();
			System.out.println("Problem closing connection");
		}
	}

	private void sendClass() throws IOException, ClassNotFoundException {
		if (null == classBytes){
			writeClassToBytes();
		}

		out.writeInt(ServerRequest.LOAD_PROCESSING_CLASS);
		out.flush();
		out.writeObject(classToSend.getName());
		out.flush();

		System.out.println("Sending class");
		try {
			out.writeObject(classBytes);
		} catch (SocketException ex) {
			System.out.println("Problem sending class");
		}

	}

}
