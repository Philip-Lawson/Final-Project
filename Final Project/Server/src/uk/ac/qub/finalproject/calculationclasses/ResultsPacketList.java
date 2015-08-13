/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

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
	
	private long timeStamp;
	
	public ResultsPacketList(){
		super();
	}
	
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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
