/**
 * 
 */
package finalproject.poc.persistence;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import finalproject.poc.calculationclasses.IResultsPacket;

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
public class ResultsPacketManager {

	/**
	 * The utility class for sending results to the database.
	 */
	private ResultsPacketJDBC resultsDB;

	/**
	 * Used to store results packets using their work packet ID as a key.
	 */
	private ConcurrentMap<String, IResultsPacket> resultsCache = new ConcurrentHashMap<String, IResultsPacket>();

	/**
	 * Writes the result packet to memory and to the database. If the current
	 * results packet is a duplicate it will be discarded.
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
		}

	}

	/**
	 * 
	 * @param packetID
	 * @return
	 */
	public boolean resultIsSaved(String packetID) {
		return resultsCache.containsKey(packetID);
	}

	public IResultsPacket getResultForComparison(String packetID) {
		return resultsCache.get(packetID);
	}

	public int getNumberOfPacketsProcessed() {
		return resultsCache.size();
	}
	
	public void loadResultsPackets(){
		Collection<IResultsPacket> resultsPackets = resultsDB.getResultsPackets();
		
		for(IResultsPacket resultsPacket : resultsPackets){
			resultsCache.putIfAbsent(resultsPacket.getPacketId(), resultsPacket);
		}
	}

}
