/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import uk.ac.qub.finalproject.server.implementations.Implementations;

import com.mchange.v2.c3p0.*;

/**
 * A connection pool object for accessing the database. This is implemented
 * using the C3P0 library and a double checked locking singleton.
 * 
 * @author Phil
 *
 */
public class ConnectionPool {

	private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = Implementations.getDatabaseURL();
	private static final String USER = Implementations.getDatabaseUser();
	private static final String PASSWORD = Implementations.getDatabasePassword();

	private static volatile ConnectionPool uniqueInstance;

	private ComboPooledDataSource connectionPool;

	private ConnectionPool() throws PropertyVetoException {
		setupConnection();
	}

	/**
	 * Returns an instance of the connection pool.
	 * 
	 * @return
	 * @throws PropertyVetoException
	 */
	public static ConnectionPool getConnectionPool()
			throws PropertyVetoException {
		if (null == uniqueInstance) {
			synchronized (ConnectionPool.class) {
				if (null == uniqueInstance) {
					uniqueInstance = new ConnectionPool();
				}
			}
		}

		return uniqueInstance;
	}

	/**
	 * Configures the connection pool.
	 * 
	 * @throws PropertyVetoException
	 */
	private void setupConnection() throws PropertyVetoException {
		connectionPool = new ComboPooledDataSource();
		connectionPool.setDriverClass(DRIVER_CLASS);
		connectionPool.setJdbcUrl(DATABASE_URL);
		connectionPool.setUser(USER);
		connectionPool.setPassword(PASSWORD);
	}

	/**
	 * Returns a connection to the database from the connection pool.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}

	/**
	 * Closes the connection pool. This should only be called on closing the
	 * application.
	 */
	public void closeConnectionPool() {
		connectionPool.close();
	}

}
