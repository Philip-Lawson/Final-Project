package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.server.RegistrationPack;
import uk.ac.qub.finalproject.serverstubs.DeviceDetailsJDBCStub;
import uk.ac.qub.finalproject.serverstubs.ObservableStub;
import uk.ac.qub.finalproject.serverstubs.UserDetailsManagerStub;

public class DeviceDetailsManagerTest {

	DeviceDetailsManager test;
	UserDetailsManagerStub userDetails;
	DeviceDetailsJDBCStub dbStub;
	ObservableStub observer;

	@Before
	public void setUp() throws Exception {
		dbStub = new DeviceDetailsJDBCStub();
		userDetails = new UserDetailsManagerStub();
		observer = new ObservableStub();

		test = new DeviceDetailsManager(userDetails);
		test.setDeviceDB(dbStub);
		test.addObserver(observer);
	}

	@After
	public void tearDown() throws Exception {
		dbStub = null;
		userDetails = null;
		test = null;
		observer = null;
	}

	@Test
	public void testLoadAllNewDevices() {
		// add devices to the 'database'
		List<Device> devices = new ArrayList<Device>();
		int devicesNum = 20;

		for (int count = 0; count < devicesNum; count++) {
			Device device = new Device();
			device.setDeviceID(count + "");
			devices.add(device);
		}

		dbStub.setDeviceMap(devices);

		int preLoadNumDevices = test.numberOfDevices();
		test.loadDevices();
		int newDevicesAdded = test.numberOfDevices() - preLoadNumDevices;

		assertEquals(devicesNum, newDevicesAdded);
		assertTrue(observer.isObserved());

	}

	@Test
	public void testLoadSomeNewDevices() {
		// add devices to the 'database'
		List<Device> devices = new ArrayList<Device>();
		int devicesNum = 20;
		int newDevices = 10;

		// load devices
		for (int count = 0; count < devicesNum; count++) {
			Device device = new Device();
			device.setDeviceID(count + "");
			devices.add(device);
		}

		dbStub.setDeviceMap(devices);
		test.loadDevices();
		int currentDevicesLoaded = test.getDeviceMap().size();

		// ask the test to load new devices and duplicate devices
		for (int count = devicesNum; count < devicesNum + newDevices; count++) {
			Device device = new Device();
			device.setDeviceID(count + "");
			devices.add(device);
		}

		dbStub.setDeviceMap(devices);
		test.loadDevices();

		int newDevicesLoaded = test.getDeviceMap().size()
				- currentDevicesLoaded;

		// make sure no duplicates are stored
		assertEquals(newDevices, newDevicesLoaded);

	}

	@Test
	public void testLoadDevicesAddToBlacklist() {
		List<Device> devices = new ArrayList<Device>();
		int devicesNum = 20;
		int blacklistingThreshold = devicesNum - 10;
		int expectedBlacklistedDevices = devicesNum - blacklistingThreshold;

		for (int count = 1; count <= devicesNum; count++) {
			Device device = new Device();
			device.setDeviceID(count + "");
			device.setInvalidResults(count);
			devices.add(device);
		}

		dbStub.setDeviceMap(devices);

		test.setBlacklistingThreshold(blacklistingThreshold);
		test.loadDevices();

		assertEquals(expectedBlacklistedDevices, test.getBlacklistedList()
				.size());
		assertEquals(expectedBlacklistedDevices,
				test.numberOfBlacklistedDevices());
	}

	@Test
	public void testAddPreviouslyStoredDevice() {
		String deviceID = "Device ID";
		Device device = new Device();
		RegistrationPack registrationPack = new RegistrationPack();

		device.setDeviceID(deviceID);
		registrationPack.setAndroidID(deviceID);
		test.getDeviceMap().put(deviceID, device);

		assertFalse(test.addDevice(registrationPack));
		assertFalse(observer.isObserved());
	}

	@Test
	public void testAddNewDeviceNoEmail() {
		String deviceID = "Device ID";
		RegistrationPack registrationPack = new RegistrationPack();

		registrationPack.setAndroidID(deviceID);
		dbStub.setIsRegistered(false);

		assertTrue(test.addDevice(registrationPack));
		assertTrue(observer.isObserved());
		assertTrue(dbStub.getDeviceRegistered());
	}

	@Test
	public void testAddNewDeviceWithEmail() {
		String deviceID = "Device ID";
		RegistrationPack registrationPack = new RegistrationPack();

		registrationPack.setAndroidID(deviceID);
		registrationPack.setEmailAddress(deviceID);

		assertTrue(test.addDevice(registrationPack));
		assertTrue(userDetails.userIsRegistered());
	}

