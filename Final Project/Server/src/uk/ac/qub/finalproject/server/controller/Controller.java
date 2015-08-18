/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.ResultProcessor;
import uk.ac.qub.finalproject.calculationclasses.ResultsValidator;
import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;
import uk.ac.qub.finalproject.persistence.ConnectionPool;
import uk.ac.qub.finalproject.persistence.DatabaseCreator;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;
import uk.ac.qub.finalproject.persistence.WorkPacketDrawerImpl;
import uk.ac.qub.finalproject.server.CalculationFinishedRequestHandler;
import uk.ac.qub.finalproject.server.CatchAllRequestHandler;
import uk.ac.qub.finalproject.server.ChangeEmailRequestHandler;
import uk.ac.qub.finalproject.server.DeleteAccountRequestHandler;
import uk.ac.qub.finalproject.server.ProcessResultRequestHandler;
import uk.ac.qub.finalproject.server.RegisterRequestHandler;
import uk.ac.qub.finalproject.server.Server;
import uk.ac.qub.finalproject.server.WorkPacketRequestHandler;
import uk.ac.qub.finalproject.server.implementations.Implementations;
import uk.ac.qub.finalproject.server.views.MainScreenView;

/**
 * @author Phil
 *
 */
public class Controller extends Application implements Observer {

	private AbstractWorkPacketDrawer workPacketDrawer;
	private AbstractWorkPacketLoader workPacketLoader;
	private AbstractResultsTransferManager resultsTransferManager;
	private DatabaseCreator databaseCreator;
	private DeviceDetailsManager deviceDetailsManager;
	private DeviceVersionManager deviceVersionManager;
	private ResultsPacketManager resultsPacketManager;
	private UserDetailsManager userDetailsManager;

	private CalculationFinishedRequestHandler calculationFinishedRequestHandler;
	private CatchAllRequestHandler catchAllRequestHandler;
	private ChangeEmailRequestHandler changeEmailRequestHandler;
	private DeleteAccountRequestHandler deleteAccountRequestHandler;
	private ProcessResultRequestHandler processResultRequestHandler;
	private RegisterRequestHandler registerRequestHandler;
	private WorkPacketRequestHandler workPacketRequestHandler;
	private Server server;

	private IResultValidator resultValidator;
	private IValidationStrategy validationStrategy;
	private IGroupValidationStrategy groupValidationStrategy;
	private ResultProcessor resultProcessor;

	private ActiveDeviceThresholdChangeListener deviceThresholdChangeListener;
	private BlacklistChangeListener blacklistChangeListener;
	private DuplicatesNumChangeListener duplicatesNumChangeListener;
	private PacketsNumChangeListener packetsNumChangeListener;
	private LoadAdditionalWorkPacketsCommand loadWorkPacketsCommand;
	private StartServerCommand startServerCommand;
	private StopSendingPacketsCommand stopSendingPacketsCommand;
	private TransferResultsCommand transferResultsCommand;

	private MainScreenView mainScreen;
	private Stage primaryStage;
	private AnchorPane rootLayout;

	public void setupSystem() {
		setupPersistence();
		setupValidation();
		setupServer();
		setupListeners();
		setupCommands();
	}

	public void setupPersistence() {
		databaseCreator = new DatabaseCreator();
		workPacketDrawer = new WorkPacketDrawerImpl();
		workPacketLoader = Implementations
				.getWorkPacketLoader(workPacketDrawer);
		resultsTransferManager = Implementations.getResultsTransferManager();

		deviceDetailsManager = new DeviceDetailsManager();
		deviceVersionManager = new DeviceVersionManager();
		resultsPacketManager = new ResultsPacketManager();
		userDetailsManager = new UserDetailsManager();

		deviceDetailsManager.setUserDetailsManager(userDetailsManager);
		userDetailsManager.setDeviceManager(deviceDetailsManager);

		/*
		 * databaseCreator.setupDatabase(); workPacketLoader.loadWorkPackets();
		 * workPacketDrawer.reloadIncompletedWorkPackets();
		 * deviceDetailsManager.loadDevices();
		 * deviceVersionManager.loadDeviceVersions();
		 * resultsPacketManager.loadResultsPackets();
		 */

		workPacketDrawer.addObserver(this);
		deviceDetailsManager.addObserver(this);
		resultsPacketManager.addObserver(this);
	}

