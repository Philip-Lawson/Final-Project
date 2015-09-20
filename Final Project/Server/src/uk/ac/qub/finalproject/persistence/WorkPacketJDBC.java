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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

/**
 * The DAO used for work packet CRUD actions.
 * 
 * @author Phil
 *
 */
public class WorkPacketJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(WorkPacketJDBC.class);

	private static final String ADD_WORK_PACKET = "INSERT INTO work_packets VALUES (?, ?);";
	private static final String GET_INCOMPLETE_WORK_PACKETS = "SELECT work_packet "
			+ "FROM work_packets WHERE packet_id NOT IN "
			+ "(SELECT packet_id FROM results_packets);";
	private static final String GET_INDIVIDUAL_WORK_PACKET = "SELECT work_packet FROM work_packets WHERE packet_id = ?";

	/**
	 * Adds a collection of work packets to the database.
	 * 
	 * @param workPackets
	 */
	public void addWorkPackets(Collection<IWorkPacket> workPackets) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(ADD_WORK_PACKET);

			for (IWorkPacket workPacket : workPackets) {
				preparedStatement.setString(1, workPacket.getPacketId());
				preparedStatement.setObject(2, workPacket);

				try {
					// if this update fails due to an attempt
					// to insert the same packet twice, the loop
					// should continue
					preparedStatement.executeUpdate();
				} catch (SQLException SQLEx) {
					logger.log(Level.WARNING,
							WorkPacketJDBC.class.getName()
									+ " Could not add work packet "
									+ workPacket.getPacketId());
				}

			}

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, WorkPacketJDBC.class.getName()
					+ " Could not add work packets");
		} finally {
			closeConnection(connection, preparedStatement, null);
		}

	}

	/**
	 * Returns a list of incomplete work packets from the database.
	 * 
	 * @return
	 */
	public Collection<IWorkPacket> getIncompleteWorkPackets() {
		List<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(1000);

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ByteArrayInputStream bytesIn = null;
		ObjectInputStream input = null;

		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(GET_INCOMPLETE_WORK_PACKETS);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				byte[] bytes = (byte[]) resultSet.getObject(1);
				bytesIn = new ByteArrayInputStream(bytes);
				input = new ObjectInputStream(bytesIn);

				IWorkPacket workPacket = (IWorkPacket) input.readObject();
				workPackets.add(workPacket);
			}

		} catch (SQLException | PropertyVetoException | IOException
				| ClassNotFoundException SQLEx) {
			logger.log(Level.WARNING, WorkPacketJDBC.class.getName()
					+ " Could not load incomplete work packets", SQLEx);
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

		return workPackets;

	}

	/**
	 * Returns a specific work packet from the database.
	 * 
	 * @param packetID
	 *            the ID of the work packet.
	 * @return
	 */
	public IWorkPacket getIndividualWorkPacket(String packetID) {
		IWorkPacket workPacket = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ByteArrayInputStream bytesIn = null;
		ObjectInputStream input = null;

		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(GET_INDIVIDUAL_WORK_PACKET);
			preparedStatement.setString(1, packetID);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				byte[] bytes = (byte[]) resultSet.getObject(1);
				bytesIn = new ByteArrayInputStream(bytes);
				input = new ObjectInputStream(bytesIn);
				workPacket = (IWorkPacket) input.readObject();
			}

		} catch (SQLException | PropertyVetoException | ClassNotFoundException
				| IOException SQLEx) {
			logger.log(Level.WARNING, WorkPacketJDBC.class.getName()
					+ " Could not get individual work packet");
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

		return workPacket;
	}

	/**
	 * Reads all the work packets from the database.
	 * 
	 * @return
	 */
	public Collection<IWorkPacket> getWorkPackets() {
		List<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(1000);

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ByteArrayInputStream bytesIn = null;
		ObjectInputStream input = null;

		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement("SELECT work_packet FROM work_packets");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				byte[] bytes = (byte[]) resultSet.getObject(1);
				bytesIn = new ByteArrayInputStream(bytes);
				input = new ObjectInputStream(bytesIn);

				IWorkPacket workPacket = (IWorkPacket) input.readObject();
				workPackets.add(workPacket);
			}

		} catch (SQLException | PropertyVetoException | IOException
				| ClassNotFoundException SQLEx) {
			logger.log(Level.WARNING, WorkPacketJDBC.class.getName()
					+ " Could not load incomplete work packets");
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

		return workPackets;

	}
}
