/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

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
	public boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket);
	
	/**
	 * Checks that a results packet is identical to a previously processed
	 * duplicate. Since the system only needs one valid result from each work
	 * packet, this method is used to verify that a client is sending valid
	 * results. <br></br>The simplest implementation of this method is a bitwise
	 * comparison.
	 * 
	 * @param resultsPacket
	 * @param savedResult
	 * @return
	 */
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult);;
}
