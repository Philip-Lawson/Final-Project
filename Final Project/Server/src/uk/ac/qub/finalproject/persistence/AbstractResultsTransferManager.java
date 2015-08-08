/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;
import uk.ac.qub.finalproject.server.calculationclasses.IResultsPacket;


/**
 * This class encapsulates the logic for transferring results from the server
 * database to another persistence layer. To be used the implementor must
 * implement a method for converting a collection of results packets into a
 * collection of the desired object e.g. Numbers, Files etc. There are also
 * boilerplate methods for connecting to a database, writing to the database and
 * closing the database. It is the responsibility of the user to implement these
 * methods as they see fit.
 * 
 * @author Phil
 *
 */
public abstract class AbstractResultsTransferManager {

	/**
	 * The results packet DAO used to retrieve the collection of results.
	 */
	private ResultsPacketJDBC resultsDB = new ResultsPacketJDBC();

	/**
	 * Abstract method to convert a collection of results packets to a
	 * collection of the desired type.
	 * 
	 * @param resultsPackets
	 *            the collection of results packets.
	 * @return a collection of the desired object e.g. Numbers, Files etc.
	 */
	protected abstract Collection<?> convertResults(
			Collection<IResultsPacket> resultsPackets);

	/**
	 * Abstract method 
	 */
	protected abstract void connectToDatabase();

	protected abstract void writeResults(Collection<?> convertedResults);

	protected abstract void closeConnection();

	public final void transferResults() {
		Collection<?> convertedResults = convertResults(resultsDB
				.getResultsPackets());
		connectToDatabase();
		writeResults(convertedResults);
		closeConnection();
	}

}
