/**
 * 
 */
package uk.ac.qub.finalproject.server.implementations;

import uk.ac.qub.finalproject.calculationclasses.AbstractValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

/**
 * @author Phil
 *
 */
public class DummyValidationStrategy extends AbstractValidationStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.calculationclasses.IValidationStrategy#validateResult
	 * (finalproject.poc.calculationclasses.IWorkPacket,
	 * finalproject.poc.calculationclasses.IResultsPacket)
	 */
	@Override
	public boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket) {
		Integer initialNum = (Integer) workPacket.getInitialData();
		Integer result = (Integer) resultsPacket.getResult();
		return result == (initialNum * 2);
	}

	@Override
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {
		Integer result = (Integer) resultsPacket.getResult();
		Integer previousResult = (Integer) savedResult.getResult();
		return result.equals(previousResult);
	}

}
