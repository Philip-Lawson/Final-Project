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
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import uk.ac.qub.finalproject.calculationclasses.GroupResultsValidator;
import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.ResultProcessor;
import uk.ac.qub.finalproject.calculationclasses.SingleResultsValidator;
import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;
import uk.ac.qub.finalproject.persistence.ConnectionPool;
import uk.ac.qub.finalproject.persistence.DatabaseCreator;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DeviceVersionManager;
import uk.ac.qub.finalproject.persistence.LoggingUtils;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;
import uk.ac.qub.finalproject.persistence.UserDetailsManager;
import uk.ac.qub.finalproject.persistence.WorkPacketDrawerImpl;
import uk.ac.qub.finalproject.server.implementations.Implementations;
import uk.ac.qub.finalproject.server.networking.CalculationFinishedRequestHandler;
import uk.ac.qub.finalproject.server.networking.CatchAllRequestHandler;
import uk.ac.qub.finalproject.server.networking.ChangeEmailRequestHandler;
import uk.ac.qub.finalproject.server.networking.DeleteAccountRequestHandler;
import uk.ac.qub.finalproject.server.networking.ProcessResultRequestHandler;
import uk.ac.qub.finalproject.server.networking.RegisterRequestHandler;
import uk.ac.qub.finalproject.server.networking.Server;
import uk.ac.qub.finalproject.server.networking.WorkPacketRequestHandler;
import uk.ac.qub.finalproject.server.views.LoadingScreenView;
import uk.ac.qub.finalproject.server.views.MainScreenView;

/**
 * This is the starting point of the application, and the class that controls
 * all interaction between the views and the domain. It also controls the setup
 * of the system.
 * 
 * @author Phil
 *
 */
public class Controller extends Application implements Observer {

	private Logger logger = LoggingUtils.getLogger(Controller.class);

	/*
	 * The persistence objects.
	 */
	private AbstractWorkPacketDrawer workPacketDrawer;
	private AbstractWorkPacketLoader workPacketLoader;
	private AbstractResultsTransferManager resultsTransferManager;
	private DatabaseCreator databaseCreator;
	private DeviceDetailsManager deviceDetailsManager;
	private DeviceVersionManager deviceVersionManager;
	private ResultsPacketManager resultsPacketManager;
	private UserDetailsManager userDetailsManager;

	/*
	 * Request handlers and the server.
	 */
	private CalculationFinishedRequestHandler calculationFinishedRequestHandler;
	private CatchAllRequestHandler catchAllRequestHandler;
	private ChangeEmailRequestHandler changeEmailRequestHandler;
	private DeleteAccountRequestHandler deleteAccountRequestHandler;
	private ProcessResultRequestHandler processResultRequestHandler;
	private RegisterRequestHandler registerRequestHandler;
	private WorkPacketRequestHandler workPacketRequestHandler;
	private Server server;

	/*
	 * Validation and result processing objects.
	 */
	private IResultValidator resultValidator;
	private IValidationStrategy validationStrategy;
	private IGroupValidationStrategy groupValidationStrategy;
	private ResultProcessor resultProcessor;

	/*
	 * Event listeners and commands for the UI.
	 */
	private ActiveDeviceThresholdChangeListener deviceThresholdChangeListener;
	private BlacklistChangeListener blacklistChangeListener;
	private DuplicatesNumChangeListener duplicatesNumChangeListener;
	private PacketsNumChangeListener packetsNumChangeListener;
	private LoadAdditionalWorkPacketsCommand loadWorkPacketsCommand;
	private StartServerCommand startServerCommand;
	private StopSendingPacketsCommand stopSendingPacketsCommand;
	private TransferResultsCommand transferResultsCommand;

	/*
	 * Stages and views for the UI.
	 */
	private Stage primaryStage;
	private Stage mainStage;
	private LoadingScreenView loadingScreen;
	private MainScreenView mainScreen;

	/**
	 * The executor service that controls the active device update thread.
	 */
	private final ScheduledExecutorService runner = Executors
			.newScheduledThreadPool(1);

