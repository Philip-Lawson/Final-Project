/**
 * 
 */
package finalproject.poc.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import finalproject.poc.calculationclasses.IWorkPacket;

/**
 * @author Phil
 *
 */
public class WorkPacketJDBC extends AbstractJDBC {

	private static final String ADD_WORK_PACKET = "INSERT INTO work_packets VALUES (?, ?);";
	private static final String GET_INCOMPLETE_WORK_PACKETS = "SELECT work_packet "
			+ "FROM work_packets JOIN results_packets ON work_packets.packet_id = results_packets.packet_id "
			+ "WHERE packet_id NOT IN results_packets;";

	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		try {
			createConnection();
			preparedStatement = connection.prepareStatement(ADD_WORK_PACKET);

			for (IWorkPacket workPacket : workPackets) {
				preparedStatement.setString(1, workPacket.getPacketId());
				preparedStatement.setObject(2, workPacket);
				preparedStatement.executeUpdate();
			}

		} catch (SQLException SQLEx) {

		} finally {
			closeConnection();
		}

	}

	public Collection<IWorkPacket> getIncompleteWorkPackets() {

		List<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(1000);
		ResultSet resultSet = null;
		try {
			createConnection();
			preparedStatement = connection
					.prepareStatement(GET_INCOMPLETE_WORK_PACKETS);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				workPackets.add((IWorkPacket) resultSet.getObject(1));
			}

		} catch (SQLException SQLEx) {

		} finally {
			
				try {
					if (null != resultSet) resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			closeConnection();
		}

		return workPackets;

	}
}
