/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;

/**
 * This DAO encapsulates requests to write or read information regarding results
 * packets to and from the database.
 * 
 * @author Phil
 *
 */
public class ResultsPacketJDBC extends AbstractJDBC {
	
	private static Logger logger = Logger.getLogger(ResultsPacketJDBC.class.getName());

	private static final String ADD_RESULTS_PACKET = "INSERT INTO results_packets VALUES (?, ?);";
	private static final String GET_RESULTS_PACKETS = "SELECT results_packet FROM results_packets";
	private static final String ALL_RESULTS_COMPLETE = "SELECT COUNT(packet_id) "
			+ " FROM work_packets WHERE packet_id NOT IN "
			+ "(SELECT packet_id FROM results_packets)";

	/**
	 * Writes a result to the database.
	 * 
	 * @param resultsPacket
	 */
	public void writeResult(IResultsPacket resultsPacket) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(ADD_RESULTS_PACKET);
			preparedStatement.setString(1, resultsPacket.getPacketId());
			preparedStatement.setObject(2, resultsPacket);
			preparedStatement.executeUpdate();
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, "Problem saving results packet to the database", SQLEx);
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Retrieves all the results that are currently stored in the database as a
	 * collection.
	 * 
	 * @return
	 */
	public Collection<IResultsPacket> getResultsPackets() {
		ResultSet resultSet = null;
		Collection<IResultsPacket> resultsPackets = new ArrayList<IResultsPacket>(
				1000);

		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = createConnection();

			preparedStatement = connection
					.prepareStatement(GET_RESULTS_PACKETS);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				resultsPackets.add((IResultsPacket) resultSet.getObject(1));
			}
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, "Problem loading results packet from the database", SQLEx);
		} finally {
			closeConnection(connection, preparedStatement, resultSet);
		}

		return resultsPackets;
	}

	public boolean allResultsComplete() {
		boolean resultsComplete = false;		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = createConnection();

			statement = connection.createStatement();
			resultSet = statement.executeQuery(ALL_RESULTS_COMPLETE);
			
			if(resultSet.next()){
				int unProcessedWorkPackets = resultSet.getInt(1);
				resultsComplete = (unProcessedWorkPackets == 0);
			}
			
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, "Problem getting result statistics from the database", SQLEx);

		} finally {
			closeConnection(connection, statement, resultSet);
		}

		return resultsComplete;
	}
}
