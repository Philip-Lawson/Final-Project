/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.WorkPacketDrawerImpl;
import uk.ac.qub.finalproject.server.controller.BlacklistChangeListener;
import uk.ac.qub.finalproject.server.controller.Command;
import uk.ac.qub.finalproject.server.controller.MainViewEventHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * @author Phil
 *
 */
public class MainScreenView {

	@FXML
	private ToggleGroup blacklistPercent;
	
	@FXML
	private Toggle fivePercentBlacklistRadioButton;
	
	@FXML
	private Toggle tenPercentBlacklistRadioButton;
	
	@FXML
	private Toggle twentyPercentBlacklistRadioButton;
	
	@FXML
	private Toggle thirtyPercentBlacklistRadioButton;

	@FXML
	private ComboBox<Integer> packetsNumComboBox;

	@FXML
	private ComboBox<Integer> duplicatesNumComboBox;

	@FXML
	private ComboBox<String> activeDeviceThresholdComboBox;

	@FXML
	private ProgressBar processingProgressBar;

	@FXML
	private LineChart<Number, Number> processingTimeLineChart;

	private XYChart.Series<Number, Number> processingTimeSeries = new XYChart.Series<Number, Number>();

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;

	@FXML
	private Button transferResultsButton;

	@FXML
	private Button loadAdditionalPacketsButton;

	@FXML
	private Label activeDevicesLabel;

	@FXML
	private Label inactiveDevicesLabel;

	@FXML
	private Label blacklistedDevicesLabel;

	@FXML
	private Label averageTimeLabel;

	@FXML
	private Label packetProgressLabel;

	@FXML
	private Label percentPacketsCompleteLabel;

	@FXML
	private Label percentBadPacketsLabel;
	
	private NumberFormatter numberFormatter = new NumberFormatter();

	private Command startCommand;
	private Command loadAdditionalWorkPacketsCommand;
	private Command stopSendingPacketsCommand;
	private Command transferResultsCommand;

	private MainViewEventHandler loadWorkPacketHandler;
	private MainViewEventHandler transferResultsHandler;

	private ConcurrentMap<Number, Integer> processingTimesMap = new ConcurrentHashMap<Number, Integer>();
	private int totalDevices;
	private int number = 10;

	public void setupWidgets() {
		setupButtons();
		setPacketsNumComboBox();
		setDuplicatesNumComboBox();
		setActiveDeviceThresholdComboBox();
		setupChart();
	}

