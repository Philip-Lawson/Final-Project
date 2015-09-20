/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.persistence.ConnectionPool;
import uk.ac.qub.finalproject.persistence.DatabaseCreator;
import uk.ac.qub.finalproject.persistence.Device;
import uk.ac.qub.finalproject.persistence.DeviceDetailsJDBC;
import uk.ac.qub.finalproject.persistence.ResultsPacketJDBC;
import uk.ac.qub.finalproject.persistence.WorkPacketJDBC;

/**
 * @author Phil
 *
 */
public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResultsPacketJDBC test = new ResultsPacketJDBC();
		WorkPacketJDBC work = new WorkPacketJDBC();
		DeviceDetailsJDBC devices = new DeviceDetailsJDBC();
		Map<String, Device> map = devices.loadDevices();
		
		
			System.out.println(work.getWorkPackets().size());
			System.out.println(test.getResultsPackets().size());
			
			
			
			for (String deviceID: map.keySet()){
				System.out.println(map.get(deviceID).getValidResults());
			}
	
		
	}

}
