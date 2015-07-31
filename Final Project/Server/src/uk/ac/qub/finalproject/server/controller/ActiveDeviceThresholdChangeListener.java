/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Phil
 *
 */
public class ActiveDeviceThresholdChangeListener implements
		ChangeListener<String> {
	private static String FIND_NON_NUMBERS_REGEX = "[^1-9]";
	private static String HOUR = "hour";

	
	private DeviceDetailsManager deviceDetailsManager;
	
	public ActiveDeviceThresholdChangeListener() {
		
	}
	
	public ActiveDeviceThresholdChangeListener(DeviceDetailsManager deviceDetailsManager){
		this.deviceDetailsManager = deviceDetailsManager;
	}

	/* (non-Javadoc)
	 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends String> arg0, String oldValue,
			String newValue) {
		// get number value from string
		int timeValue = Integer.parseInt(newValue.replaceAll(FIND_NON_NUMBERS_REGEX, ""));
		
		if (newValue.contains(HOUR)){
			deviceDetailsManager.setActiveDeviceThreshold(timeValue*60);
		} else {
			deviceDetailsManager.setActiveDeviceThreshold(timeValue);
		}
	
	}
	
}
