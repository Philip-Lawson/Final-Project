/**
 * 
 */
package finalproject.poc.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import finalproject.poc.appserver.RegistrationPack;

/**
 * @author Phil
 *
 */
public class UserDetailsJDBC extends AbstractJDBC {

	private static final String GET_USER_DEVICES = "SELECT device_id FROM user WHERE email_address = ?";
	private static final String CHANGE_EMAIL_ADDRESS = "UPDATE users SET email_address = ? WHERE email_address = "
			+ "(SELECT email_address FROM users WHERE device_id = ?) ;";

	public Collection<String> getUserDevices(String emailAddress) {
		Collection<String> devicesList = new ArrayList<String>();
		ResultSet resultSet = null;

		try {
			createConnection();

			preparedStatement = connection.prepareStatement(GET_USER_DEVICES);
			preparedStatement.setString(1, emailAddress);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				devicesList.add(resultSet.getString(1));
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

		return devicesList;
	}

	public boolean changeEmailAddress(RegistrationPack registrationPack) {
		try {
			createConnection();
			preparedStatement = connection.prepareStatement(CHANGE_EMAIL_ADDRESS);
			preparedStatement.setString(1, registrationPack.getEmailAddress());
			preparedStatement.setString(2, registrationPack.getAndroidID());
			
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException SQLEx) {
			return false;
		} finally {
			closeConnection();
		}
	}

}
