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

import uk.ac.qub.finalproject.server.RegistrationPack;



/**
 * @author Phil
 *
 */
public class UserDetailsJDBC extends AbstractJDBC {

	private static final String GET_USER_DEVICES = "SELECT device_id FROM user WHERE email_address = ?";
	private static final String GET_VALID_RESULTS_COMPLETED = "SELECT SUM (valid_results) FROM devices WHERE device_id IN ("
			+ GET_USER_DEVICES + ")";
	private static final String CHANGE_EMAIL_ADDRESS = "UPDATE users SET email_address = ? WHERE email_address = "
			+ "(SELECT email_address FROM users WHERE device_id = ?) ;";
	
	private Encryptor encryptor = new EncryptorImpl();
	
	public void setEncryptor(Encryptor encryptor){
		this.encryptor = encryptor;
	}

	public Collection<String> getUserEmails(String emailAddress) {
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
		} catch (SQLException SQLEx) {

		} finally {
			closeConnection(connection, preparedStatement, resultSet);
		}

		return devicesList;
	}

	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = createConnection();
			preparedStatement = connection
					.prepareStatement(CHANGE_EMAIL_ADDRESS);
			preparedStatement.setBytes(1, encryptor.encrypt(registrationPack.getEmailAddress()));
			preparedStatement.setBytes(2, encryptor.encrypt(registrationPack.getAndroidID()));

			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException SQLEx) {
			return false;
		} finally {
			closeConnection(connection, preparedStatement, null);
		}
	}
	
	public int getUserValidResults(String emailAddress){		
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
			
			if (resultSet.next()){
				validResults = resultSet.getInt(1);
			}			
			
		} catch (SQLException SQLEx) {
			
		} finally {			
			closeConnection(connection, preparedStatement, resultSet);
		}
		
		return validResults;
	}

}
