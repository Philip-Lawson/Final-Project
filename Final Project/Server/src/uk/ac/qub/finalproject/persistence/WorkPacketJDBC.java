/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;


/**
 * @author Phil
 *
 */
public class WorkPacketJDBC extends AbstractJDBC {

	private static final String ADD_WORK_PACKET = "INSERT INTO work_packets VALUES (?, ?);";
	private static final String GET_INCOMPLETE_WORK_PACKETS = "SELECT work_packet "
			+ "FROM work_packets JOIN results_packets ON work_packets.packet_id = results_packets.packet_id "
			+ "WHERE packet_id NOT IN results_packets;";
	private static final String GET_INDIVIDUAL_WORK_PACKET = "SELECT work_packet FROM work_packets WHERE packet_id = ?";

	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		Connection connection  = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(ADD_WORK_PACKET);

			for (IWorkPacket workPacket : workPackets) {
				preparedStatement.setString(1, workPacket.getPacketId());
				preparedStatement.setObject(2, workPacket);
				preparedStatement.executeUpdate();
			}

		} catch (SQLException | ClassNotFoundException SQLEx) {

		} finally {
			closeConnection(connection, preparedStatement, null);
		}

	}

	public Collection<IWorkPacket> getIncompleteWorkPackets() {
		List<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(1000);
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(GET_INCOMPLETE_WORK_PACKETS);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				workPackets.add((IWorkPacket) resultSet.getObject(1));
			}

		} catch (SQLException | ClassNotFoundException SQLEx) {

		} finally {			
			closeConnection(connection, preparedStatement, resultSet);
		}

		return workPackets;

	}
	
	public IWorkPacket getIndividualWorkPacket(String packetID){
		IWorkPacket workPacket = null;
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(GET_INDIVIDUAL_WORK_PACKET);
			preparedStatement.setString(1, packetID);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				workPacket = (IWorkPacket) resultSet.getObject(1);
			}

		} catch (SQLException | ClassNotFoundException SQLEx) {

		} finally {							
			closeConnection(connection, preparedStatement, resultSet);
		}

		return workPacket;

		
		
	}
}