	@Test
	public void testWriteValidResultSentNoEmailAddress() {
		String deviceID = "Device ID";
		Device device = new Device();
		device.setDeviceID(deviceID);
		test.getDeviceMap().put(deviceID, device);

		long oldValidResults = test.numberOfValidResults();
		test.writeValidResultSent(deviceID);

		assertEquals(oldValidResults + 1, test.numberOfValidResults());
		assertTrue(observer.isObserved());
		assertTrue(dbStub.isValidResultWritten());
		assertEquals(1, test.getDeviceMap().get(deviceID).getValidResults());

	}

	@Test
	public void testWriteValidResultSentWithEmailAddress() {
		String deviceID = "Device ID";
		Device device = new Device();
		device.setDeviceID(deviceID);
		device.setEmailAddress(deviceID);

		test.getDeviceMap().put(deviceID, device);
		test.writeValidResultSent(deviceID);

		assertTrue(userDetails.isAchievementMilestoneChecked());
	}

	@Test
	public void testWriteInvalidResultSentNotBlacklisted() {
		String deviceID = "Device ID";
		Device device = new Device();
		device.setDeviceID(deviceID);
		test.getDeviceMap().put(deviceID, device);

		long oldInvalidResults = test.numberOfInvalidResults();
		test.writeInvalidResultSent(deviceID);

		assertEquals(oldInvalidResults + 1, test.numberOfInvalidResults());
		assertTrue(observer.isObserved());
		assertTrue(dbStub.isInvalidResultWritten());
		assertEquals(1, test.getDeviceMap().get(deviceID).getInvalidResults());
	}

	@Test
	public void testWriteInvalidResultSentBlacklisted() {
		String deviceID = "Device ID";
		Device device = new Device();
		device.setDeviceID(deviceID);
		test.getDeviceMap().put(deviceID, device);

		test.setBlacklistingThreshold(0);
		test.setMinPercentInvalidResults(0);
		test.writeInvalidResultSent(deviceID);

		assertEquals(1, test.getBlacklistedList().size());
		assertEquals(1, test.numberOfBlacklistedDevices());
		assertTrue(test.deviceIsBlacklisted(deviceID));
	}

	@Test
	public void testDeviceIsBlacklisted() {
		String deviceID = "Device ID";

		test.getBlacklistedList().add(deviceID);
		assertTrue(test.deviceIsBlacklisted(deviceID));
	}

	@Test
	public void testUpdateActiveDeviceDevicePresent() {
		String deviceID = "Device ID";
		long timeActive = 1000;
		Device device = new Device();
		device.setDeviceID(deviceID);

		test.getDeviceMap().put(deviceID, device);
		test.updateActiveDevice(deviceID, timeActive);

		assertEquals(timeActive, device.getLastTimeActive());
	}

	@Test
	public void testUpdateActiveDeviceDeviceNotPresent() {
		String deviceID = "Device ID";
		long timeActive = 1000;
		Device device = new Device();
		device.setDeviceID(deviceID);

		test.updateActiveDevice(deviceID, timeActive);

		assertNotEquals(timeActive, device.getLastTimeActive());
	}

	@Test
	public void testDeregisterDeviceGoodDatabase() {
		String deviceID = "Device ID";
		Device device = new Device();
		int expectedNumDevices = 0;

		device.setDeviceID(deviceID);
		test.getDeviceMap().put(deviceID, device);
		dbStub.setDeviceDeregistered(true);

		assertTrue(test.deregisterDevice(deviceID));
		assertEquals(expectedNumDevices, test.numberOfDevices());

	}

	@Test
	public void testDeregisterDeviceBadDatabase() {
		String deviceID = "Device ID";
		Device device = new Device();
		int expectedNumDevices = 0;

		device.setDeviceID(deviceID);
		test.getDeviceMap().put(deviceID, device);
		dbStub.setDeviceDeregistered(false);

		assertFalse(test.deregisterDevice(deviceID));
		assertEquals(expectedNumDevices, test.numberOfDevices());
	}

	@Test
	public void testNumberOfDevices() {
		int expectedNumDevices = 10;
		for (int count = 0; count < expectedNumDevices; count++) {
			RegistrationPack registrationPack = new RegistrationPack();
			registrationPack.setAndroidID(count + "");
			test.addDevice(registrationPack);
		}

		assertEquals(expectedNumDevices, test.numberOfDevices());
	}

