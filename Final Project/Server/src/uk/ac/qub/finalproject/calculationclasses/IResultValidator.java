/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

/**
 * Implementations of this interface must use a validation strategy to determine
 * if a results packet contains a valid result. The implementor must allow the
 * validation strategy to be replaced dynamically.
 * 
 * @author Phil
 *
 */
public interface IResultValidator {

	/**
	 * Returns true if the validity of the result is still pending. This is
	 * applicable for group analysis when there are insufficient results stored
	 * to complete validation.
	 * 
	 * @return
	 */
	public boolean resultIsPending(String resultsPacketID);

	/**
	 * Only applicable for group comparison. If a result is still pending the
	 * result should be added to the group along with the ID of the device that
	 * processed the result.
	 * 
	 * @param resultPacket
	 */
	public void addResultToGroup(IResultsPacket resultPacket, IWorkPacket initialData, String deviceID);

	/**
	 * Determines if the results packet returned from the client is a valid
	 * result i.e. not fraudulent. To know if the result is valid the validation
	 * strategy will need to be aware of the initial data.
	 *
	 * @param resultsPacket
	 *            the results packet to be checked
	 * @return true if the results is valid
	 */
	public boolean resultIsValid(IResultsPacket resultsPacket, IWorkPacket initalData);

	/**
	 * Dynamically changes the validation strategy used. All implementations
	 * must prevent the strategy from being assigned as null.
	 * 
	 * @param validationStrategy
	 *            the new validation strategy
	 */
	public void setValidationStrategy(IValidationStrategy validationStrategy);
	
	public void setGroupValidationStrategy(IGroupValidationStrategy groupValidationStrategy);

}