	public void setupButtons() {
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startCommand.execute();
				number++;
				int num = (number / 10) * 10;
				addProcessingTime(num);
				updateActiveDevices(num);
				updateAverageProcessingTime(num + "");
				updateBlacklistedDevices(num);
				updatePacketStats(num, num);
				updateProgress(num, num);
				updateTotalNumDevices(num * 2);
				startButton.setDisable(true);
				stopButton.setDisable(false);
			}
		});

		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				stopButton.setDisable(true);
				startButton.setDisable(false);
				stopSendingPacketsCommand.execute();
			}

		});

		loadWorkPacketHandler = new MainViewEventHandler(
				loadAdditionalWorkPacketsCommand);
		loadAdditionalPacketsButton.setOnAction(loadWorkPacketHandler);

		transferResultsHandler = new MainViewEventHandler(
				transferResultsCommand);
		transferResultsButton.setOnAction(transferResultsHandler);
		
		fivePercentBlacklistRadioButton.setUserData("5");
		tenPercentBlacklistRadioButton.setUserData("10");
		twentyPercentBlacklistRadioButton.setUserData("20");
		thirtyPercentBlacklistRadioButton.setUserData("30");
		
		
	}

	public void setPacketsNumComboBox() {
		packetsNumComboBox.getItems()
				.addAll(2, 3, 5, 8, 10, 15, 20, 30, 40, 50);
		packetsNumComboBox
				.setValue(WorkPacketDrawerImpl.DEFAULT_PACKETS_PER_LIST);
		packetsNumComboBox.setVisibleRowCount(4);
		
	}

	public void setDuplicatesNumComboBox() {
		duplicatesNumComboBox.getItems().addAll(5, 8, 10, 15, 20);
		duplicatesNumComboBox
				.setValue(WorkPacketDrawerImpl.DEFAULT_TIMES_TO_SEND_PACKET_LIST);
		duplicatesNumComboBox.setVisibleRowCount(4);
	}

	public void setActiveDeviceThresholdComboBox() {
		activeDeviceThresholdComboBox.getItems().addAll("5 minutes",
				"10 minutes", "15 minutes", "30 minutes", "1 hour", "2 hours",
				"3 hours", "5 hours", "8 hours");
		activeDeviceThresholdComboBox
				.setValue(DeviceDetailsManager.DEFAULT_ACTIVE_DEVICE_THRESHOLD
						+ " minutes");
		activeDeviceThresholdComboBox.setVisibleRowCount(4);
	}

	public void setupChart() {
		processingTimeLineChart.getData().add(processingTimeSeries);	
		NumberAxis xAxis = (NumberAxis) processingTimeLineChart.getXAxis();
		NumberAxis yAxis = (NumberAxis) processingTimeLineChart.getYAxis();
		xAxis.setTickLabelFormatter(numberFormatter);
		yAxis.setTickLabelFormatter(numberFormatter);		
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

	public void setLoadAdditionalWorkPacketsCommand(Command command) {
		this.loadAdditionalWorkPacketsCommand = command;
	}

	public void setStartServerCommand(Command command) {
		this.startCommand = command;
	}

	public void setStopSendingPacketsCommand(Command command) {
		this.stopSendingPacketsCommand = command;
	}

	public void setTransferResultsCommand(Command command) {
		this.transferResultsCommand = command;
	}

	public void updateProgress(long packetsComplete, long totalPackets) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double progress = packetsComplete / totalPackets;
				int percentComplete = (int) progress * 100;

				processingProgressBar.setProgress(progress);
				packetProgressLabel.setText(packetsComplete + "/"
						+ totalPackets);
				percentPacketsCompleteLabel.setText(percentComplete + " %");
			}

		});
	}

	public void addProcessingTime(int minutes) {
		Integer devices = processingTimesMap.get(minutes);

		if (null == devices) {
			processingTimesMap.put(minutes, 1);
		} else {
			processingTimesMap.put(minutes, ++devices);
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				/*
				 * Known bug. Occasionally an index out of bounds exception will
				 * be thrown and printed to console.
				 * https://bugs.openjdk.java.net/browse/JDK-8120863
				 */				
				processingTimeSeries.getData().clear();
				

				for (Number minute : processingTimesMap.keySet()) {
					Number devices = processingTimesMap.get(minute);
					processingTimeSeries.getData().add(
							new XYChart.Data<Number, Number>(minute, devices));
				}		

			}

		});

	}

	public void updateAverageProcessingTime(String averageTime) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				averageTimeLabel.setText(averageTime);
			}

		});
	}

	public void updatePacketStats(long validPackets, long invalidPackets) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					double percentBadPackets = invalidPackets
							/ (validPackets + invalidPackets);
					int percent = (int) (percentBadPackets * 100);
					percentBadPacketsLabel.setText(percent + " %");
				} catch (ArithmeticException Ex){
					
				}				
			}

		});
	}

	public void updateActiveDevices(int activeDevices) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				activeDevicesLabel.setText(activeDevices + "");
				inactiveDevicesLabel.setText((totalDevices - activeDevices)
						+ "");
			}

		});
	}

	public void updateTotalNumDevices(int devices) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				totalDevices = devices;
				int activeDevices = Integer.parseInt(activeDevicesLabel
						.getText());
				inactiveDevicesLabel.setText((devices - activeDevices) + "");
			}

		});
	}

	public void updateBlacklistedDevices(int blacklistedDevices) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				blacklistedDevicesLabel.setText(blacklistedDevices + "");
			}

		});
	}

	public void processingComplete() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}

		});
	}
}
