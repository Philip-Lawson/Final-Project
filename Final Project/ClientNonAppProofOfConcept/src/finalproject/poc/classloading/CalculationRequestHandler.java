package finalproject.poc.classloading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.SocketException;

import finalproject.poc.calculationclasses.DummyProcessor;
import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.IWorkPacket;

public class CalculationRequestHandler extends AbstractServerRequestHandler {

	private DummyProcessor processor;

	public CalculationRequestHandler() {
		super();		
		this.processor = new DummyProcessor();
	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub

		try {
			System.out.println("Reading objects");
			IWorkPacket packet = (IWorkPacket) input.readObject();
			IResultsPacket result = processor.execute(packet);
			System.out.println("Objects read");
			System.out.println(result.getPacketId() + result.getResult());

			output.reset();
			output.writeInt(ClientRequest.PROCESS_RESULT.getRequestNum());
			System.out.println("int written");

			output.writeObject(result);

			System.out.println("Finished sending data");
			output.flush();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.NEW_CALCULATION.getRequestNum();
	}

}
