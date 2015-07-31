/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

import java.util.ArrayList;

/**
 * @author Phil
 *
 */
public class ResultsPacketList extends ArrayList<IResultsPacket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 806447377441322468L;
	
	private String deviceID;
	
	public ResultsPacketList(String deviceID){
		super();
		this.deviceID = deviceID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

}