	public void setupValidation() {

		// if you need group validation change the name to a
		// GroupResultsValidator
		resultValidator = new ResultsValidator(resultsPacketManager,
				deviceDetailsManager);

		// change this to your own validation strategy
		validationStrategy = Implementations.getValidationStrategy();

		// if you need group validation change this to your own group validation
		// strategy
		groupValidationStrategy = Implementations.getGroupValidationStrategy();

		resultProcessor = new ResultProcessor(deviceDetailsManager,
				resultsPacketManager, resultValidator, workPacketDrawer);

		resultValidator.setValidationStrategy(validationStrategy);
		resultValidator.setGroupValidationStrategy(groupValidationStrategy);
		resultProcessor.addObserver(this);
	}

	public void setupServer() {
		calculationFinishedRequestHandler = new CalculationFinishedRequestHandler(
				deviceDetailsManager, deviceVersionManager, userDetailsManager);
		catchAllRequestHandler = new CatchAllRequestHandler();
		changeEmailRequestHandler = new ChangeEmailRequestHandler(
				userDetailsManager);
		deleteAccountRequestHandler = new DeleteAccountRequestHandler(
				deviceDetailsManager);
		processResultRequestHandler = new ProcessResultRequestHandler(
				deviceDetailsManager, workPacketDrawer, resultProcessor);
		registerRequestHandler = new RegisterRequestHandler(
				deviceDetailsManager, deviceVersionManager);
		workPacketRequestHandler = new WorkPacketRequestHandler(
				workPacketDrawer, deviceDetailsManager, deviceVersionManager);

		// chain the handlers together
		changeEmailRequestHandler.setNextHandler(deleteAccountRequestHandler);
		deleteAccountRequestHandler.setNextHandler(processResultRequestHandler);
		processResultRequestHandler.setNextHandler(registerRequestHandler);
		registerRequestHandler.setNextHandler(workPacketRequestHandler);
		workPacketRequestHandler.setNextHandler(catchAllRequestHandler);

		// setup the server
		server = new Server();
		server.setRequestHandlers(changeEmailRequestHandler);

	}

	public void setupListeners() {
		deviceThresholdChangeListener = new ActiveDeviceThresholdChangeListener(
				deviceDetailsManager);
		blacklistChangeListener = new BlacklistChangeListener(
				deviceDetailsManager);
		duplicatesNumChangeListener = new DuplicatesNumChangeListener(
				workPacketDrawer);
		packetsNumChangeListener = new PacketsNumChangeListener(
				workPacketDrawer);
	}

	public void setupCommands() {
		loadWorkPacketsCommand = new LoadAdditionalWorkPacketsCommand(
				workPacketLoader);
		startServerCommand = new StartServerCommand(server);
		stopSendingPacketsCommand = new StopSendingPacketsCommand(server,
				calculationFinishedRequestHandler);
		transferResultsCommand = new TransferResultsCommand(
				resultsTransferManager);

	}

	public void setupRootLayout() {

	}

	public void setupView() {
		primaryStage.close();

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(
					"/uk/ac/qub/finalproject/server/views/MainScreen.fxml"));
			AnchorPane mainView = (AnchorPane) loader.load();
			mainScreen = loader.getController();

			mainScreen.setupWidgets();
			mainScreen
					.setActiveDeviceThresholdChangeListener(deviceThresholdChangeListener);
			mainScreen.setBlacklistChangeListener(blacklistChangeListener);
			mainScreen
					.setDuplicatesNumChangeListener(duplicatesNumChangeListener);
			mainScreen.setPacketsNumChangeListener(packetsNumChangeListener);

			mainScreen
					.setLoadAdditionalWorkPacketsCommand(loadWorkPacketsCommand);
			mainScreen.setStartServerCommand(startServerCommand);
			mainScreen.setStopSendingPacketsCommand(stopSendingPacketsCommand);
			mainScreen.setTransferResultsCommand(transferResultsCommand);

			if (rootLayout.getChildren().size() > 0) {
				rootLayout.getChildren().remove(0);
			}

