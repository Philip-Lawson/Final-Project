/**
 * 
 */
package finalproject.poc.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import finalproject.poc.calculationclasses.IResultsPacket;

/**
 * @author Phil
 *
 */
public class ResultsPacketJDBC extends AbstractJDBC {

	private static final String ADD_RESULTS_PACKET = "INSERT INTO results_packets VALUES (?, ?);";
	private static final String GET_RESULTS_PACKETS = "SELECT results_packet FROM results_packets";

	public void writeResult(IResultsPacket resultsPacket) {
		try {
			createConnection();

			preparedStatement = connection.prepareStatement(ADD_RESULTS_PACKET);
			preparedStatement.setString(1, resultsPacket.getPacketId());
			preparedStatement.setObject(2, resultsPacket);
			preparedStatement.executeUpdate();
		} catch (SQLException SQLEx) {

		} finally {
			closeConnection();
		}
	}

	public Collection<IResultsPacket> getResultsPackets() {
		ResultSet resultSet = null;
		Collection<IResultsPacket> resultsPackets = new ArrayList<IResultsPacket>(1000);
		
		try {
			createConnection();

			preparedStatement = connection
					.prepareStatement(GET_RESULTS_PACKETS);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				resultsPackets.add( (IResultsPacket) resultSet.getObject(1));
			}			
		} catch (SQLException SQLEx) {

		} finally {

			try {
				if (null != resultSet)
					resultSet.close();
			} catch (SQLException e) {
				
			}
			
			closeConnection();
		}
		
		return resultsPackets;
	}
}
