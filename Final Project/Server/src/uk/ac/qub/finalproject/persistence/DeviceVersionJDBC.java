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
 * @author Phil
 *
 */
public class DeviceVersionJDBC extends AbstractJDBC {

	private Logger logger = LoggingUtils.getLogger(DeviceVersionJDBC.class);

	private String SAVE_DEVICE_CODE = "UPDATE devices SET version_code = ? WHERE device_id = ?";
	private String LOAD_DEVICE_VERSION_INFO = "SELECT version_code, device_id FROM devices ORDER BY version_code DESC";

	private Encryptor encryptor = getEncryptor();

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

	public void updateDeviceVersion(Integer version, String deviceID) {
		saveDeviceVersion(version, deviceID);
	}

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

	private void addResultToMap(Map<Integer, List<String>> versionMap,
			Integer versionCode, String deviceID) {
		if (!versionMap.containsKey(versionCode)) {
			versionMap.put(versionCode, new Vector<String>());
		}

		versionMap.get(versionCode).add(deviceID);
	}
}
