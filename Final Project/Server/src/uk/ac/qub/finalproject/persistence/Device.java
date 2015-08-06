/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

/**
 * This POJO represents an Android Device as needed by the system. It stores the
 * unique device ID along with the number of valid results processed, invalid
 * results processed and the last time it was active (in milliseconds snce
 * 1970).
 * 
 * @author Phil
 *
 */
public class Device {

	/**
	 * The unique device ID represented as a Hex string.
	 */
	private String deviceID;
	
	/**
	 * An optional email address.
	 */
	private String emailAddress;

	/**
	 * The number of accurate results processed by this device.
	 */
	private int validResults = 0;

	/**
	 * The number of inaccurate results processed by this device.
	 */
	private int invalidResults = 0;

	/**
	 * The last time this device was in contact with the server in milliseconds.
	 */
	private long lastTimeActive = 0;

	/**
	 * Default constructor.
	 */
	public Device() {

	}

	/**
	 * This constructor takes the unique device ID as the argument.
	 * 
	 * @param deviceID
	 *            the unique device ID
	 */
	public Device(String deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Returns the device's ID
	 * 
	 * @return a Hex string representing the device's ID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * Sets the device ID
	 * 
	 * @param deviceID
	 *            a Hex string representing the device's ID
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Returns the number of valid results this device has sent
	 * 
	 * @return the number of valid results this device has sent.
	 */
	public int getValidResults() {
		return validResults;
	}

	/**
	 * Sets the number of valid results the device has sent.
	 * 
	 * @param validResults
	 *            the number of valid results the device has sent.
	 */
	public void setValidResults(int validResults) {
		this.validResults = validResults;
	}

	/**
	 * Convenience method to increment the device's valid results count by one.
	 */
	public void addValidResult() {
		++validResults;
	}

	/**
	 * Returns the number of invalid results the device has sent.
	 * 
	 * @return
	 */
	public int getInvalidResults() {
		return invalidResults;
	}

	/**
	 * Sets the number of invalid results the device has sent.
	 * 
	 * @param invalidResults
	 *            the number of invalid results this device has sent.
	 */
	public void setInvalidResults(int invalidResults) {
		this.invalidResults = invalidResults;
	}

	/**
	 * Convenience method to increment the number of invalid results sent by
	 * one.
	 */
	public void addInvalidResult() {
		++invalidResults;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	/**
	 * The last time in milliseconds that this device was active.
	 * 
	 * @return the last time that this device was active.
	 */
	public long getLastTimeActive() {
		return lastTimeActive;
	}

	/**
	 * Sets the last time this device was active at.
	 * 
	 * @param lastTimeActive
	 */
	public void setLastTimeActive(long lastTimeActive) {
		this.lastTimeActive = lastTimeActive;
	}

}
