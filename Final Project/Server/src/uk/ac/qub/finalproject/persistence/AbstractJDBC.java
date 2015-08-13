/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is the base class for all DAO's in the server's persistence layer. It
 * contains the connection details along with convenience methods for creating
 * and closing connections.
 * 
 * @author Phil
 *
 */
public abstract class AbstractJDBC {

	private static final String DATABASE_URL = "";
	private static final String USERNAME = "";
	private static final String PASSWORD = "";

	/**
	 * A convenience method for creating the connection to the database.
	 * 
	 * @throws SQLException
	 *             if the connection fails.
	 * @throws ClassNotFoundException
	 */
	protected Connection createConnection() throws SQLException,
			ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
	}

	/**
	 * A convenience method for closing the connection and the prepared
	 * statement. This takes care of nulls and SQL Exceptions.
	 */
	protected void closeConnection(Connection connection, Statement statement,
			ResultSet resultSet) {
		try {
			if (null != resultSet)
				resultSet.close();
		} catch (SQLException SQLEx) {

		}

		try {
			if (null != statement)
				statement.close();
		} catch (SQLException SQLEx) {

		}

		try {
			if (null != connection)
				connection.close();
		} catch (SQLException SQLEx) {

		}
	}

}