	/**
	 * Helper method to set up the system. This should be called on startup.
	 */
	public void setupSystem() {
		setupPersistence();
		setupValidation();
		setupServer();
		setupListeners();
		setupCommands();
	}

	/**
	 * Sets up the persistence layer.
	 */
	public void setupPersistence() {
		loadingScreen.updateProgress("Creating Database", 0);

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

		databaseCreator.setupDatabase();

		loadingScreen.updateProgress("Loading Work Packets", 10);
		workPacketLoader.loadWorkPackets();
		loadingScreen.updateProgress("Loading Incomplete Work Packets", 30);
		workPacketDrawer.reloadIncompletedWorkPackets();
		loadingScreen.updateProgress("Loading Devices", 50);
		deviceDetailsManager.loadDevices();
		deviceVersionManager.loadDeviceVersions();
		loadingScreen.updateProgress("Loading Results Packets", 70);
		resultsPacketManager.loadResultsPackets();

		workPacketDrawer.addObserver(this);
		deviceDetailsManager.addObserver(this);
		resultsPacketManager.addObserver(this);
	}

	/**
	 * Sets up the validation and processing objects.
	 */
	public void setupValidation() {
		if (Implementations.groupValidationNeeded()) {
			resultValidator = new GroupResultsValidator(resultsPacketManager,
					deviceDetailsManager);
			groupValidationStrategy = Implementations
					.getGroupValidationStrategy();
			resultValidator.setGroupValidationStrategy(groupValidationStrategy);
		} else {
			resultValidator = new SingleResultsValidator(resultsPacketManager,
					deviceDetailsManager);
		}

		validationStrategy = Implementations.getValidationStrategy();
		resultValidator.setValidationStrategy(validationStrategy);

		resultProcessor = new ResultProcessor(deviceDetailsManager,
				resultsPacketManager, resultValidator, workPacketDrawer);
		resultProcessor.addObserver(this);
	}

	/**
	 * Sets up all networking objects.
	 */
	public void setupServer() {
		loadingScreen.updateProgress("Setting up Server", 80);

		calculationFinishedRequestHandler = new CalculationFinishedRequestHandler(
				deviceDetailsManager, deviceVersionManager, userDetailsManager);
		catchAllRequestHandler = new CatchAllRequestHandler();
		changeEmailRequestHandler = new ChangeEmailRequestHandler(
				userDetailsManager);
		deleteAccountRequestHandler = new DeleteAccountRequestHandler(
				deviceDetailsManager);
		processResultRequestHandler = new ProcessResultRequestHandler(
				deviceDetailsManager, deviceVersionManager, workPacketDrawer,
				resultProcessor);
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

	/**
	 * Sets up the UI listeners.
	 */
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

	/**
	 * Sets up the UI commands.
	 */
	public void setupCommands() {
		loadWorkPacketsCommand = new LoadAdditionalWorkPacketsCommand(
				workPacketLoader);
		startServerCommand = new StartServerCommand(server,
				changeEmailRequestHandler);
		stopSendingPacketsCommand = new StopSendingPacketsCommand(server,
				calculationFinishedRequestHandler);
		transferResultsCommand = new TransferResultsCommand(
				resultsTransferManager);

	}

	/**
	 * Sets up the loading screen and shows it to the user.
	 */
	public void showLoadingScreen() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(
					"/uk/ac/qub/finalproject/server/views/LoadingScreen.fxml"));
			AnchorPane loadingView = (AnchorPane) loader.load();
			loadingScreen = loader.getController();

			Scene scene = new Scene(loadingView);
			primaryStage.setScene(scene);

			loadingScreen.setTitle(Implementations.getServerScreenTitle());
			primaryStage.setHeight(125);
			primaryStage.setWidth(400);
			primaryStage.setResizable(false);
			primaryStage.centerOnScreen();
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.getIcons()
			.add(new Image(getClass().getResourceAsStream(
					"/uk/ac/qub/finalproject/server/views/ic_launcher.png")));

