/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.WorkPacketDrawerImpl;
import uk.ac.qub.finalproject.server.controller.BlacklistChangeListener;
import uk.ac.qub.finalproject.server.controller.Command;
import uk.ac.qub.finalproject.server.implementations.Implementations;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is the controller class for the UI actions of the Main Screen. It must
 * have a reference to commands and action listeners passed in before the UI can
 * be opened.
 * 
 * @author Phil
 *
 */
public class MainScreenView {

	private static final String LOADING_PACKETS_PROGRESS_MESSAGE = "Loading Work Packets";

	private static final String RESULTS_TRANSFER_PROGRESS_MESSAGE = "Transferring Results";

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

	@FXML
	private Label databaseProgressLabel;

	@FXML
	private GridPane databaseProcessingPane;

	@FXML
	private ProgressBar databaseProgressBar;

	@FXML
	private MenuItem quitMenuItem;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private MenuItem closeMenuItem;

	private Alert databaseAlert;
	private NumberFormatter numberFormatter = new NumberFormatter();

	private Command startCommand;
	private Command loadAdditionalWorkPacketsCommand;
	private Command stopSendingPacketsCommand;
	private Command transferResultsCommand;

	private ConcurrentMap<Number, Integer> processingTimesMap = new ConcurrentHashMap<Number, Integer>();
	private int totalDevices;

	/**
	 * Sets up all the widgets on screen, including event listeners.
	 */
	public void setupWidgets() {
		setupButtons();
		setPacketsNumComboBox();
		setDuplicatesNumComboBox();
		setActiveDeviceThresholdComboBox();
		setupChart();
		setupMenuItems();
	}

