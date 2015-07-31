/**
 * 
 */
package uk.ac.qub.finalproject.server.calculationclasses;

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
	 * Determines if the results packet returned from the client is a valid
	 * result i.e. not fraudulent. To know if the result is valid the validation
	 * strategy will need to be aware of the initial data.
	 *
	 * @param resultsPacket
	 *            the results packet to be checked
	 * @return true if the results is valid
	 */
	public boolean resultIsValid(IResultsPacket resultsPacket);

	/**
	 * Dynamically changes the validation strategy used. All implementations must
	 * prevent the strategy from being assigned as null.
	 * 
	 * @param validationStrategy the new validation strategy 
	 */
	public void setValidationStrategy(IValidationStrategy validationStrategy);

}
