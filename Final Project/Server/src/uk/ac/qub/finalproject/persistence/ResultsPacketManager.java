/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket;

/**
 * The results packet manager encapsulates the logic for storing processed
 * results packets in memory and in the database. The caller can <br>
 * (a) Ask the manager to write a result to the system <br>
 * (b) Find out if a result has been processed and stored previously <br>
 * (c) Ask the manager to retrieve a previously calculated result for comparison
 * with a duplicate result. N.B. This should only be called after calling
 * resultIsSaved(). <br>
 * (d) Find out how many work packets have been processed and stored.
 * 
 * 
 * @author Phil
 *
 */
public class ResultsPacketManager extends Observable {

	/**
	 * The utility class for sending results to the database.
	 */
	private ResultsPacketJDBC resultsDB = new ResultsPacketJDBC();

	/**
	 * Used to store results packets using their work packet ID as a key.
	 */
	private ConcurrentMap<String, IResultsPacket> resultsCache = new ConcurrentHashMap<String, IResultsPacket>();

	public ResultsPacketManager() {
		
	}
	
	/**
	 * Takes a results DAO as an argument.
	 * 
	 * @param resultsDB
	 */
	public ResultsPacketManager(ResultsPacketJDBC resultsDB) {
		this.resultsDB = resultsDB;
	}

	/**
	 * Writes the result packet to memory and to the database. If the referenced
	 * results packet is already saved it will not be added to the cache or the
	 * database.
	 * 
	 * @param resultsPacket
	 */
	public void writeResult(IResultsPacket resultsPacket) {
		String packetID = resultsPacket.getPacketId();
		if (resultIsSaved(packetID)) {
			// discard the result
			resultsPacket = null;
		} else {
			resultsCache.put(packetID, resultsPacket);
			resultsDB.writeResult(resultsPacket);
			setChanged();
			notifyObservers();
		}

	}

	/**
	 * Determines whether a result is saved in the cache. This should be called
	 * before trying to retrieve a result to avoid receiving a null object.
	 * 
	 * @param packetID
	 *            the unique ID of the results packet.
	 * @return true if the result is cached.
	 */
	public boolean resultIsSaved(String packetID) {
		return resultsCache.containsKey(packetID);
	}

	/**
	 * Retrieves a result for comparison from the cache. This should only be
	 * called after calling rewsultIsSaved() otherwise there is a risk of
	 * retrieving a null value.
	 * 
	 * @param packetID
	 *            the IDF of the packet
	 * @return a previously processed result for comparison.
	 */
	public IResultsPacket getResultForComparison(String packetID) {
		return resultsCache.get(packetID);
	}

	/**
	 * Returns the number of packets that have already been processed.
	 * 
	 * @return the number of packets that have been processed.
	 */
	public int getNumberOfPacketsProcessed() {
		return resultsCache.size();
	}

	/**
	 * Loads previously processed results packets from the database. This method
	 * should be used when the server is restarted.
	 */
	public void loadResultsPackets() {
		Collection<IResultsPacket> resultsPackets = resultsDB
				.getResultsPackets();

		for (IResultsPacket resultsPacket : resultsPackets) {
			resultsCache
					.putIfAbsent(resultsPacket.getPacketId(), resultsPacket);
		}
	}

}
