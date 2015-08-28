package uk.ac.qub.finalproject.calculationclassstubs;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

public class ValidationStrategyStub implements IValidationStrategy {
	
	private boolean newResultIsValid;
	private boolean comparedResultIsValid;

	@Override
	public boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket) {
		return newResultIsValid;
	}

	@Override
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {
		return comparedResultIsValid;
	}

	public boolean isNewResultIsValid() {
		return newResultIsValid;
	}

	public void setNewResultIsValid(boolean newResultIsValid) {
		this.newResultIsValid = newResultIsValid;
	}

	public boolean isComparedResultIsValid() {
		return comparedResultIsValid;
	}

	public void setComparedResultIsValid(boolean comparedResultIsValid) {
		this.comparedResultIsValid = comparedResultIsValid;
	}

}
