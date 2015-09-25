/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacket;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;

/**
 * This is a dummy implementation of the work packet loader. It loads work
 * packets filled with integers. Each time load additional packets is called it
 * returns a new set of integer work packets.
 * 
 * @author Phil
 *
 */
public class DummyWorkPacketLoader extends AbstractWorkPacketLoader {

	private int packetCount = 20;

	public DummyWorkPacketLoader(AbstractWorkPacketDrawer workPacketDrawer) {
		super(workPacketDrawer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader#
	 * retrieveInitialWorkPackets()
	 */
	@Override
	protected Collection<IWorkPacket> retrieveInitialWorkPackets() {
		ArrayList<IWorkPacket> packetsList = new ArrayList<IWorkPacket>();

		for (int count = 0; count < 20; count++) {
			IWorkPacket workPacket = new WorkPacket(Integer.valueOf(count));
			packetsList.add(workPacket);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}

		return packetsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader#
	 * retrieveAdditionalWorkPackets()
	 */
	@Override
	protected Collection<IWorkPacket> retrieveAdditionalWorkPackets() {
		ArrayList<IWorkPacket> packetsList = new ArrayList<IWorkPacket>();

		for (int count = 0; count < 20; count++) {
			IWorkPacket workPacket = new WorkPacket(
					Integer.valueOf(packetCount++));
			packetsList.add(workPacket);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}

		return packetsList;
	}

}
