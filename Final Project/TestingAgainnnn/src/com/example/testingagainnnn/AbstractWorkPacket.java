package com.example.testingagainnnn;

/**
 * Abstract implementation of IWorkPacket. To be sent from the server to
 * be processed by the client device. This stores the ID of the work packet
 * and the data to be processed.
 * 
 * @author Phil
 *
 */
public abstract class AbstractWorkPacket implements IWorkPacket {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3127345838271149066L;

	/**
	 * The ID of the work packet. This ID should be unique.
	 */
	private String packetId;

	/**
	 * The data to be processed.
	 */
	private Object initialData;

	/**
	 * Default constructor.
	 */
	public AbstractWorkPacket() {

	}

	/**
	 * Instantiates a work packet with a packet ID.
	 * 
	 * @param packetId
	 *            the ID of the work packet
	 */
	public AbstractWorkPacket(String packetId) {
		this.setPacketId(packetId);
	}

	/**
	 * Instantiates a work packet with a packet ID and the data to be processed.
	 * 
	 * @param packetId
	 *            the ID of the work packet
	 * @param initialData
	 *            the data to be processed
	 */
	public AbstractWorkPacket(String packetId, Object initialData) {
		this(packetId);
		this.setInitialData(initialData);
	}

	@Override
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	@Override
	public String getPacketId() {
		return packetId;
	}

	@Override
	public void setInitialData(Object initialData) {
		this.initialData = initialData;
	}

	@Override
	public Object getInitialData() {
		return initialData;
	}

}
