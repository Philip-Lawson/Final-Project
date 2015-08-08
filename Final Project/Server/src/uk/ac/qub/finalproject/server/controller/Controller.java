/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;
import uk.ac.qub.finalproject.persistence.DatabaseCreator;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.DummyResultsTransferManager;
import uk.ac.qub.finalproject.persistence.DummyWorkPacketLoader;
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
import uk.ac.qub.finalproject.server.calculationclasses.DummyGroupValidaitonStrategy;
import uk.ac.qub.finalproject.server.calculationclasses.DummyValidationStrategy;
import uk.ac.qub.finalproject.server.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.server.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.server.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.server.calculationclasses.ResultsValidator;
import uk.ac.qub.finalproject.server.views.MainScreenView;

/**
 * @author Phil
 *
 */
public class Controller implements Observer {

	private AbstractWorkPacketDrawer workPacketDrawer;
	private AbstractWorkPacketLoader workPacketLoader;
	private AbstractResultsTransferManager resultsTransferManager;
	private DatabaseCreator databaseCreator;
	private DeviceDetailsManager deviceDetailsManager;
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

	private ActiveDeviceThresholdChangeListener deviceThresholdChangeListener;
	private BlacklistChangeListener blacklistChangeListener;
	private DuplicatesNumChangeListener duplicatesNumChangeListener;
	private PacketsNumChangeListener packetsNumChangeListener;
	private LoadAdditionalWorkPacketsCommand loadWorkPacketsCommand;
	private StartServerCommand startServerCommand;
	private StopSendingPacketsCommand stopSendingPacketsCommand;
	private TransferResultsCommand transferResultsCommand;

	private TimerTask updateActiveDevicesTask;
	private MainScreenView mainScreen;

	public void setupPersistence() {
		databaseCreator = new DatabaseCreator();
		databaseCreator.setupDatabase();

		workPacketDrawer = new WorkPacketDrawerImpl();
		workPacketLoader = new DummyWorkPacketLoader(workPacketDrawer);
		resultsTransferManager = new DummyResultsTransferManager();

		deviceDetailsManager = new DeviceDetailsManager();
		resultsPacketManager = new ResultsPacketManager();
		userDetailsManager = new UserDetailsManager();

		deviceDetailsManager.setUserDetailsManager(userDetailsManager);
		userDetailsManager.setDeviceManager(deviceDetailsManager);

		deviceDetailsManager.addObserver(this);
		resultsPacketManager.addObserver(this);
		userDetailsManager.addObserver(this);
	}

	public void setupValidation() {
		resultValidator = new ResultsValidator(resultsPacketManager);
		validationStrategy = new DummyValidationStrategy();
		groupValidationStrategy = new DummyGroupValidaitonStrategy();

		resultValidator.setValidationStrategy(validationStrategy);
		resultValidator.setGroupValidationStrategy(groupValidationStrategy);
	}

	public void setupServer() {
		calculationFinishedRequestHandler = new CalculationFinishedRequestHandler(
				deviceDetailsManager, userDetailsManager);
		catchAllRequestHandler = new CatchAllRequestHandler();
		changeEmailRequestHandler = new ChangeEmailRequestHandler(
				userDetailsManager);
		deleteAccountRequestHandler = new DeleteAccountRequestHandler(
				deviceDetailsManager);
		processResultRequestHandler = new ProcessResultRequestHandler(
				deviceDetailsManager, resultsPacketManager, resultValidator,
				workPacketDrawer);
		registerRequestHandler = new RegisterRequestHandler(
				deviceDetailsManager);
		workPacketRequestHandler = new WorkPacketRequestHandler(
				workPacketDrawer);

		// the results processor will update the controller when there are no
		// packets left or all results are processed
		processResultRequestHandler.addObserver(this);

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

	public void setupView() {
		mainScreen = new MainScreenView();

		mainScreen
				.setActiveDeviceThresholdChangeListener(deviceThresholdChangeListener);
		mainScreen.setBlacklistChangeListener(blacklistChangeListener);
		mainScreen.setDuplicatesNumChangeListener(duplicatesNumChangeListener);
		mainScreen.setPacketsNumChangeListener(packetsNumChangeListener);

		mainScreen.setLoadAdditionalWorkPacketsCommand(loadWorkPacketsCommand);
		mainScreen.setStartServerCommand(startServerCommand);
		mainScreen.setStopSendingPacketsCommand(stopSendingPacketsCommand);
		mainScreen.setTransferResultsCommand(transferResultsCommand);

	}

	public void startTimerTask() {
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

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0.getClass().equals(ProcessResultRequestHandler.class)) {
			String request = (String) arg1;
			processorUpdate(request);
		} else if (arg0.getClass().equals(DeviceDetailsManager.class)) {
			deviceDetailsUpdate();
		} else if (arg0.getClass().equals(ResultsPacketManager.class)) {
			resultsPacketUpdate();
		} else if (arg0.getClass().equals(UserDetailsManager.class)) {
			userDetailsUpdate();
		}

	}

	private void processorUpdate(String request) {
		if (request.equals(ProcessResultRequestHandler.LOAD_MORE_WORK_PACKETS)) {
			workPacketDrawer.reloadIncompletedWorkPackets();
		} else if (request
				.equals(ProcessResultRequestHandler.PROCESSING_COMPLETE)) {
			stopSendingPacketsCommand.execute();
			updateViewProcessingComplete();
		}
	}

	private void deviceDetailsUpdate() {

	}

	private void resultsPacketUpdate() {

	}

	private void userDetailsUpdate() {

	}
	
	private void updateViewProcessingComplete(){
		
	}

}
