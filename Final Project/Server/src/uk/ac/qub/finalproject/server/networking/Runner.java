/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.beans.PropertyVetoException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.persistence.ConnectionPool;
import uk.ac.qub.finalproject.persistence.DatabaseCreator;
import uk.ac.qub.finalproject.persistence.Device;
import uk.ac.qub.finalproject.persistence.DeviceDetailsJDBC;
import uk.ac.qub.finalproject.persistence.ResultsPacketJDBC;
import uk.ac.qub.finalproject.persistence.WorkPacketJDBC;
import uk.ac.qub.finalproject.server.views.NumberFormatter;

/**
 * @author Phil
 *
 */
public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		ConnectionPool pool = ConnectionPool.getConnectionPool();
		
			Connection connection = pool.getConnection();
			Statement statement = connection.createStatement();
			statement.addBatch("DELETE FROM results_packets");
			statement.addBatch("DELETE FROM work_packets");
			statement.addBatch("DELETE FROM users");
			statement.addBatch("DELETE FROM devices");
			
			statement.executeBatch();
			
			statement.close();
			connection.close();
			
			pool.closeConnectionPool();
		} catch (PropertyVetoException| SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		/*
		ResultsPacketJDBC test = new ResultsPacketJDBC();
		System.out.println(test.getResultsPackets().size()); */
		
		try {
			Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
			
			while(networks.hasMoreElements()){
				Enumeration<InetAddress> addresses = networks.nextElement().getInetAddresses();
				while(addresses.hasMoreElements()){
					System.out.println(addresses.nextElement().getHostAddress());
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}

}
