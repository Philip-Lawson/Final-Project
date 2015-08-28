package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;
import uk.ac.qub.finalproject.calculationclasses.WorkPacket;
import uk.ac.qub.finalproject.persistencestubs.WorkPacketJDBCStub;
import uk.ac.qub.finalproject.serverstubs.ObservableStub;

public class WorkPacketDrawerImplTest {

	private WorkPacketDrawerImpl test;
	private WorkPacketJDBCStub dbStub;
	private ObservableStub observer;

	@Before
	public void setUp() throws Exception {
		dbStub = new WorkPacketJDBCStub();
		test = new WorkPacketDrawerImpl(dbStub);
		observer = new ObservableStub();
		test.addObserver(observer);
	}

	@After
	public void tearDown() throws Exception {
		dbStub = null;
		test = null;
		observer = null;
	}

	@Test
	public void testAddValidWorkPacketsNoDuplicatesCurrentPacketListEmpty() {
		int numPacketsToAdd = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}

		test.setPacketsPerList(numPacketsToAdd);
		test.addWorkPackets(workPackets);

		assertTrue(observer.isObserved());
		assertTrue(dbStub.isWorkPacketsAdded());
		assertEquals(numPacketsToAdd, test.getCurrentWorkPacketList().size());

	}

	@Test
	public void testAddValidWorkPacketsWithDuplicatesCurrentPacketListEmpty() {
		int numPacketsToAdd = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
			workPackets.add(packet);
		}

		test.setPacketsPerList(numPacketsToAdd);
		test.addWorkPackets(workPackets);
		assertEquals(numPacketsToAdd, test.getCurrentWorkPacketList().size());
	}

	@Test
	public void testAddAllInvalidWorkPacketsCurrentPacketListEmpty() {
		int numPacketsToAdd = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket();
			packet.setPacketId(null);
			workPackets.add(packet);
		}

		test.addWorkPackets(workPackets);
		assertFalse(observer.isObserved());
		assertFalse(dbStub.isWorkPacketsAdded());
		assertEquals(0, test.getCurrentWorkPacketList().size());
	}

	@Test
	public void testAddSomeInvalidWorkPacketsCurrentPacketListEmpty() {
		int numPacketsToAdd = 20;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			if (count % 2 == 0) {
				packet.setPacketId(null);
			}

			workPackets.add(packet);
		}

		test.setPacketsPerList(numPacketsToAdd / 2);
		test.addWorkPackets(workPackets);
		assertTrue(observer.isObserved());
		assertTrue(dbStub.isWorkPacketsAdded());
		assertEquals(numPacketsToAdd / 2, test.getCurrentWorkPacketList()
				.size());
	}

	@Test
	public void addPacketsCurrentPacketListNotEmpty() {
		test.getCurrentWorkPacketList().add(new WorkPacket());

		int numPacketsToAdd = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}

		test.addWorkPackets(workPackets);
		assertTrue(observer.isObserved());
		assertTrue(dbStub.isWorkPacketsAdded());
		assertEquals(numPacketsToAdd, test.getUnprocessedWorkPacketMap().size());

	}

	@Test
	public void testReloadIncompletedWorkPacketsCurrentPacketListNotEmpty() {
		int numPacketsToAdd = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);
		test.getCurrentWorkPacketList().add(new WorkPacket());

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}

		dbStub.setWorkPackets(workPackets);
		test.reloadIncompletedWorkPackets();

		assertTrue(dbStub.isIncompleteWorkPacketsReloaded());
		assertEquals(numPacketsToAdd, test.getUnprocessedWorkPacketMap().size());

	}

	@Test
	public void testGetNextWorkPacketWorkPacketsAvailable() {
		int numPacketsToAdd = 5;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}

		test.setPacketsPerList(numPacketsToAdd);
		test.addWorkPackets(workPackets);

		assertEquals(numPacketsToAdd, test.getNextWorkPacket().size());
	}

	@Test
	public void testGetAllWorkPacketsAvailableToNoneLeft() {
		int numPacketsToAdd = 5;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}

		test.setPacketsPerList(numPacketsToAdd);
		test.setNumberOfCopies(numPacketsToAdd);
		test.addWorkPackets(workPackets);

		for (int count = 0; count < numPacketsToAdd - 1; count++) {
			test.getNextWorkPacket();
		}

		// check that there's one proper copy of the list left
		assertTrue(test.hasWorkPackets());
		assertTrue(test.getNextWorkPacket().size() > 0);

		// check that there's now nothing left - transfer work packets has been
		// called at the proper time with no packets remaining in the map
		assertFalse(test.hasWorkPackets());
	}

	@Test
	public void testSetNumberOfCopiesValid() {
		test.setNumberOfCopies(5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNumberOfCopiesInvalidUpper() {
		int invalidNumber = 101;
		test.setNumberOfCopies(invalidNumber);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNumberOfCopiesInvalidLower() {
		int invalidNumber = 4;
		test.setNumberOfCopies(invalidNumber);
	}

	@Test
	public void testTransferPacketsInsufficientPackets() {
		int numPacketsToAdd = 5;

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");

			// avoid side effects of using the addWorkPackets method
			test.getUnprocessedWorkPacketMap()
					.put(packet.getPacketId(), packet);
		}

		test.setPacketsPerList(numPacketsToAdd + 1);
		test.transferWorkPackets();

		assertEquals(numPacketsToAdd, test.getCurrentWorkPacketList().size());
		assertEquals(numPacketsToAdd, test.getSentPacketsMap().size());
	}

	@Test
	public void testTransferPacketsSufficientPackets() {
		int numPacketsToAdd = 7;
		int numPacketsToTransfer = numPacketsToAdd - 2;

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");

			// avoid side effects of using the addWorkPackets method
			test.getUnprocessedWorkPacketMap()
					.put(packet.getPacketId(), packet);
		}

		test.setPacketsPerList(numPacketsToTransfer);
		test.transferWorkPackets();

		assertEquals(numPacketsToTransfer, test.getCurrentWorkPacketList()
				.size());
		assertEquals(numPacketsToAdd - numPacketsToTransfer, test
				.getUnprocessedWorkPacketMap().size());
		assertEquals(numPacketsToTransfer, test.getSentPacketsMap().size());		
	}

	@Test
	public void testSetPacketsPerListValidNumber() {
		int numPacketsToAdd = 25;
		int packetsPerList = 10;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.setPacketsPerList(packetsPerList);
		test.addWorkPackets(workPackets);
		
		assertEquals(numPacketsToAdd-packetsPerList, test.getUnprocessedWorkPacketMap().size());
	}
	
	@Test
	public void testSetPacketsPerListInvalidNumber() {
		int numPacketsToAdd = 25;
		int packetsPerList = 2;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.setPacketsPerList(packetsPerList);
		test.addWorkPackets(workPackets);
		
		assertNotEquals(numPacketsToAdd-packetsPerList, test.getUnprocessedWorkPacketMap().size());
	}

	@Test
	public void testNumberOfPacketsRemainingNoneSent() {
		int numPacketsToAdd = 25;
		int numberOfCopies = 5;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.setNumberOfCopies(numberOfCopies);;
		test.addWorkPackets(workPackets);
		
		assertEquals(numPacketsToAdd*numberOfCopies, test.numberOfPacketsRemaining());
	}
	
	@Test
	public void testNumberOfPacketsRemainingSomeSent() {
		int numPacketsToAdd = 25;
		int packetsPerList = 6;
		int listsToSend = 3;
		int expectedPacketsRemaining = (numPacketsToAdd*packetsPerList) - listsToSend*packetsPerList;
		
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.setPacketsPerList(packetsPerList);		
		test.setNumberOfCopies(packetsPerList);
		test.addWorkPackets(workPackets);
		
		for(int count=0; count<listsToSend; count++){
			test.getNextWorkPacket();
		}
		
		assertEquals(expectedPacketsRemaining, test.numberOfPacketsRemaining());
	}

	@Test
	public void testNumberOfDistinctWorkPackets() {
		int numPacketsToAdd = 25;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.addWorkPackets(workPackets);
		assertEquals(numPacketsToAdd, test.numberOfDistinctWorkPackets());		
	}

	@Test
	public void testHasNoWorkPackets() {
		assertFalse(test.hasWorkPackets());
	}

	@Test
	public void testHasWorkPackets() {
		int numPacketsToAdd = 5;
		Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(
				numPacketsToAdd);

		for (int count = 0; count < numPacketsToAdd; count++) {
			IWorkPacket packet = new WorkPacket(count + "", count + "");
			workPackets.add(packet);
		}
		
		test.addWorkPackets(workPackets);
		assertTrue(test.hasWorkPackets());
	}	

	@Test
	public void testGetInitialProcessedData() {
		String packetID = "Packet ID";
		IWorkPacket workPacket = new WorkPacket(packetID, packetID);
		test.getUnprocessedWorkPacketMap().put(packetID, workPacket);
		
		assertEquals(workPacket, test.getInitialData(packetID));		
	}
	
	@Test
	public void testGetInitialUnprocessedData() {
		String packetID = "Packet ID";
		IWorkPacket workPacket = new WorkPacket(packetID, packetID);
		test.getSentPacketsMap().put(packetID, workPacket);
		
		assertEquals(workPacket, test.getInitialData(packetID));		
	}
	
	

}
