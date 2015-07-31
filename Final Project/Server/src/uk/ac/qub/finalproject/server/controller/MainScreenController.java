package uk.ac.qub.finalproject.server.controller;

public interface MainScreenController {

	public void addBlacklistChangeListener();
	public void addDeviceTimeoutChangeListener();
	public void addDuplicatePacketsChangeListener();
	public void addPacketsPerListChangeListener();
}
