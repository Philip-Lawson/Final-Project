/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import uk.ac.qub.finalproject.persistence.DeviceVersionJDBC;

/**
 * @author Phil
 *
 */
public class DeviceVersionJDBCStub extends DeviceVersionJDBC {
	 

	private Map<Integer, List<String>> versionMap = new HashMap<Integer, List<String>>();
	private boolean versionSaved = false;
	private boolean versionUpdated = false;
	private boolean getVersionMapCalled = false;
	
	@Override
	public void saveDeviceVersion(Integer versionCde, String deviceID){
		versionSaved = true;
	}
	
	@Override
	public void updateDeviceVersion(Integer versionCde, String deviceID){
		versionUpdated = true;
	}
	
	@Override
	public Map<Integer, List<String>> getDeviceVersions(){
		versionMap.put(1, new Vector<String>());
		getVersionMapCalled = true;
		return versionMap;
	}
	
	public int mapSize(){
		return versionMap.size();
	}
	
	public void reset(){
		versionSaved = false;
		versionUpdated = false;
		getVersionMapCalled = false;
	}
	
	public boolean resultSaved(){
		return versionSaved;
	}
	
	public boolean versionUpdated(){
		return versionUpdated;
	}
	
	public boolean mapLoaded(){
		return getVersionMapCalled;
	}
}
