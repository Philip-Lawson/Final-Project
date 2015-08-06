/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.server.RegistrationPack;



/**
 * @author Phil
 *
 */
public class DeviceDetailsJDBC extends AbstractJDBC {

	private static final String REGISTER_DEVICE = "INSERT INTO devices VALUES (?);";
	private static final String REGISTER_EMAIL = "INSERT INTO users VALUES (?, ?);";
	private static final String DEREGISTER_DEVICE = "DELETE FROM devices WHERE device_id = ?;";
	private static final String VALID_RESULT_SENT = "UPDATE devices SET valid_results = valid_results + 1 WHERE device_id = ?;";
	private static final String INVALID_RESULT_SENT = "UPDATE devices SET invalid_results = invalid_results + 1 WHERE device_id = ?;";
	private static final String LOAD_DEVICES = "SELECT * FROM devices";
	private static final String LOAD_EMAIL_LIST = "SELECT device_id, email_address FROM users";

	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();
	private Encryptor encryptor = new EncryptorImpl();
	
	public void setEncryptor(Encryptor encryptor){
		this.encryptor = encryptor;
	}

	public boolean deregisterDevice(String deviceID) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(DEREGISTER_DEVICE);
			preparedStatement.setBytes(1, encryptor.encrypt(deviceID));
			preparedStatement.executeQuery();

			return true;
		} catch (SQLException e) {
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
			preparedStatement.executeQuery();

		} catch (SQLException SQLEx) {

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
			preparedStatement.executeQuery();
		} catch (SQLException SQLEx) {

		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	public boolean registerDevice(RegistrationPack registrationPack) {
		String email = registrationPack.getEmailAddress();
		byte[] deviceID = encryptor.encrypt(registrationPack.getAndroidID());		
		byte[] emailBytes = encryptor.encrypt(email);
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(REGISTER_DEVICE);
			preparedStatement.setBytes(1, deviceID);
			preparedStatement.executeQuery();
			
			preparedStatement.close();

			if (emailValidationStrategy.emailIsValid(email)) {
				preparedStatement = connection.prepareStatement(REGISTER_EMAIL);
				preparedStatement.setBytes(1, deviceID);
				preparedStatement.setBytes(2, emailBytes);
				preparedStatement.executeQuery();
			}

			return true;

		} catch (SQLException SQLEx) {
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	public ConcurrentMap<String, Device> loadDevices() {
		ConcurrentMap<String, Device> devices = new ConcurrentHashMap<String, Device>();
		ResultSet deviceResultSet = null;
		ResultSet emailResultSet = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(LOAD_DEVICES);
			deviceResultSet = preparedStatement.executeQuery();

			while (deviceResultSet.next()) {
				Device device = new Device();
				String deviceID = encryptor.decrypt(deviceResultSet
						.getBytes("device_id"));

				device.setDeviceID(deviceID);
				device.setValidResults(deviceResultSet.getInt("valid_results"));
				device.setInvalidResults(deviceResultSet.getInt("invalid_results"));

				devices.put(deviceID, device);
			}
			preparedStatement.close();

			preparedStatement = connection.prepareStatement(LOAD_EMAIL_LIST);
			emailResultSet = preparedStatement.executeQuery();
			
			while(emailResultSet.next()){
				String deviceID = encryptor.decrypt(emailResultSet.getBytes("device_id"));
				String emailAddress= encryptor.decrypt(emailResultSet.getBytes("email_address"));
				
				Device device = devices.get(deviceID);
				device.setEmailAddress(emailAddress);
				devices.put(deviceID, device);
			}

		} catch (SQLException e) {

		} finally {
			try {
				if (null != deviceResultSet)
					deviceResultSet.close();
			} catch (SQLException SQLEx) {

			}
						
			closeConnection(connection, preparedStatement, emailResultSet);
		}

		return devices;
	}

}
