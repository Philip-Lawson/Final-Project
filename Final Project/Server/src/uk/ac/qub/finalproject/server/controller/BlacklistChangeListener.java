/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;

/**
 * @author Phil
 *
 */
public class BlacklistChangeListener implements ChangeListener<Toggle> {

	private static String FIND_NON_NUMBERS_REGEX = "[^0-9]";
	private DeviceDetailsManager deviceDetailsManager;

	public BlacklistChangeListener() {

	}

	public BlacklistChangeListener(DeviceDetailsManager deviceDetailsManager) {
		this.deviceDetailsManager = deviceDetailsManager;
	}

	@Override
	public void changed(ObservableValue<? extends Toggle> observable,
			Toggle oldValue, Toggle newValue) {
		String toggledText = newValue.getUserData().toString();
		int blacklistPercent = Integer.parseInt(toggledText.replaceAll(
				FIND_NON_NUMBERS_REGEX, ""));

		deviceDetailsManager.setMinPercentInvalidResults(blacklistPercent);
	}
}
