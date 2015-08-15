package uk.ac.qub.finalproject.calculationclasses;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.persistencestubs.DeviceDetailsManagerStub;
import uk.ac.qub.finalproject.persistencestubs.ResultsPacketManagerStub;
import uk.ac.qub.finalproject.persistencestubs.WorkPacketDrawerStub;
import uk.ac.qub.finalproject.serverstubs.ObservableStub;
import uk.ac.qub.finalproject.serverstubs.ValidatorStub;

public class ResultProcessorTest {

	ResultProcessor test;
	ValidatorStub validator;
	WorkPacketDrawerStub workPacketDrawer;
	ResultsPacketManagerStub resultsPacketManager;
	DeviceDetailsManagerStub deviceDetailsManager;
	ObservableStub observer;
	ResultsPacketList resultList;
	IResultsPacket resultPacket;
	IWorkPacket initialData;
	Object[] testObjects = { test, validator, workPacketDrawer,
			resultsPacketManager, deviceDetailsManager, observer, resultList,
			resultPacket, initialData };

	String deviceID;
	String resultsPacketID;

	@Before
	public void setUp() throws Exception {
		validator = new ValidatorStub();
		workPacketDrawer = new WorkPacketDrawerStub();
		resultsPacketManager = new ResultsPacketManagerStub();
		deviceDetailsManager = new DeviceDetailsManagerStub();
		test = new ResultProcessor(deviceDetailsManager, resultsPacketManager,
				validator, workPacketDrawer);
		observer = new ObservableStub();
		test.addObserver(observer);

		deviceID = "Device ID";
		resultsPacketID = "Results Packet ID";
		resultPacket = new ResultsPacket();
		resultPacket.setPacketId(resultsPacketID);
		initialData = new WorkPacket(resultsPacketID, resultsPacketID);
		workPacketDrawer.setInitialData(initialData);

		resultList = new ResultsPacketList();
		resultList.add(resultPacket);
	}

	@After
	public void tearDown() throws Exception {
		for (int count = 0; count < testObjects.length; count++) {
			testObjects[count] = null;
		}
	}

	@Test
	public void testProcessResults() {
		// just checking that the call to process results moves through the
		// three expected methods
		validator.setResultIsValid(true);
		workPacketDrawer.setHasWorkPackets(false);
		resultsPacketManager.setAllResultsComplete(true);
		resultList.setTimeStamp(new Date().getTime());
		
		test.processResults(resultList, deviceID);
		
		assertEquals(resultPacket, resultsPacketManager.getResultsPacket());
		assertTrue(resultsPacketManager.isResultWritten());
		assertTrue(deviceDetailsManager.isValidResultSent());		
		assertEquals(ResultProcessor.PROCESSING_COMPLETE, (String) observer.getSentbject());
		assertEquals(2, observer.getTimesNotified());
	}

	@Test
	public void testProcessIndividualPacketsResultPending() {
		validator.setResultPending(true);
		test.processIndividualPackets(resultList, deviceID);

		assertTrue(validator.isResultAdded());
		assertEquals(resultPacket, validator.getResultsPacket());
		assertEquals(initialData, validator.getInitialData());
		assertEquals(deviceID, validator.getDeviceID());
	}

	@Test
	public void testProcessIndividualPacketsResultValid() {
		validator.setResultIsValid(true);

		test.processIndividualPackets(resultList, deviceID);

		assertTrue(deviceDetailsManager.isValidResultSent());
		assertTrue(resultsPacketManager.isResultWritten());

		assertEquals(resultPacket, resultsPacketManager.getResultsPacket());
		assertEquals(deviceID, deviceDetailsManager.getDeviceID());
	}

	@Test
	public void testProcessIndividualPacketsResultInvalid() {
		validator.setResultIsValid(false);
		resultsPacketManager.setResultWritten(false);

		test.processIndividualPackets(resultList, deviceID);

		assertTrue(deviceDetailsManager.isInvalidResultSent());
		assertFalse(resultsPacketManager.isResultWritten());

		assertNull(resultsPacketManager.getResultsPacket());
		assertEquals(deviceID, deviceDetailsManager.getDeviceID());
	}

	@Test
	public void testProcessTimeStamp() {
		// need to make sure there is less than one minutes worth of processing
		// time
		test.processTimeStamp(new Date().getTime(), 100);

		String expected = "0";
		String actual = (String) observer.getSentbject();

		assertEquals(expected, actual);
		assertTrue(deviceDetailsManager.isAverageAdjusted());
	}

	@Test
	public void testCheckProcessingComplete() {
		workPacketDrawer.setHasWorkPackets(false);
		resultsPacketManager.setAllResultsComplete(true);

		test.checkProcessingComplete();
		String actual = (String) observer.getSentbject();

		assertTrue(observer.isObserved());
		assertEquals(ResultProcessor.PROCESSING_COMPLETE, actual);
	}

	@Test
	public void testCheckProcessingCompleteMorePacketsNeeded() {
		workPacketDrawer.setHasWorkPackets(false);
		resultsPacketManager.setAllResultsComplete(false);

		test.checkProcessingComplete();
		String actual = (String) observer.getSentbject();

		assertTrue(observer.isObserved());
		assertEquals(ResultProcessor.LOAD_MORE_WORK_PACKETS, actual);
	}

	@Test
	public void testProcessingNotComplete() {
		workPacketDrawer.setHasWorkPackets(true);

		test.checkProcessingComplete();
		assertFalse(observer.isObserved());
	}

	@Test
	public void testGetMinutesProcessing() {
		int minutes = 9;
		long actualMinutes = TimeUnit.MINUTES.toMillis(minutes);
		int packetCount = 3;

		String expectedTime = minutes / packetCount + "";

		assertEquals(expectedTime,
				test.getMinutesProcessing(packetCount, actualMinutes));
	}

}
