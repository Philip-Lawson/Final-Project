/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.*;

/**
 * @author Phil
 *
 */
public class ConnectionPool {	
	
	private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://web2.eeecs.qub.ac.uk/40143289";
	private static final String USER = "40143289";
	private static final String PASSWORD = "FMA4237";
	
	private static ConnectionPool uniqueInstance;
	
	private ComboPooledDataSource connectionPool;
	
	private ConnectionPool() throws PropertyVetoException{
		setupConnection();
	}
	
	public static ConnectionPool getConnectionPool() throws PropertyVetoException{
		if (null == uniqueInstance) {
			synchronized (ConnectionPool.class){
				if (null == uniqueInstance){
					uniqueInstance = new ConnectionPool();
				}
			}
		}
		
		return uniqueInstance;
	}
	
	private void setupConnection() throws PropertyVetoException{
		connectionPool = new ComboPooledDataSource();
		connectionPool.setDriverClass(DRIVER_CLASS);
		connectionPool.setJdbcUrl(DATABASE_URL);
		connectionPool.setUser(USER);
		connectionPool.setPassword(PASSWORD);
	}
	
	public Connection getConnection() throws SQLException{
		return connectionPool.getConnection();
	}
	
	public void closeConnectionPool(){
		connectionPool.close();
	}
	
	

}