			rootLayout.getChildren().add(mainView);
			AnchorPane.setTopAnchor(rootLayout.getChildren().get(0), 0.0);
			AnchorPane.setBottomAnchor(rootLayout.getChildren().get(0), 0.0);
			AnchorPane.setLeftAnchor(rootLayout.getChildren().get(0), 0.0);
			AnchorPane.setRightAnchor(rootLayout.getChildren().get(0), 0.0);

			primaryStage.setMaximized(true);
			;
			primaryStage.setResizable(true);

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					cleanupBeforeClose();
				}

			});

			primaryStage.show();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	public void startActiveDeviceUpdates() {
		final ScheduledExecutorService runner = Executors
				.newScheduledThreadPool(1);

		// start the timer to update the active devices view every minute on
		// their own thread
		runner.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				int activeDevices = deviceDetailsManager
						.numberOfActiveDevices();
				mainScreen.updateActiveDevices(activeDevices);
			}

		}, 0, 1000 * 60, TimeUnit.MILLISECONDS);
	}

	private void cleanupBeforeClose() {
		try {
			ConnectionPool.getConnectionPool().closeConnectionPool();
		} catch (PropertyVetoException e) {
			// TODO logging
		}

		server.stopServer();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0.getClass().equals(resultProcessor.getClass())) {
			String request = (String) arg1;
			processorUpdate(request);
		} else if (arg0.getClass().equals(deviceDetailsManager.getClass())) {
			deviceDetailsUpdate();
		} else if (arg0.getClass().equals(resultsPacketManager.getClass())) {
			resultsPacketUpdate();
		} else if (arg0.getClass().equals(workPacketDrawer.getClass())) {
			// keeping this as a separate condition as it is likely that
			// requirements may change here e.g. updating the view every time a
			// new packet is sent out
			resultsPacketUpdate();
		}

	}

	private void processorUpdate(String request) {
		if (request.equals(ResultProcessor.LOAD_MORE_WORK_PACKETS)) {
			workPacketDrawer.reloadIncompletedWorkPackets();
		} else if (request.equals(ResultProcessor.PROCESSING_COMPLETE)) {
			stopSendingPacketsCommand.execute();
			updateViewProcessingComplete();
		} else {
			updateProcessingTimes(request);
		}
	}

	private void deviceDetailsUpdate() {
		long validResults = deviceDetailsManager.numberOfValidResults();
		long invalidResults = deviceDetailsManager.numberOfInvalidResults();
		long averageTime = deviceDetailsManager.getAverageProcessingTime();
		String time = String.format("%d min, %d sec",
				TimeUnit.MILLISECONDS.toSeconds(averageTime) / 60,
				TimeUnit.MILLISECONDS.toSeconds(averageTime) % 60);

		mainScreen.updateBlacklistedDevices(deviceDetailsManager
				.numberOfBlacklistedDevices());
		mainScreen.updatePacketStats(validResults, invalidResults);
		mainScreen
				.updateTotalNumDevices(deviceDetailsManager.numberOfDevices());
		mainScreen.updateAverageProcessingTime(time);
	}

	private void resultsPacketUpdate() {
		int resultsCompleted = resultsPacketManager
				.getNumberOfPacketsProcessed();
		int totalPackets = workPacketDrawer.numberOfDistinctWorkPackets();
		mainScreen.updateProgress(resultsCompleted, totalPackets);
	}

	private void updateViewProcessingComplete() {
		// show server infographic?
		mainScreen.processingComplete();
	}

	private void updateProcessingTimes(String minutes) {
		mainScreen.addProcessingTime(Integer.parseInt(minutes));
	}

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		this.primaryStage = arg0;

		primaryStage.setTitle(Implementations.getServerScreenTitle());

		// initiate root layout
		FXMLLoader rootLoader = new FXMLLoader();
		rootLoader.setLocation(getClass().getResource(
				"/uk/ac/qub/finalproject/server/views/RootLayout.fxml"));
		rootLayout = (AnchorPane) rootLoader.load();

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setHeight(420);
		primaryStage.setWidth(600);
		primaryStage.show();

		// TODO show loading screen

		setupSystem();
		setupView();
		// startActiveDeviceUpdates();

		// TODO show main screen

	}

}
