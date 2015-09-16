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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
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

	public void setupWidgets() {
		setupButtons();
		setPacketsNumComboBox();
		setDuplicatesNumComboBox();
		setActiveDeviceThresholdComboBox();
		setupChart();
		setupMenuItems();
	}

	public void setupButtons() {
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startCommand.execute();
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
		activeDeviceThresholdComboBox.getItems().addAll("5 mins", "10 mins",
				"15 mins", "30 mins", "1 hour", "2 hours", "3 hours",
				"5 hours", "8 hours");
		activeDeviceThresholdComboBox
				.setValue(DeviceDetailsManager.DEFAULT_ACTIVE_DEVICE_THRESHOLD
						+ " mins");
		activeDeviceThresholdComboBox.setVisibleRowCount(4);
	}

	public void setupChart() {
		// add an initial time to the chart
		// this ensures a continuous line from
		// the bottom of the X/Y axis
		processingTimesMap.put(0, 0);

		processingTimeLineChart.getData().add(processingTimeSeries);
		NumberAxis xAxis = (NumberAxis) processingTimeLineChart.getXAxis();
		NumberAxis yAxis = (NumberAxis) processingTimeLineChart.getYAxis();
		xAxis.setTickLabelFormatter(numberFormatter);
		yAxis.setTickLabelFormatter(numberFormatter);

	}

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
				} catch (ArithmeticException Ex) {

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

	private void startProgressIndicator(String progressMessage) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				databaseProgressLabel.setText(progressMessage);
				databaseProgressBar.setVisible(true);
				databaseProcessingPane.setVisible(true);
			}

		});
	}

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

	private void closeWaitMessage() {
		Platform.runLater(new Runnable() {

			public void run() {
				if (databaseAlert != null && databaseAlert.isShowing()) {
					databaseAlert.close();
				}
			}

		});

	}

	public void closeApplication() {
		Platform.exit();
	}

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
					newStage.show();
				} catch (IOException e) {

				}
			}

		});

	}

}
