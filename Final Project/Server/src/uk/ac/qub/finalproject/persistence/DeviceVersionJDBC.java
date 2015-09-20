/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DAO for performing CRUD operations regarding device version information.
 * 
 * @author Phil
 *
 */
public class DeviceVersionJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(DeviceVersionJDBC.class);

	private String SAVE_DEVICE_CODE = "UPDATE devices SET version_code = ? WHERE device_id = ?";
	private String LOAD_DEVICE_VERSION_INFO = "SELECT version_code, device_id FROM devices ORDER BY version_code DESC";

	private Encryptor encryptor = getEncryptor();

	/**
	 * Saves the device version information to the database.
	 * 
	 * @param version
	 * @param deviceID
	 */
	public void saveDeviceVersion(Integer version, String deviceID) {
		try {
			Connection connection = createConnection();
			PreparedStatement preparedStatement = connection
					.prepareStatement(SAVE_DEVICE_CODE);
			preparedStatement.setInt(1, version);
			preparedStatement.setBytes(2, encryptor.encrypt(deviceID));
			preparedStatement.executeUpdate();

			closeConnection(connection, preparedStatement, null);
		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.WARNING, DeviceVersionJDBC.class.getName()
					+ " Problem saving device attributes to the database");
		}

	}

	/**
	 * Updates a device's version information in the database.
	 * 
	 * @param version
	 * @param deviceID
	 */
	public void updateDeviceVersion(Integer version, String deviceID) {
		saveDeviceVersion(version, deviceID);
	}

	/**
	 * Retrieves all device version information from the database.
	 * 
	 * @return
	 */
	public Map<Integer, List<String>> getDeviceVersions() {
		Map<Integer, List<String>> versionMap = new HashMap<Integer, List<String>>();

		try {
			Connection connection = createConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement
					.executeQuery(LOAD_DEVICE_VERSION_INFO);

			while (resultSet.next()) {
				Integer versionCode = resultSet.getInt("version_code");
				String deviceID = encryptor.decrypt(resultSet
						.getBytes("device_id"));
				addResultToMap(versionMap, versionCode, deviceID);
			}

			closeConnection(connection, statement, resultSet);
		} catch (SQLException | PropertyVetoException e) {
			logger.log(Level.WARNING, DeviceVersionJDBC.class.getName()
					+ " Problem loading device attributes from the database");
		}

		return versionMap;

	}

	/**
	 * Adds the device version information to the referenced map.
	 * 
	 * @param versionMap
	 * @param versionCode
	 * @param deviceID
	 */
	private void addResultToMap(Map<Integer, List<String>> versionMap,
			Integer versionCode, String deviceID) {
		if (!versionMap.containsKey(versionCode)) {
			versionMap.put(versionCode, new Vector<String>());
		}

		versionMap.get(versionCode).add(deviceID);
	}
}
