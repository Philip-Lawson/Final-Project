package uk.ac.qub.finalproject.calculationclasses;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.calculationclassstubs.GroupValidationStrategyStub;
import uk.ac.qub.finalproject.calculationclassstubs.ValidationStrategyStub;
import uk.ac.qub.finalproject.persistencestubs.DeviceDetailsManagerStub;
import uk.ac.qub.finalproject.persistencestubs.ResultsPacketManagerStub;

public class GroupResultsValidatorTest {

	GroupResultsValidator test;
	DeviceDetailsManagerStub deviceDetailsManager;
	ResultsPacketManagerStub resultsPacketManager;
	ValidationStrategyStub validationStrategy;
	GroupValidationStrategyStub groupValidationStrategy;
	IWorkPacket initialData;
	IResultsPacket resultsPacket;
	Map<String, Boolean> deviceStats;
	int invalidResultsNum, validResultsNum;

	@Before
	public void setUp() throws Exception {
		deviceDetailsManager = new DeviceDetailsManagerStub();
		resultsPacketManager = new ResultsPacketManagerStub();
		validationStrategy = new ValidationStrategyStub();
		groupValidationStrategy = new GroupValidationStrategyStub();
		test = new GroupResultsValidator(resultsPacketManager,
				deviceDetailsManager);
		test.setValidationStrategy(validationStrategy);
		test.setGroupValidationStrategy(groupValidationStrategy);
		initialData = new WorkPacket();
		resultsPacket = new ResultsPacket();
		resultsPacket.setPacketId(initialData.getPacketId());
		deviceStats = new HashMap<String, Boolean>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetInvalidResultsNeededForComparison() {
		invalidResultsNum = 4;
		test.setResultsNeededForComparison(invalidResultsNum);
		assertNotEquals(invalidResultsNum, test.getResultsNeededForComparison());
	}

	@Test
	public void testSetValidResultsNeededForComparison() {
		validResultsNum = 5;
		test.setResultsNeededForComparison(validResultsNum);
		assertEquals(validResultsNum, test.getResultsNeededForComparison());
	}

	@Test
	public void testResultIsInvalidNotSaved() {
		resultsPacketManager.setResultSaved(false);				
		assertFalse(test.resultIsValid(resultsPacket, initialData));
	}
	
	@Test
	public void testResultIsInvalidSaved() {
		resultsPacketManager.setResultSaved(true);		
		resultsPacketManager.setResultsPacket(resultsPacket);
		validationStrategy.setComparedResultIsValid(false);
		
		assertFalse(test.resultIsValid(resultsPacket, initialData));
	}
	
	@Test
	public void testResultIsValidSaved() {
		resultsPacketManager.setResultSaved(true);		
		resultsPacketManager.setResultsPacket(resultsPacket);
		validationStrategy.setComparedResultIsValid(true);
		
		assertTrue(test.resultIsValid(resultsPacket, initialData));
	}
	

	@Test
	public void testResultIsPending() {

		for (int count = 0; count < test.getResultsNeededForComparison() - 1; count++) {
			test.addResultToGroup(resultsPacket, initialData, count + "");
		}

		assertTrue(test.resultIsPending(resultsPacket.getPacketId()));
	}

	@Test
	public void testResultIsNotPending() {
		groupValidationStrategy.setBestResult(resultsPacket);
		groupValidationStrategy.setDeviceStats(deviceStats);

		for (int count = 0; count < test.getResultsNeededForComparison(); count++) {
			test.addResultToGroup(resultsPacket, initialData, count + "");
		}

		assertFalse(test.resultIsPending(resultsPacket.getPacketId()));
	}

	@Test
	public void testAddResultToGroupResultStilPending() {
		for (int count = 0; count < test.getResultsNeededForComparison() - 1; count++) {
			test.addResultToGroup(resultsPacket, initialData, "A" + count);
		}

		assertTrue(test.resultIsPending(initialData.getPacketId()));
	}

	@Test
	public void testAddResultToGroupResultShouldBeNoDuplicates() {
		for (int count = 0; count < test.getResultsNeededForComparison() - 1; count++) {
			test.addResultToGroup(resultsPacket, initialData, "A" + count);
			test.addResultToGroup(resultsPacket, initialData, "A" + count);
		}
				
		assertTrue(test.resultIsPending(initialData.getPacketId()));
	}
	
	@Test
	public void testAddResultToGroupResultResultGetsProcessed() {
		String deviceA = "device A";
		String deviceB = "device B";

		deviceStats.put(deviceA, true);
		deviceStats.put(deviceB, false);
		groupValidationStrategy.setDeviceStats(deviceStats);
		
		for (int count = 0; count < test.getResultsNeededForComparison(); count++) {
			test.addResultToGroup(resultsPacket, initialData, "A" + count);			
		}
				
		assertFalse(test.resultIsPending(initialData.getPacketId()));
	}
	
	@Test
	public void testProcessGroupResult() {
		Map<String, IResultsPacket> pendingResults = new HashMap<String, IResultsPacket>();
		String deviceA = "device A";
		String deviceB = "device B";

		deviceStats.put(deviceA, true);
		deviceStats.put(deviceB, false);
		groupValidationStrategy.setDeviceStats(deviceStats);

		test.processGroupResult(pendingResults, initialData);

		assertTrue(deviceDetailsManager.isValidResultSent());
		assertTrue(deviceDetailsManager.isInvalidResultSent());
	}

}
