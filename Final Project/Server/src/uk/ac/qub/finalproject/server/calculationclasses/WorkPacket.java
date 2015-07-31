/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

import java.io.Serializable;

/**
 * Concrete work packet. This will be sent to the client device to be processed.
 * The work packet stores the data to be processed and a packet ID. <br>
 * </br>The packet ID should be unique.
 * 
 * <br>
 * </br>Can be instantiated with a default constructor, with the initial data
 * and a default packet ID or with the packet ID and the data to be processed.
 * 
 * @author Phil
 *
 */
public class WorkPacket extends AbstractWorkPacket implements Serializable {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 4893290929764015278L;

	/**
	 * Default constructor.
	 */
	public WorkPacket() {
		super();
	}

	/**
	 * This constructor initialises the work packet with a default packet ID.
	 * 
	 * @param initialData
	 *            the data to be processed
	 */
	public WorkPacket(Object initialData) {
		super(initialData);
	}

	/**
	 * Initialises the work packet with a packet ID and the data to be
	 * processed.
	 * 
	 * @param packetId
	 *            the ID of the work packet
	 * @param initialData
	 *            the data to be processed
	 */
	public WorkPacket(String packetId, Object initialData) {
		super(packetId, initialData);
	}

}