			primaryStage.show();
		} catch (IOException e) {

		}

	}

	/**
	 * Helper method to update the loading screen when loading is complete.
	 */
	private void loadingComplete() {
		loadingScreen.updateProgress("Complete", 100);
	}

	/**
	 * Sets up the main screen and shows it to the user.
	 */
	public void showMainScreen() {
		try {
			mainStage = new Stage(StageStyle.DECORATED);
			mainStage.setTitle(Implementations.getServerScreenTitle());

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(
					"/uk/ac/qub/finalproject/server/views/MainScreen.fxml"));
			AnchorPane mainView = (AnchorPane) loader.load();
			mainScreen = loader.getController();

			Scene scene = new Scene(mainView);
			mainStage.setScene(scene);

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

			mainStage.setHeight(600);
			mainStage.setWidth(1200);
			mainStage.setMaximized(true);
			mainStage.setResizable(true);
			mainStage.getIcons()
					.add(new Image(getClass().getResourceAsStream(
							"/uk/ac/qub/finalproject/server/views/ic_launcher.png")));

			mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					cleanupBeforeClose();
				}

			});

			if (primaryStage.isShowing()) {
				primaryStage.close();
			}

			mainStage.show();

			// make sure the main screen is up to date
			resultsPacketUpdate();
			deviceDetailsUpdate();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	/**
	 * start the timer to update the active devices view every minute on a
	 * separate thread
	 */
	public void startActiveDeviceUpdates() {
		runner.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				int activeDevices = deviceDetailsManager
						.numberOfActiveDevices();
				mainScreen.updateActiveDevices(activeDevices);
			}

		}, 0, 1000 * 60, TimeUnit.MILLISECONDS);
	}

	/**
	 * Cleans up resources just before the application closes.
	 */
	private void cleanupBeforeClose() {
		try {
			ConnectionPool.getConnectionPool().closeConnectionPool();
		} catch (PropertyVetoException e) {
			logger.log(Level.SEVERE, Controller.class.getName()
					+ " Problem closing the connection pool.");
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

	/**
	 * Updates the view based on information from the processor.
	 * 
	 * @param request
	 */
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

	/**
	 * Updates the device information on the UI.
	 */
	private void deviceDetailsUpdate() {
		long validResults = deviceDetailsManager.numberOfValidResults();
		long invalidResults = deviceDetailsManager.numberOfInvalidResults();
		long averageTime = deviceDetailsManager.getAverageProcessingTime();
		String time = String.format("%d mins %d secs",
				TimeUnit.MILLISECONDS.toSeconds(averageTime) / 60,
				TimeUnit.MILLISECONDS.toSeconds(averageTime) % 60);

		mainScreen.updateBlacklistedDevices(deviceDetailsManager
				.numberOfBlacklistedDevices());
		mainScreen.updatePacketStats(validResults, invalidResults);
		mainScreen
				.updateTotalNumDevices(deviceDetailsManager.numberOfDevices());
		mainScreen.updateAverageProcessingTime(time);
	}

	/**
	 * Updates results packet information on the UI.
	 */
	private void resultsPacketUpdate() {
		int resultsCompleted = resultsPacketManager
				.getNumberOfPacketsProcessed();
		int totalPackets = workPacketDrawer.numberOfDistinctWorkPackets();
		mainScreen.updateProgress(resultsCompleted, totalPackets);
	}

	/**
	 * Updates the view when processing is complete.
	 */
	private void updateViewProcessingComplete() {
		mainScreen.processingComplete();
	}

	/**
	 * Updates the processing times on the UI.
	 * 
	 * @param minutes
	 */
	private void updateProcessingTimes(String minutes) {
		mainScreen.addProcessingTime(Integer.parseInt(minutes));
	}

	/**
	 * The starting point of the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		this.primaryStage = arg0;
		Task<Void> backgroundLoadingTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				setupSystem();
				Thread.sleep(500);
				loadingComplete();
				Thread.sleep(200);
				return null;
			}

		};

		backgroundLoadingTask.stateProperty().addListener(
				new ChangeListener<Worker.State>() {

					@Override
					public void changed(
							ObservableValue<? extends State> observable,
							State oldState, State newState) {
						if (newState == Worker.State.SUCCEEDED) {
							showMainScreen();
							startActiveDeviceUpdates();
						}
					}

				});

		showLoadingScreen();
		new Thread(backgroundLoadingTask).start();
	}

}
