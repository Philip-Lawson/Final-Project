/**
 * 
 */
package finalproject.poc.persistence;


import java.sql.SQLException;

import finalproject.poc.appserver.RegistrationPack;

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
	
	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();


	public boolean deregisterDevice(String deviceID) {
		try {
			createConnection();

			preparedStatement = connection.prepareStatement(DEREGISTER_DEVICE);
			preparedStatement.setString(1, deviceID);
			preparedStatement.executeQuery();

			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			closeConnection();
		}
	}

	public void writeValidResultSent(String deviceID) {
		try {
			createConnection();

			preparedStatement = connection.prepareStatement(VALID_RESULT_SENT);
			preparedStatement.setString(1, deviceID);
			preparedStatement.executeQuery();

		} catch (SQLException SQLEx) {

		} finally {
			closeConnection();
		}
	}

	public void writeInvalidResultSent(String deviceID) {
		try {
			createConnection();

			preparedStatement = connection
					.prepareStatement(INVALID_RESULT_SENT);
			preparedStatement.setString(1, deviceID);
			preparedStatement.executeQuery();
		} catch (SQLException SQLEx) {

		} finally {
			closeConnection();
		}
	}

	public void registerDevice(RegistrationPack registrationPack) {
		String deviceID = registrationPack.getAndroidID();
		String email = registrationPack.getEmailAddress();

		try {
			createConnection();

			preparedStatement = connection.prepareStatement(REGISTER_DEVICE);
			preparedStatement.setString(1, deviceID);
			preparedStatement.executeQuery();

			if (emailValidationStrategy.emailIsValid(email)) {
				preparedStatement = connection.prepareStatement(REGISTER_EMAIL);
				preparedStatement.setString(1, deviceID);
				preparedStatement.setString(2, email);
				preparedStatement.executeQuery();
			}

		} catch (SQLException SQLEx) {

		} finally {
			closeConnection();
		}
	}	
	

}
