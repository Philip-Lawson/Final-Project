/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

/**
 * @author Phil
 *
 */
public abstract class AbstractValidationStrategy implements IValidationStrategy {

	/* (non-Javadoc)
	 * @see finalproject.poc.calculationclasses.IValidationStrategy#validateNewResult(finalproject.poc.calculationclasses.IWorkPacket, finalproject.poc.calculationclasses.IResultsPacket)
	 */
	@Override
	public abstract boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket) ;

	/* (non-Javadoc)
	 * @see finalproject.poc.calculationclasses.IValidationStrategy#compareWithSavedResult(finalproject.poc.calculationclasses.IResultsPacket, finalproject.poc.calculationclasses.IResultsPacket)
	 */
	@Override
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {
		// TODO Auto-generated method stub
		return false;
	}

}
