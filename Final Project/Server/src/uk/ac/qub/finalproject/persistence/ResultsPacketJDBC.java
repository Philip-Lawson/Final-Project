/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
 * This DAO performs CRUD operations on the results packet table. It allows the
 * user to add results packets to the database, retrieve results packets from
 * the database and determine if all work packets have an associated results
 * packet.
 * 
 * @author Phil
 *
 */
public class ResultsPacketJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(ResultsPacketJDBC.class);

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
			logger.log(Level.WARNING, ResultsPacketJDBC.class.getName()
					+ " Problem saving results packet to the database");
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Writes a list of results packets to the database in one batch.
	 * 
	 * @param resultsPacketList
	 */
	public void writeResultList(Collection<IResultsPacket> resultsPacketList) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(ADD_RESULTS_PACKET);

			for (IResultsPacket resultsPacket : resultsPacketList) {
				preparedStatement.setString(1, resultsPacket.getPacketId());
				preparedStatement.setObject(2, resultsPacket);

				try {
					// allow the loop to continue if there
					// is a problem with this particular update
					preparedStatement.executeUpdate();
				} catch (SQLException e) {

				}
			}

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, ResultsPacketJDBC.class.getName()
					+ " Problem saving results packets to the database");
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
		ArrayList<IResultsPacket> resultsPackets = new ArrayList<IResultsPacket>(
				1000);

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ByteArrayInputStream bytesIn = null;
		ObjectInputStream input = null;

		try {
			connection = createConnection();

			preparedStatement = connection
					.prepareStatement(GET_RESULTS_PACKETS);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				byte[] obj = (byte[]) resultSet.getObject("results_packet");
				bytesIn = new ByteArrayInputStream(obj);
				input = new ObjectInputStream(bytesIn);

				IResultsPacket packet = (IResultsPacket) input.readObject();
				resultsPackets.add(packet);
			}
		} catch (SQLException | PropertyVetoException | IOException
				| ClassNotFoundException SQLEx) {
			logger.log(Level.WARNING, ResultsPacketJDBC.class.getName()
					+ " Problem loading results packet from the database");
		} finally {
			closeConnection(connection, preparedStatement, resultSet);

			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {

			}

			try {
				if (bytesIn != null) {
					input.close();
				}
			} catch (IOException e) {

			}

		}

		resultsPackets.trimToSize();
		return resultsPackets;
	}

	/**
	 * This queries the database to see if all work packets have been processed.
	 * 
	 * @return true if all work packets have been processed.
	 */
	public boolean allResultsComplete() {
		boolean resultsComplete = false;
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = createConnection();

			statement = connection.createStatement();
			resultSet = statement.executeQuery(ALL_RESULTS_COMPLETE);

			if (resultSet.next()) {
				int unProcessedWorkPackets = resultSet.getInt(1);
				resultsComplete = (unProcessedWorkPackets == 0);
			}

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, ResultsPacketJDBC.class.getName()
					+ " Problem getting result statistics from the database");

		} finally {
			closeConnection(connection, statement, resultSet);
		}

		return resultsComplete;
	}
}
