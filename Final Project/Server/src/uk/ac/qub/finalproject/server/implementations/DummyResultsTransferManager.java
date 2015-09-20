/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;

/**
 * @author Phil
 *
 */
public class DummyResultsTransferManager extends AbstractResultsTransferManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#convertResults
	 * (java.util.Collection)
	 */
	@Override
	protected Collection<?> convertResults(
			Collection<IResultsPacket> resultsPackets) {
		ArrayList<Integer> results = new ArrayList<Integer>(
				resultsPackets.size());
		for (IResultsPacket resultsPacket : resultsPackets) {
			results.add((Integer) resultsPacket.getResult());
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#connectToDatabase
	 * ()
	 */
	@Override
	protected void connectToDatabase() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#writeResults
	 * (java.util.Collection)
	 */
	@Override
	protected void writeResults(Collection<?> convertedResults) {
		Iterator<?> resultsIt = convertedResults.iterator();

		while (resultsIt.hasNext()) {
			System.out.println(resultsIt.next().toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#closeConnection
	 * ()
	 */
	@Override
	protected void closeConnection() {
		// TODO Auto-generated method stub

	}

}
