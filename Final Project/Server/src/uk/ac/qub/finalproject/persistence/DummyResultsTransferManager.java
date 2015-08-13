/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;

/**
 * @author Phil
 *
 */
public class DummyResultsTransferManager extends AbstractResultsTransferManager {

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#convertResults(java.util.Collection)
	 */
	@Override
	protected Collection<?> convertResults(
			Collection<IResultsPacket> resultsPackets) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#connectToDatabase()
	 */
	@Override
	protected void connectToDatabase() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#writeResults(java.util.Collection)
	 */
	@Override
	protected void writeResults(Collection<?> convertedResults) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.persistence.AbstractResultsTransfer#closeConnection()
	 */
	@Override
	protected void closeConnection() {
		// TODO Auto-generated method stub

	}

}
