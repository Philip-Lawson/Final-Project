/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.server.views.MainScreenView;

/**
 * @author Phil
 *
 */
public class MainScreenControllerImp implements MainScreenController {

	private MainScreenView mainScreen;
	private DeviceDetailsManager deviceDetailsManager;
	private AbstractWorkPacketDrawer workPacketsManager;

	@Override
	public void addBlacklistChangeListener() {
		mainScreen.setBlacklistChangeListener(new BlacklistChangeListener(
				deviceDetailsManager));
	}

	@Override
	public void addDeviceTimeoutChangeListener() {
		mainScreen
				.setActiveDeviceThresholdChangeListener(new ActiveDeviceThresholdChangeListener(
						deviceDetailsManager));

	}

	@Override
	public void addDuplicatePacketsChangeListener() {
		mainScreen
				.setDuplicatesNumChangeListener(new DuplicatesNumChangeListener(
						workPacketsManager));

	}

	@Override
	public void addPacketsPerListChangeListener() {
		mainScreen.setPacketsNumChangeListener(new PacketsNumChangeListener(
				workPacketsManager));

	}

}
