/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

/**
 * @author Phil
 *
 */
public class DummyWorkPacketLoader extends AbstractWorkPacketLoader {

	public DummyWorkPacketLoader(AbstractWorkPacketDrawer workPacketDrawer) {
		super(workPacketDrawer);		
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader#retrieveInitialWorkPackets()
	 */
	@Override
	protected Collection<IWorkPacket> retrieveInitialWorkPackets() {
		return new ArrayList<IWorkPacket>();
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader#retrieveAdditionalWorkPackets()
	 */
	@Override
	protected Collection<IWorkPacket> retrieveAdditionalWorkPackets() {		
		return new ArrayList<IWorkPacket>();
	}

}
