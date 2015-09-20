/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;

/**
 * The DAO used to perform CRUD operations associated with registered users.
 * 
 * @author Phil
 *
 */
public class UserDetailsJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(UserDetailsJDBC.class);

	private static final String REGISTER_EMAIL = "INSERT INTO users VALUES (?, ?);";
	private static final String GET_USER_DEVICES = "SELECT device_id FROM users WHERE email_address = ?";
	private static final String GET_ALL_EMAILS = "SELECT email_address FROM users";
	private static final String GET_VALID_RESULTS_COMPLETED = "SELECT SUM(valid_results) FROM devices WHERE device_id IN ("
			+ GET_USER_DEVICES + ")";
	private static final String CHANGE_EMAIL_ADDRESS = "UPDATE users SET email_address = ? WHERE email_address IN ("
			+ "SELECT email_address FROM "
			+ "(SELECT email_address FROM users WHERE device_id = ?) AS temp_users) ;";
	private static final String DELETE_EMAIL_ADDRESS = "DELETE email_address FROM users WHERE device_id = ?";

	private Encryptor encryptor = getEncryptor();
	private EmailValidationStrategy emailValidationStrategy = new EmailValidationStrategy();

	/**
	 * Saves a user's email to the database. This method will only work if the
	 * user has not already registered.
	 * 
	 * @param registrationPack
	 * @return
	 */
	public boolean registerEmail(RegistrationPack registrationPack) {

		String email = registrationPack.getEmailAddress();
		byte[] deviceID = encryptor.encrypt(registrationPack.getAndroidID());
		byte[] emailBytes = encryptor.encrypt(email);

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {

			if (emailValidationStrategy.emailIsValid(email)) {
				connection = createConnection();
				preparedStatement = connection.prepareStatement(REGISTER_EMAIL);
				preparedStatement.setBytes(1, deviceID);
				preparedStatement.setBytes(2, emailBytes);
				preparedStatement.executeUpdate();
				return true;
			}

			return false;
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not register email");
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Retrieves a list of all devices associated with a registered user.
	 * 
	 * @param emailAddress
	 *            the user's email address.
	 * @return
	 */
	public Collection<String> getUserDevices(String emailAddress) {
		Collection<String> devicesList = new ArrayList<String>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = createConnection();

			preparedStatement = connection.prepareStatement(GET_USER_DEVICES);
			preparedStatement.setBytes(1, encryptor.encrypt(emailAddress));
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				devicesList.add(encryptor.decrypt(resultSet.getBytes(1)));
			}
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not get user devices");
		} finally {
			closeConnection(connection, preparedStatement, resultSet);
		}

		return devicesList;
	}

	/**
	 * Changes a user's email address.
	 * 
	 * @param registrationPack
	 * @return
	 */
	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(CHANGE_EMAIL_ADDRESS);
			preparedStatement.setBytes(1,
					encryptor.encrypt(registrationPack.getEmailAddress()));
			preparedStatement.setBytes(2,
					encryptor.encrypt(registrationPack.getAndroidID()));

			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not change email address");
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Deletes a user's email address from the database.
	 * 
	 * @param registrationPack
	 * @return
	 */
	public boolean deleteEmailAddress(RegistrationPack registrationPack) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			byte[] deviceID = encryptor
					.encrypt(registrationPack.getAndroidID());

			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(DELETE_EMAIL_ADDRESS);
			preparedStatement.setBytes(1, deviceID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not delete email address");
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}

	/**
	 * Returns the number of calid results associated with a registered user.
	 * 
	 * @param emailAddress
	 *            the user's email address.
	 * @return
	 */
	public int getUserValidResults(String emailAddress) {
		int validResults = 0;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(GET_VALID_RESULTS_COMPLETED);
			preparedStatement.setBytes(1, encryptor.encrypt(emailAddress));
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				validResults = resultSet.getInt(1);
			}

		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not get valid results");
		} finally {
			closeConnection(connection, preparedStatement, resultSet);
		}

		return validResults;
	}

	/**
	 * Retrieves all user emails from the database.
	 * 
	 * @return
	 */
	public Collection<String> getAllUserEmails() {
		Collection<String> emailList = new ArrayList<String>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = createConnection();
			preparedStatement = connection.prepareStatement(GET_ALL_EMAILS);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				emailList.add(encryptor.decrypt(resultSet.getBytes(1)));
			}
		} catch (SQLException | PropertyVetoException SQLEx) {
			logger.log(Level.WARNING, UserDetailsJDBC.class.getName()
					+ " Could not get all emails");
		} finally {
			closeConnection(connection, preparedStatement, resultSet);
		}

		return emailList;
	}

}
