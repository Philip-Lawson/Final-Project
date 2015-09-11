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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import uk.ac.qub.finalproject.server.views.LoadingScreenView;
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

	private Stage primaryStage;
	private Stage mainStage;	
	private LoadingScreenView loadingScreen;
	private MainScreenView mainScreen;

	public void setupSystem() {
		setupPersistence();
		setupValidation();
		setupServer();
		setupListeners();
		setupCommands();
	}

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

		/*
		 * loadingScreen.updateProgress("Loading Work Packets", 10);
		 * workPacketLoader.loadWorkPackets();		 
		 * loadingScreen.updateProgress("Loading Incomplete Work Packets", 30);
		 * workPacketDrawer.reloadIncompletedWorkPackets();	
		 * loadingScreen.updateProgress("Loading Devices", 50);	 
		 * deviceDetailsManager.loadDevices();
		 * deviceVersionManager.loadDeviceVersions();
		 * loadingScreen.updateProgress("Loading Results Packets", 70);
		 * resultsPacketManager.loadResultsPackets();
		 */

		workPacketDrawer.addObserver(this);
		deviceDetailsManager.addObserver(this);
		resultsPacketManager.addObserver(this);
	}

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
	
	private void loadingComplete(){
		loadingScreen.updateProgress("Complete", 100);
	}
	
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

			primaryStage.show();
		} catch (IOException e) {

		}

	}

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

			mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					cleanupBeforeClose();
				}

			});

			if (primaryStage.isShowing()){
				primaryStage.close();
			}
			
			mainStage.show();
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
		Task<Void> backgroundLoading = new Task<Void>(){

			@Override
			protected Void call() throws Exception {
				setupSystem();
				Thread.sleep(500);
				loadingComplete();	
				Thread.sleep(200);
				return null;
			}
			
		};
		
		backgroundLoading.stateProperty().addListener(new ChangeListener<Worker.State>(){

			@Override
			public void changed(ObservableValue<? extends State> observable,
					State oldState, State newState) {
				if (newState == Worker.State.SUCCEEDED){
					showMainScreen();
					// startActiveDeviceUpdates();
				}				
			}
			
		});		
		
		showLoadingScreen();	
		new Thread(backgroundLoading).start();
	}
	
}
