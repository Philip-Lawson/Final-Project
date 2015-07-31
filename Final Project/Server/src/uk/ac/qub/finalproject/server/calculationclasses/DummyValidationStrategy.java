/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

/**
 * @author Phil
 *
 */
public class DummyValidationStrategy extends AbstractValidationStrategy {

	/* (non-Javadoc)
	 * @see finalproject.poc.calculationclasses.IValidationStrategy#validateResult(finalproject.poc.calculationclasses.IWorkPacket, finalproject.poc.calculationclasses.IResultsPacket)
	 */
	@Override
	public boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {
		// TODO Auto-generated method stub
		return true;
	}

}
