/**
 * 
 */
package finalproject.poc.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Phil
 *
 */
public abstract class AbstractJDBC {
	
	private static final String DATABASE_URL = "";
	private static final String USER = "";
	private static final String PASSWORD = "";
	
	protected Connection connection;
	protected PreparedStatement preparedStatement;
	
	protected void createConnection() throws SQLException {
		connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);		
	}

	protected void closeConnection() {
		try {
			if (preparedStatement != null)
				preparedStatement.close();
		} catch (SQLException SQLEx) {

		}

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException SQLEx) {

		}
	}

}
