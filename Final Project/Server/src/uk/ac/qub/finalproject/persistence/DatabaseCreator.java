/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates the non-domain specific databases used to store device details, user
 * details, loaded work packets and results packets. As this may be called
 * multiple times each create table statement includes the command
 * "CREATE TABLE IF NOW EXISTS".
 * 
 * @author Phil
 *
 */
public class DatabaseCreator extends AbstractJDBC {
	
	private Logger logger = Logger.getLogger(DatabaseCreator.class.getName());

	private static final String CREATE_DEVICES_TABLE = "CREATE TABLE IF NOT EXISTS devices "
			+ "( device_id VARBINARY (255) NOT NULL,"
			+ " valid_results INT NOT NULL DEFAULT 0,"
			+ " invalid_results INT NOT NULL DEFAULT 0,"
			+ " version_code INT NOT NULL DEFAULT -1"
			+ " PRIMARY KEY (device_id),"
			+ " INDEX (device_id) );";

	private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users "
			+ "( device_id VARBINARY (255) NOT NULL, "
			+ "email_address VARBINARY (255) NOT NULL, "
			+ "PRIMARY KEY (device_id), "
			+ "FOREIGN KEY (device_id) REFERENCES devices (device_id) "
			+ "ON DELETE CASCADE );";

	private static final String CREATE_WORK_PACKET_TABLE = "CREATE TABLE IF NOT EXISTS work_packets "
			+ "( packet_id VARCHAR(20) NOT NULL, "
			+ "work_packet BLOB NOT NULL, "
			+ "PRIMARY KEY (packet_id),"
			+ "INDEX (packet_id) );";

	private static final String CREATE_RESULTS_PACKET_TABLE = "CREATE TABLE IF NOT EXISTS results_packets "
			+ "( packet_id VARCHAR(20) NOT NULL, "
			+ "results_packet BLOB NOT NULL, "
			+ "PRIMARY KEY (packet_id), "
			+ "FOREIGN KEY (packet_id) REFERENCES work_packets (packet_id) "
			+ "ON DELETE CASCADE );";

	

	private String[] TABLE_CREATION_SCRIPTS = { CREATE_DEVICES_TABLE,
			CREATE_USERS_TABLE, CREATE_RESULTS_PACKET_TABLE,
			CREATE_WORK_PACKET_TABLE};

	public void setupDatabase() {

		Connection connection = null;
		Statement statement = null;

		try {
			connection = createConnection();
			statement = connection.createStatement();

			for (String script : TABLE_CREATION_SCRIPTS) {
				statement.addBatch(script);
			}

			statement.executeBatch();
		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.SEVERE, "Problem setting up the database", e);
		} finally {
			closeConnection(connection, statement, null);
		}

	}

}