	/**
	 * Sets up the buttons on screen and assigns them with appropriate event
	 * handlers.
	 */
	public void setupButtons() {
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startCommand.execute();
				startButton.setDisable(true);
				stopButton.setDisable(false);
			}
		});

		startButton.setAccessibleText("Start Button");
		startButton
				.setAccessibleHelp("Click this to start sending packets to be processed");

		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				stopButton.setDisable(true);
				startButton.setDisable(false);
				stopSendingPacketsCommand.execute();
			}

		});

		stopButton.setAccessibleText("Stop Button");
		stopButton
				.setAccessibleHelp("Click this to stop sending packets to be processed");

		loadAdditionalPacketsButton
				.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						if (transferResultsButton.isDisabled()) {
							showWaitMessage("Still Transferring Results",
									"transferring results.");
						} else {
							loadAdditionalPacketsButton.setDisable(true);
							startProgressIndicator(LOADING_PACKETS_PROGRESS_MESSAGE);

							Task<Void> loadPackets = new Task<Void>() {

								@Override
								protected Void call() throws Exception {
									loadAdditionalWorkPacketsCommand.execute();
									return null;
								}

							};

							loadPackets.stateProperty().addListener(
									new ChangeListener<Worker.State>() {

										@Override
										public void changed(
												ObservableValue<? extends State> observable,
												State oldValue, State newValue) {
											if (newValue == Worker.State.SUCCEEDED) {
												stopProgressIndicator();
												closeWaitMessage();
												showDatabaseConfirmation(
														"Loading Complete",
														"Packets Loaded",
														"All new packets have been loaded to the system");

												loadAdditionalPacketsButton
														.setDisable(false);
											}

										}

									});

							new Thread(loadPackets).start();
						}
					}

				});

		loadAdditionalPacketsButton
				.setAccessibleText("Load Work Packets Button");
		loadAdditionalPacketsButton
				.setAccessibleHelp("Use this button to load more work packets from the database");

		transferResultsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (loadAdditionalPacketsButton.isDisabled()) {
					showWaitMessage("Still Loading Packets", "loading packets.");
				} else {
					transferResultsButton.setDisable(true);
					startProgressIndicator(RESULTS_TRANSFER_PROGRESS_MESSAGE);

					Task<Void> transferTask = new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							transferResultsCommand.execute();
							return null;
						}

					};

					transferTask.stateProperty().addListener(
							new ChangeListener<Worker.State>() {

								@Override
								public void changed(
										ObservableValue<? extends Worker.State> observable,
										Worker.State oldValue,
										Worker.State newValue) {
									if (newValue == Worker.State.SUCCEEDED) {
										stopProgressIndicator();
										closeWaitMessage();
										showDatabaseConfirmation(
												"Results Transferred",
												"Results Transferred",
												"All results have been transferred to your database.");
										transferResultsButton.setDisable(false);
									}
								}

							});

					new Thread(transferTask).start();

				}
			}

		});

		transferResultsButton.setAccessibleText("Transfer Results Button");
		transferResultsButton
				.setAccessibleHelp("Use this to transfer results packets to your database");

		fivePercentBlacklistRadioButton.setUserData("5");
		tenPercentBlacklistRadioButton.setUserData("10");
		twentyPercentBlacklistRadioButton.setUserData("20");
		thirtyPercentBlacklistRadioButton.setUserData("30");

		/*
		 * Bug fix. THe progress bar blocks if it hasn't previously been set
		 * visible.
		 */
		databaseProgressBar.setVisible(true);
		databaseProgressBar.setVisible(false);

	}

	/**
	 * Sets the packets number combo box with a list of options.
	 */
	public void setPacketsNumComboBox() {
		packetsNumComboBox.getItems()
				.addAll(2, 3, 5, 8, 10, 15, 20, 30, 40, 50);
		packetsNumComboBox
				.setValue(WorkPacketDrawerImpl.DEFAULT_PACKETS_PER_LIST);
		packetsNumComboBox.setVisibleRowCount(4);

		packetsNumComboBox.setAccessibleText("Packets per list Combobox");
		packetsNumComboBox
				.setAccessibleHelp("This changes the number of packets sent out in each work packet list");

	}

	/**
	 * Sets the duplicates number combo box with a list of options.
	 */
	public void setDuplicatesNumComboBox() {
		duplicatesNumComboBox.getItems().addAll(5, 8, 10, 15, 20);
		duplicatesNumComboBox
				.setValue(WorkPacketDrawerImpl.DEFAULT_TIMES_TO_SEND_PACKET_LIST);
		duplicatesNumComboBox.setVisibleRowCount(4);

		duplicatesNumComboBox.setAccessibleText("Duplicate packets Combobox");
		duplicatesNumComboBox
				.setAccessibleHelp("This sets the number of times each work packet is sent");
	}

	/**
	 * Sets the active device threshold combo box with a list of options.
	 */
	public void setActiveDeviceThresholdComboBox() {
		activeDeviceThresholdComboBox.getItems().addAll("5 mins", "10 mins",
				"15 mins", "30 mins", "1 hour", "2 hours", "3 hours",
				"5 hours", "8 hours");
		activeDeviceThresholdComboBox
				.setValue(DeviceDetailsManager.DEFAULT_ACTIVE_DEVICE_THRESHOLD
						+ " mins");
		activeDeviceThresholdComboBox.setVisibleRowCount(4);

		activeDeviceThresholdComboBox
				.setAccessibleText("Active Device Threshold Combobox");
		activeDeviceThresholdComboBox
				.setAccessibleHelp("Use this to set when a device becomes inactive");
	}

	/**
	 * Sets up the line chart.
	 */
	public void setupChart() {
		// add an initial time to the chart
		// this ensures a continuous line from
		// the bottom of the X/Y axis
		processingTimesMap.put(0, 0);

		processingTimeLineChart.getData().add(processingTimeSeries);
		NumberAxis xAxis = (NumberAxis) processingTimeLineChart.getXAxis();
		NumberAxis yAxis = (NumberAxis) processingTimeLineChart.getYAxis();
		xAxis.setTickLabelFormatter(numberFormatter);
		xAxis.setLowerBound(10);
		yAxis.setTickLabelFormatter(numberFormatter);
		yAxis.setLowerBound(10);

	}

	/**
	 * Sets up the menu items with appropriate event listeners.
	 */
	public void setupMenuItems() {
		quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				closeApplication();
			}
		});

		aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showAboutWindow();
			}
		});

		closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				closeApplication();
			}
		});
	}

	/**
	 * Sets the change listener for the blacklist.
	 * 
	 * @param changeListener
	 */
	public void setBlacklistChangeListener(
			BlacklistChangeListener changeListener) {
		blacklistPercent.selectedToggleProperty().addListener(changeListener);
	}

	/**
	 * Sets the change listener for changes to the packets number combo box.
	 * 
	 * @param changeListener
	 */
	public void setPacketsNumChangeListener(
			ChangeListener<Integer> changeListener) {
		packetsNumComboBox.valueProperty().addListener(changeListener);
	}

	/**
	 * Sets the change listener for changes to the duplicates number combo box.
	 * 
	 * @param changeListener
	 */
	public void setDuplicatesNumChangeListener(
			ChangeListener<Integer> changeListener) {
		duplicatesNumComboBox.valueProperty().addListener(changeListener);
	}

	/**
	 * Sets the change listener for changes to the active device threshold combo
	 * box.
	 * 
	 * @param changeListener
	 */
	public void setActiveDeviceThresholdChangeListener(
			ChangeListener<String> changeListener) {
		activeDeviceThresholdComboBox.valueProperty().addListener(
				changeListener);
	}

	/**
	 * Sets the command to load additional work packets.
	 * 
	 * @param command
	 */
	public void setLoadAdditionalWorkPacketsCommand(Command command) {
		this.loadAdditionalWorkPacketsCommand = command;
	}

	/**
	 * Sets the start server command to the appropriate command.
	 * 
	 * @param command
	 */
	public void setStartServerCommand(Command command) {
		this.startCommand = command;
	}

	/**
	 * Sets the stop sending packetscommand to the appropriate command.
	 * 
	 * @param command
	 */
	public void setStopSendingPacketsCommand(Command command) {
		this.stopSendingPacketsCommand = command;
	}

	/**
	 * Sets the transfer results command to the appropriate command.
	 * 
	 * @param command
	 */
	public void setTransferResultsCommand(Command command) {
		this.transferResultsCommand = command;
	}

	/**
	 * Updates the progress of the computation on screen.
	 * 
	 * @param packetsComplete
	 * @param totalPackets
	 */
	public void updateProgress(long packetsComplete, long totalPackets) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double progress = packetsComplete / (double) totalPackets;
				int percentComplete = (int) (progress * 100);

				processingProgressBar.setProgress(progress);
				packetProgressLabel.setText(packetsComplete + "/"
						+ totalPackets);
				packetProgressLabel.setAccessibleText(packetsComplete + " of "
						+ totalPackets + " completed");
				percentPacketsCompleteLabel.setText(percentComplete + " %");
				percentPacketsCompleteLabel.setAccessibleText(percentComplete
						+ " percent complete");
			}

		});
	}

	/**
	 * Adds another processing time value to the line chart.
	 * 
	 * @param minutes
	 */
	public void addProcessingTime(int minutes) {

		if (processingTimesMap.containsKey(minutes)) {
			Integer devices = processingTimesMap.get(minutes);
			processingTimesMap.put(minutes, ++devices);
		} else {
			processingTimesMap.put(minutes, 1);
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				processingTimeSeries.getData().clear();

				for (Number minute : processingTimesMap.keySet()) {
					Number devices = processingTimesMap.get(minute);
					processingTimeSeries.getData().add(
							new XYChart.Data<Number, Number>(minute, devices));
				}
			}
		});

	}

	/**
	 * Updates the average processing time on screen.
	 * 
	 * @param averageTime
	 */
	public void updateAverageProcessingTime(String averageTime) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				averageTimeLabel.setText(averageTime);
				averageTimeLabel
						.setAccessibleText("The average processing time is "
								+ averageTime);
			}

		});
	}

	/**
	 * Updates the packet statistics on screen.
	 * 
	 * @param validPackets
	 * @param invalidPackets
	 */
	public void updatePacketStats(long validPackets, long invalidPackets) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					double percentBadPackets = invalidPackets
							/ (validPackets + invalidPackets);
					int percent = (int) (percentBadPackets * 100);
					percentBadPacketsLabel.setText(percent + " %");
					percentBadPacketsLabel.setAccessibleText(percent
							+ " percent bad packets");
				} catch (ArithmeticException Ex) {

				}
			}

		});
	}

	/**
	 * Updates the data on active devices on screen.
	 * 
	 * @param activeDevices
	 */
	public void updateActiveDevices(int activeDevices) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				activeDevicesLabel.setText(activeDevices + "");
				activeDevicesLabel.setAccessibleText(activeDevices
						+ " active devices");
				inactiveDevicesLabel.setText((totalDevices - activeDevices)
						+ "");
				inactiveDevicesLabel
						.setAccessibleText((totalDevices - activeDevices)
								+ " inactive devices");
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
				inactiveDevicesLabel
						.setAccessibleText((devices - activeDevices)
								+ " inactive devices");
			}

		});
	}

	/**
	 * Updates the data on blacklisted devices on screen.
	 * 
	 * @param blacklistedDevices
	 */
	public void updateBlacklistedDevices(int blacklistedDevices) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				blacklistedDevicesLabel.setText(blacklistedDevices + "");
				blacklistedDevicesLabel.setAccessibleText(blacklistedDevices
						+ " blacklisted devices");
			}

		});
	}

	/**
	 * Shows an alert to the user when processing is complete. This allows them
	 * to decide on the next action to take.
	 */
	public void processingComplete() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert processingComplete = new Alert(AlertType.INFORMATION);
				processingComplete.setTitle("Processing Complete");
				processingComplete
						.setHeaderText("All packets have been processed.");
				processingComplete
						.setContentText("Finish by transferring all results, or load more packets to continue processing.");
				processingComplete.showAndWait();
			}

		});
	}

	/**
	 * Helper method starts the progress indicator in the corner.
	 * 
	 * @param progressMessage
	 */
	private void startProgressIndicator(String progressMessage) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				databaseProgressLabel.setText(progressMessage);
				databaseProgressLabel.setAccessibleText(progressMessage);
				databaseProgressBar.setVisible(true);
				databaseProcessingPane.setVisible(true);
			}

		});
	}

	/**
	 * Helper method stops the progress indicator.
	 */
	private void stopProgressIndicator() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				databaseProcessingPane.setVisible(false);
				databaseProgressBar.setVisible(false);
				databaseProgressLabel.setText("");
			}

		});
	}

	/**
	 * Helper method shows confirmation of success once the database action has
	 * completed.
	 * 
	 * @param title
	 * @param headerText
	 * @param contentText
	 */
	private void showDatabaseConfirmation(String title, String headerText,
			String contentText) {
		Platform.runLater(new Runnable() {

			public void run() {
				databaseAlert = new Alert(AlertType.INFORMATION);
				databaseAlert.setTitle(title);
				databaseAlert.setHeaderText(headerText);
				databaseAlert.setContentText(contentText);
				databaseAlert.showAndWait();
			}

		});

	}

	/**
	 * Shows a wait message to the user if they try to trigger a second database
	 * action while the first action is completing.
	 * 
	 * @param title
	 * @param contentText
	 */
	private void showWaitMessage(String title, String contentText) {
		Platform.runLater(new Runnable() {

			public void run() {
				databaseAlert = new Alert(AlertType.WARNING);
				databaseAlert.setTitle(title);
				databaseAlert.setHeaderText("");
				databaseAlert
						.setContentText("Please try again when the database has finished "
								+ contentText);
				databaseAlert.showAndWait();
			}

		});
	}

	/**
	 * Helper method closes the wait message when a database action completes.
	 */
	private void closeWaitMessage() {
		Platform.runLater(new Runnable() {

			public void run() {
				if (databaseAlert != null && databaseAlert.isShowing()) {
					databaseAlert.close();
				}
			}

		});

	}

	/**
	 * Helper method to close the application.
	 */
	public void closeApplication() {
		Platform.exit();
	}

	/**
	 * Shows the about window to the user.
	 */
	public void showAboutWindow() {
		Platform.runLater(new Runnable() {

			public void run() {
				try {
					Stage newStage = new Stage();
					newStage.setTitle("About "
							+ Implementations.getServerScreenTitle());
					newStage.initModality(Modality.APPLICATION_MODAL);

					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass()
							.getResource(
									"/uk/ac/qub/finalproject/server/views/AboutWindow.fxml"));

					AnchorPane aboutView = (AnchorPane) loader.load();
					AboutWindowView controller = loader.getController();
					controller
							.addCloseButtonListener(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {
									newStage.close();
								}
							});
					controller.setProjectNameText(Implementations
							.getServerScreenTitle());
					controller.initialiseHyperLinkListener();

					Scene scene = new Scene(aboutView);
					newStage.setScene(scene);
					newStage.setResizable(false);
					newStage.getIcons()
							.add(new Image(
									getClass()
											.getResourceAsStream(
													"/uk/ac/qub/finalproject/server/views/ic_launcher.png")));
					newStage.show();
				} catch (IOException e) {

				}
			}

		});

	}

}
