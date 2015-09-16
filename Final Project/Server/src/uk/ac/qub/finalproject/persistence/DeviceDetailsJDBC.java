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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;

/**
 * A DAO to store, write and update information in the database regarding device
 * details.
 * 
 * @author Phil
 *
 */
public class DeviceDetailsJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(DeviceDetailsJDBC.class);

	private static final String REGISTER_DEVICE = "INSERT INTO devices (device_id) VALUES (?);";

	private static final String DEREGISTER_DEVICE = "DELETE FROM devices WHERE device_id = ?;";
	private static final String VALID_RESULT_SENT = "UPDATE devices SET valid_results = valid_results + 1 WHERE device_id = ?;";
	private static final String INVALID_RESULT_SENT = "UPDATE devices SET invalid_results = invalid_results + 1 WHERE device_id = ?;";
	private static final String LOAD_DEVICES = "SELECT * FROM devices";
	private static final String LOAD_EMAIL_LIST = "SELECT device_id, email_address FROM users";

	private Encryptor encryptor = getEncryptor();

	public boolean deregisterDevice(String deviceID) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(DEREGISTER_DEVICE);
			preparedStatement.setBytes(1, encryptor.encrypt(deviceID));
			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.FINE, DeviceDetailsJDBC.class.getName()
					+ " Problem writing a valid result to the database");
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	public void writeValidResultSent(String deviceID) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(VALID_RESULT_SENT);
			preparedStatement.setBytes(1, encryptor.encrypt(deviceID));
			preparedStatement.executeUpdate();

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.FINE, DeviceDetailsJDBC.class.getName()
					+ " Problem writing a valid result to the database");
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	public void writeInvalidResultSent(String deviceID) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();

			preparedStatement = connection
					.prepareStatement(INVALID_RESULT_SENT);
			preparedStatement.setBytes(1, encryptor.encrypt(deviceID));
			preparedStatement.executeUpdate();
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.FINE, DeviceDetailsJDBC.class.getName()
					+ " Problem writing an invalid result to the database");
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	public boolean registerDevice(RegistrationPack registrationPack) {
		byte[] deviceID = encryptor.encrypt(registrationPack.getAndroidID());

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(REGISTER_DEVICE);
			preparedStatement.setBytes(1, deviceID);
			preparedStatement.executeUpdate();

			return true;

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.FINE, DeviceDetailsJDBC.class.getName()
					+ " Problem registering a device");
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Loads all devices currently stored in the database as device objects,
	 * including their valid/invalid results count and their email address..
	 * 
	 * @return
	 */
	public ConcurrentMap<String, Device> loadDevices() {
		ConcurrentMap<String, Device> devices = new ConcurrentHashMap<String, Device>();
		ResultSet deviceResultSet = null;
		ResultSet emailResultSet = null;

		Connection connection = null;
		Statement statement = null;

		try {
			connection = createConnection();
			statement = connection.createStatement();
			deviceResultSet = statement.executeQuery(LOAD_DEVICES);

			deviceResultSet.beforeFirst();

			while (deviceResultSet.next()) {
				Device device = new Device();
				String deviceID = encryptor.decrypt(deviceResultSet
						.getBytes("device_id"));

				device.setDeviceID(deviceID);
				device.setValidResults(deviceResultSet.getInt("valid_results"));
				device.setInvalidResults(deviceResultSet
						.getInt("invalid_results"));

				// since their last time active isn't stored this
				// makes sure no device will be accidentally declared active
				device.setLastTimeActive(0);
				devices.put(deviceID, device);
			}

			statement.close();

			statement = connection.createStatement();
			emailResultSet = statement.executeQuery(LOAD_EMAIL_LIST);

			while (emailResultSet.next()) {
				String deviceID = encryptor.decrypt(emailResultSet
						.getBytes("device_id"));
				String emailAddress = encryptor.decrypt(emailResultSet
						.getBytes("email_address"));

				Device device = devices.get(deviceID);
				device.setEmailAddress(emailAddress);
				devices.put(deviceID, device);
			}

		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.FINE, DeviceDetailsJDBC.class.getName()
					+ " Problem loading device attributes from the database");
		} finally {
			try {
				if (null != deviceResultSet)
					deviceResultSet.close();
			} catch (SQLException SQLEx) {

			}

			closeConnection(connection, statement, emailResultSet);
		}

		return devices;
	}

}
