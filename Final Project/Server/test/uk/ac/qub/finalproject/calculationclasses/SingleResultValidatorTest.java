package uk.ac.qub.finalproject.calculationclasses;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.calculationclassstubs.ValidationStrategyStub;
import uk.ac.qub.finalproject.persistencestubs.ResultsPacketManagerStub;

public class SingleResultValidatorTest {

	SingleResultsValidator test;
	ValidationStrategyStub validationStrategy;
	ResultsPacketManagerStub resultsPacketManager;
	IWorkPacket initialData;
	IResultsPacket resultsPacket;
	
	
	@Before
	public void setUp() throws Exception {
		validationStrategy = new ValidationStrategyStub();
		resultsPacketManager= new ResultsPacketManagerStub();
		test = new SingleResultsValidator();
		test.setValidationStrategy(validationStrategy);
		test.setResultsPacketManager(resultsPacketManager);
		
		initialData = new WorkPacket();
		resultsPacket = new ResultsPacket();
		resultsPacket.setPacketId(initialData.getPacketId());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testResultIsPending() {
		assertFalse(test.resultIsPending(""));
	}

	@Test
	public void testResultIsValidResultSaved() {
		validationStrategy.setComparedResultIsValid(true);
		validationStrategy.setNewResultIsValid(false);
		resultsPacketManager.setResultsPacket(resultsPacket);
		resultsPacketManager.setResultSaved(true);
		
		assertTrue(test.resultIsValid(resultsPacket, initialData));
	}
	
	@Test
	public void testResultIsValidResultNotSaved() {
		validationStrategy.setComparedResultIsValid(false);
		validationStrategy.setNewResultIsValid(true);
		resultsPacketManager.setResultsPacket(resultsPacket);
		resultsPacketManager.setResultSaved(false);
		
		assertTrue(test.resultIsValid(resultsPacket, initialData));
	}
	
	@Test
	public void testResultIsInvalidResultSaved() {
		validationStrategy.setComparedResultIsValid(false);
		validationStrategy.setNewResultIsValid(false);
		resultsPacketManager.setResultsPacket(resultsPacket);
		resultsPacketManager.setResultSaved(true);
		
		assertFalse(test.resultIsValid(resultsPacket, initialData));
	}
	
	@Test
	public void testResultIsInvalidResultNotSaved() {
		validationStrategy.setComparedResultIsValid(false);
		validationStrategy.setNewResultIsValid(false);
		resultsPacketManager.setResultsPacket(resultsPacket);
		resultsPacketManager.setResultSaved(false);
		
		assertFalse(test.resultIsValid(resultsPacket, initialData));
	}
}
