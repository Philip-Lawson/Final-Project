/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;

import uk.ac.qub.finalproject.server.calculationclasses.IWorkPacket;

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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader#retrieveAdditionalWorkPackets()
	 */
	@Override
	protected Collection<IWorkPacket> retrieveAdditionalWorkPackets() {
		// TODO Auto-generated method stub
		return null;
	}

}
