/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IResultValidator;
import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.IValidationStrategy;
import uk.ac.qub.finalproject.calculationclasses.IWorkPacket;

/**
 * @author Phil
 *
 */
public class ValidatorStub implements IResultValidator {

	private boolean resultPending;
	private boolean resultAdded;
	private boolean resultIsValid;
	private IWorkPacket initialData;
	private IResultsPacket resultsPacket;
	private String deviceID;
	
	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.calculationclasses.IResultValidator#resultIsPending(java.lang.String)
	 */
	@Override
	public boolean resultIsPending(String resultsPacketID) {		
		return resultPending;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.calculationclasses.IResultValidator#addResultToGroup(uk.ac.qub.finalproject.calculationclasses.IResultsPacket, uk.ac.qub.finalproject.calculationclasses.IWorkPacket, java.lang.String)
	 */
	@Override
	public void addResultToGroup(IResultsPacket resultPacket,
			IWorkPacket initialData, String deviceID) {
		resultAdded = true;
		this.resultsPacket = resultPacket;
		this.initialData = initialData;
		this.deviceID = deviceID;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.calculationclasses.IResultValidator#resultIsValid(uk.ac.qub.finalproject.calculationclasses.IResultsPacket, uk.ac.qub.finalproject.calculationclasses.IWorkPacket)
	 */
	@Override
	public boolean resultIsValid(IResultsPacket resultsPacket,
			IWorkPacket initalData) {		
		return resultIsValid;
	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.calculationclasses.IResultValidator#setValidationStrategy(uk.ac.qub.finalproject.calculationclasses.IValidationStrategy)
	 */
	@Override
	public void setValidationStrategy(IValidationStrategy validationStrategy) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.qub.finalproject.calculationclasses.IResultValidator#setGroupValidationStrategy(uk.ac.qub.finalproject.calculationclasses.IGroupValidationStrategy)
	 */
	@Override
	public void setGroupValidationStrategy(
			IGroupValidationStrategy groupValidationStrategy) {
		// TODO Auto-generated method stub

	}

	public boolean isResultPending() {
		return resultPending;
	}

	public void setResultPending(boolean resultPending) {
		this.resultPending = resultPending;
	}

	public boolean isResultAdded() {
		return resultAdded;
	}

	public void setResultAdded(boolean resultAdded) {
		this.resultAdded = resultAdded;
	}

	public boolean isResultIsValid() {
		return resultIsValid;
	}

	public void setResultIsValid(boolean resultIsValid) {
		this.resultIsValid = resultIsValid;
	}

	public IWorkPacket getInitialData() {
		return initialData;
	}

	public void setInitialData(IWorkPacket initialData) {
		this.initialData = initialData;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public IResultsPacket getResultsPacket() {
		return resultsPacket;
	}

	public void setResultsPacket(IResultsPacket resultsPacket) {
		this.resultsPacket = resultsPacket;
	}

}