	@Test
	public void testNumberOfValidResults() {
		int validResults = 10;
		String deviceID = "Device ID";
		Device device = new Device(deviceID);
		test.getDeviceMap().put(deviceID, device);

		for (int count = 0; count < validResults; count++) {
			test.writeValidResultSent(deviceID);
		}

		assertEquals(validResults, test.numberOfValidResults());
	}

	@Test
	public void testNumberOfInvalidResults() {
		int invalidResults = 10;
		String deviceID = "Device ID";
		Device device = new Device(deviceID);
		test.getDeviceMap().put(deviceID, device);

		for (int count = 0; count < invalidResults; count++) {
			test.writeInvalidResultSent(deviceID);
		}

		assertEquals(invalidResults, test.numberOfInvalidResults());
	}

	@Test
	public void testNumberOfBlacklistedDevices() {
		test.setBlacklistingThreshold(0);
		test.setMinPercentInvalidResults(0);

		int invalidDevices = 10;

		for (int count = 0; count < invalidDevices; count++) {
			String deviceID = count + "";
			Device device = new Device(deviceID);
			test.getDeviceMap().put(deviceID, device);
			test.writeInvalidResultSent(deviceID);
		}

		assertEquals(invalidDevices, test.numberOfBlacklistedDevices());

	}

	@Test
	public void testAdjustAverage() {

		// need to make sure that the rolling average will produce the same
		// number as a traditional average function
		int total = 0;
		int timesToAdd = 5;

		for (int count = 0; count < timesToAdd; count++) {
			test.adjustAverage(1, 2);
			total += 2;
		}

		total += 2;
		total += 9;

		int expectedAverage = total / (timesToAdd + 2);

		// add 2 and 9 to the rolling average
		test.adjustAverage(2, 11);
		long actualAverage = test.getAverageProcessingTime();

		assertEquals(expectedAverage, actualAverage);
	}

	@Test
	public void testGetAverageProcessingTime() {
		assertEquals(0, test.getAverageProcessingTime());

		test.adjustAverage(2, 2);
		assertEquals(1, test.getAverageProcessingTime());
	}

	@Test
	public void testNumberOfActiveDevices() {
		int activeDeviceThreshold = 10;
		int activeDevices = 12;
		long time = new Date().getTime();

		for (int count = 0; count < activeDevices; count++) {
			Device device = new Device(count + "");

			if (count % 2 == 0) {
				device.setLastTimeActive(time);
			} else {
				device.setLastTimeActive(0);
			}

			test.getDeviceMap().put(count + "", device);
		}

		test.changeActiveDeviceThreshold(activeDeviceThreshold);

		assertEquals(activeDevices / 2, test.numberOfActiveDevices());
	}

	@Test
	public void testChangeActiveDeviceThreshold() {
		int activeDeviceThreshold = 10;
		long time = new Date().getTime();

		for (int count = 0; count < activeDeviceThreshold; count++) {
			Device device = new Device(count + "");
			device.setLastTimeActive(time);
			test.getDeviceMap().put(count + "", device);
		}

		int activeDevices = test
				.changeActiveDeviceThreshold(activeDeviceThreshold);

		assertEquals(activeDeviceThreshold, activeDevices);
	}

	@Test
	public void testSetBlacklistingThreshold() {
		test.setBlacklistingThreshold(10);
		assertTrue(observer.isObserved());
	}

	@Test
	public void testSetMinPercentInvalidResultsValidValue() {
		// checks that the percentage is changed and, more importantly, that the
		// blacklist is recalculated
		int numDevices = 10;
		int allBlacklistPercent = 40;
		int halfBlacklistPercent = 50;
		
		for (int count = 1; count <= numDevices; count++) {
			Device device = new Device(count + "");
			device.setInvalidResults(count);
			if (count % 2 == 0) {
				device.setValidResults(count);
			}

			test.getDeviceMap().put(count + "", device);
		}

		test.setBlacklistingThreshold(0);
		test.setMinPercentInvalidResults(allBlacklistPercent);
		observer.setObserved(false);

		// make sure that blacklist is full before the reset
		assertEquals(numDevices, test.numberOfBlacklistedDevices());

		test.setMinPercentInvalidResults(halfBlacklistPercent);

		assertTrue(observer.isObserved());
		assertEquals(test.numberOfBlacklistedDevices(), 5);
	}

	@Test
	public void testSetMinPercentInvalidResultsInvalidMinusValues() {
		test.setMinPercentInvalidResults(-100);
		assertFalse(observer.isObserved());
		test.setMinPercentInvalidResults(0);
		assertFalse(observer.isObserved());
		test.setMinPercentInvalidResults(101);
		assertFalse(observer.isObserved());
	}

}
