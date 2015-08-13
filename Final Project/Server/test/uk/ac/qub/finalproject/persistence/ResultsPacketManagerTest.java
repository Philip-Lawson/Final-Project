package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.calculationclasses.ResultsPacket;
import uk.ac.qub.finalproject.serverstubs.ObservableStub;
import uk.ac.qub.finalproject.serverstubs.ResultsPacketJDBCStub;

public class ResultsPacketManagerTest {

	ResultsPacketManager test;
	ResultsPacketJDBCStub dbStub;

	@Before
	public void setUp() throws Exception {
		dbStub = new ResultsPacketJDBCStub();
		test = new ResultsPacketManager(dbStub);
	}

	@After
	public void tearDown() throws Exception {
		dbStub = null;
		test = null;
	}

	@Test
	public void testWriteResultUnsaved() {
		ResultsPacket resultsPacket = new ResultsPacket();
		ObservableStub observer = new ObservableStub();
		String packetID = "5";

		resultsPacket.setPacketId(packetID);
		resultsPacket.setResult("Result");
		observer.setObserved(false);
		test.addObserver(observer);

		test.writeResult(resultsPacket);

		assertTrue(observer.isObserved());
		assertTrue(dbStub.resultWritten());
		assertTrue(test.getResultsMap().containsKey(packetID));
		assertTrue(resultsPacket.equals(test.getResultsMap().get(packetID)));
	}

	@Test
	public void testWriteResultSaved() {
		ResultsPacket resultsPacket = new ResultsPacket();
		ObservableStub observer = new ObservableStub();
		String packetID = "5";

		resultsPacket.setPacketId(packetID);
		resultsPacket.setResult("Result");
		observer.setObserved(false);
		test.addObserver(observer);

		test.getResultsMap().put(packetID, resultsPacket);
		int packetsProcessedBeforeAdd = test.getNumberOfPacketsProcessed();
		test.writeResult(resultsPacket);
		int packetsProcessedAfterAdd = test.getNumberOfPacketsProcessed();

		assertFalse(observer.isObserved());
		assertEquals(packetsProcessedBeforeAdd, packetsProcessedAfterAdd);
		assertFalse(dbStub.resultWritten());
	}

	@Test
	public void testResultNotOverWritten() {
		ResultsPacket originalResultsPacket = new ResultsPacket();
		ResultsPacket newResultsPacket = new ResultsPacket();
		String packetID = "5";

		originalResultsPacket.setPacketId(packetID);
		originalResultsPacket.setResult("Result");
		newResultsPacket.setPacketId(packetID);
		newResultsPacket.setResult("Result");

		test.writeResult(originalResultsPacket);
		test.writeResult(newResultsPacket);

		assertTrue(originalResultsPacket.equals(test
				.getResultForComparison(packetID)));
		assertFalse(newResultsPacket.equals(test
				.getResultForComparison(packetID)));
	}

	@Test
	public void testResultIsSaved() {
		ResultsPacket resultsPacket = new ResultsPacket();
		String packetID = "5";

		resultsPacket.setPacketId(packetID);
		resultsPacket.setResult("Result");
		test.getResultsMap().put(packetID, resultsPacket);

		assertTrue(test.resultIsSaved(packetID));
	}

	@Test
	public void testResultIsNotSaved() {
		ResultsPacket resultsPacket = new ResultsPacket();
		String packetID = "5";

		resultsPacket.setPacketId(packetID);
		resultsPacket.setResult("Result");

		assertFalse(test.resultIsSaved(packetID));
	}

	@Test
	public void testGetAvailableResultForComparison() {
		ResultsPacket resultsPacket = new ResultsPacket();
		String packetID = "5";

		resultsPacket.setPacketId(packetID);
		resultsPacket.setResult("Result");
		test.getResultsMap().put(packetID, resultsPacket);

		assertTrue(resultsPacket.equals(test.getResultForComparison(packetID)));
	}

	@Test
	public void testGetUnavailableResultForComparison() {
		String packetID = "5";
		assertNull(test.getResultForComparison(packetID));
	}

	@Test
	public void testGetNumberOfPacketsProcessedDistinctPackets() {
		int numPackets = 10;

		for (int count = 0; count < numPackets; count++) {
			ResultsPacket resultsPacket = new ResultsPacket();
			resultsPacket.setPacketId(count + "");
			resultsPacket.setResult(count + 1 + "");

			test.writeResult(resultsPacket);
		}

		assertEquals(numPackets, test.getNumberOfPacketsProcessed());
	}

	@Test
	public void testGetNumberOfPacketsProcessedIndistinctPackets() {
		int numPackets = 10;

		for (int count = 0; count < numPackets; count++) {
			ResultsPacket resultsPacket = new ResultsPacket();
			resultsPacket.setPacketId(this.toString());
			resultsPacket.setResult(count + 1 + "");

			test.writeResult(resultsPacket);
		}

		assertEquals(1, test.getNumberOfPacketsProcessed());
	}

	@Test
	public void testLoadResultsPackets() {
		int originalSize = test.getResultsMap().size();
		int newResults = 10;
		
		dbStub.addResults(newResults);
		test.loadResultsPackets();
		
		assertEquals(originalSize + newResults, test.getResultsMap().size());
	}
}
