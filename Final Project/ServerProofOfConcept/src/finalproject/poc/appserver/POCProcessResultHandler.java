package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import finalproject.poc.calculationclasses.IResultsPacket;
import finalproject.poc.calculationclasses.IWorkPacket;
import finalproject.poc.calculationclasses.WorkPacket;

public class POCProcessResultHandler extends AbstractClientRequestHandler {

	public static LinkedList<Integer> numList = new LinkedList<Integer>();

	public POCProcessResultHandler() {
		super();		
		for (int count = 0; count < 10; count++) {
			numList.add(count);
		}

	}

	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			IResultsPacket result = (IResultsPacket) input.readObject();
			System.out.println("ID: " + result.getPacketId());
			System.out.println("Result: " + result.getResult());

			if (!numList.isEmpty()) {
				Integer num = numList.removeFirst();
				System.out.println("Number sent: " + num);
				String number = num + "";
				IWorkPacket workPacket = new WorkPacket(number.hashCode() + "", num);

				output.reset();
				output.writeInt(ServerRequest.NEW_CALCULATION.getRequestNum());
				output.writeObject(workPacket);
				output.flush();
				System.out.println("Work packet sent");
			} else {
				output.reset();
				output.writeInt(ServerRequest.BECOME_DORMANT.getRequestNum());
				output.flush();
				System.out.println("Client connection closed");
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void delegate(int requestNum, ObjectInputStream input,
			ObjectOutputStream output) {

	}

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.PROCESS_RESULT.getRequestNum();
	}

}
