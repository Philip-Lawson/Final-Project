/**
 * 
 */
package finalproject.poc.calculationclasses;

/**
 * The validation strategy used to validate results packets.
 * 
 * @author Phil
 *
 */
public interface IValidationStrategy {

	/**
	 * Checks that a results packet is valid. All implementations should return
	 * true if the results packet is valid.
	 * 
	 * @param workPacket
	 *            the initial work packet that was processed
	 * @param resultsPacket
	 *            the result of the processed data
	 * @return true if the result is valid
	 */
	public boolean validateResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket);
}
