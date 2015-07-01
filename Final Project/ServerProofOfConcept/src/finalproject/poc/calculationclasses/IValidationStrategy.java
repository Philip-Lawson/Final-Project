/**
 * 
 */
package finalproject.poc.calculationclasses;

/**
 * @author Phil
 *
 */
public interface IValidationStrategy {
	public boolean validateResult(IWorkPacket workPacket, IResultsPacket resultsPacket);
}
