/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import uk.ac.qub.finalproject.server.controller.BlacklistChangeListener;
import uk.ac.qub.finalproject.server.controller.Command;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * @author Phil
 *
 */
public class MainScreenView extends Application {

	@FXML
	private ToggleGroup blacklistPercent;

	@FXML
	private ComboBox<Integer> packetsNumComboBox;

	@FXML
	private ComboBox<Integer> duplicatesNumComboBox;

	@FXML
	private ComboBox<String> activeDeviceThresholdComboBox;

	@Override
	public void start(Stage arg0) throws Exception {

	}

	public void setPacketsNumComboBox() {
		packetsNumComboBox.getItems()
				.addAll(2, 3, 5, 8, 10, 15, 20, 30, 40, 50);
		// packetsNumComboBox.setEditable(true);
		// spacketsNumComboBox.setConverter(new IntegerStringConverter());
	}

	public void setDuplicatesNumComboBox() {
		duplicatesNumComboBox.getItems().addAll(2, 3, 5, 8, 10, 15, 20);
	}

	public void setActiveDeviceThresholdComboBox() {
		activeDeviceThresholdComboBox.getItems().addAll("5 minutes",
				"10 minutes", "15 minutes", "30 minutes", "1 hour", "2 hours",
				"3 hours", "5 hours", "8 hours");
	}

	public void setBlacklistChangeListener(
			BlacklistChangeListener changeListener) {
		blacklistPercent.selectedToggleProperty().addListener(changeListener);
	}

	public void setPacketsNumChangeListener(
			ChangeListener<Integer> changeListener) {
		packetsNumComboBox.valueProperty().addListener(changeListener);
	}

	public void setDuplicatesNumChangeListener(
			ChangeListener<Integer> changeListener) {
		duplicatesNumComboBox.valueProperty().addListener(changeListener);
	}

	public void setActiveDeviceThresholdChangeListener(
			ChangeListener<String> changeListener) {
		activeDeviceThresholdComboBox.valueProperty().addListener(
				changeListener);
	}
	
	public void setLoadAdditionalWorkPacketsCommand(Command command){
		
	}
	
	public void setStartServerCommand(Command command){
		
	}

	public void setStopSendingPacketsCommand(Command command){
		
	}
	
	public void setTransferResultsCommand(Command command){
		
	}
	
	public void updateProgress(int packetsComplete, int packetsLeft) {

	}

	public void updateAverageProcessingTime(String averageTime) {

	}

	public void updateGoodPacketsProportion(int goodPackets, int badPackets) {

	}

	public void updateActiveDevices(int activeDevices) {

	}
	
	public void updateTotalNumDevices(int devices) {
		
	}
	
	public void updateBlacklistedDevices(int blacklistedDevices) {
		
	}

}
