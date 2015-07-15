/**
 * 
 */
package finalproject.poc.persistence;

/**
 * @author Phil
 *
 */
public class Device {

	private String deviceID;
	private int validResults = 0;
	private int invalidResults = 0;
	private long lastTimeActive = 0;

	public Device(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public int getValidResults() {
		return validResults;
	}

	public void setValidResults(int validResults) {
		this.validResults = validResults;
	}
	
	public void addValidResult(){
		++validResults;
	}

	public int getInvalidResults() {
		return invalidResults;
	}

	public void setInvalidResults(int invalidResults) {
		this.invalidResults = invalidResults;
	}
	
	public void addInvalidResult(){
		++invalidResults;
	}

	public long getLastTimeActive() {
		return lastTimeActive;
	}

	public void setLastTimeActive(long lastTimeActive) {
		this.lastTimeActive = lastTimeActive;
	}

}
