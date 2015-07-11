/**
 * 
 */
package finalproject.poc.calculationclasses;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import finalproject.poc.persistence.DatabaseFacade;
import finalproject.poc.persistence.ResultsPacketManager;

/**
 * An abstract representation of a result packet validator. <br>
 * </br> The validator has an IValidationStrategy as a class member, allowing
 * the validation strategy to be changed dynamically. Valid results are stored
 * in a ConcurrentMap to allow for fast reads and synchronised writes .Once a
 * valid result is processed and stored duplicate results packets will be
 * compared using the compareWithCachedResult method. This should prove useful
 * if the validation strategy is computationally intensive. <br>
 * </br> The resultIsCached method is present should the consumer wish to
 * implement policies to minimize entries to a database or to quickly discard
 * duplicates once a result has been processed. In that case resultIsCached
 * <b>must</b> be called before validating a result.
 * 
 * @author Phil
 *
 */
public abstract class AbstractResultsValidator implements IResultValidator {

	

	private ResultsPacketManager resultsDrawer;
	
	/**
	 * Used to validate individual results packets.
	 */
	private IValidationStrategy validationStrategy;

	/**
	 * Default constructor. IF this is called the validation strategy must be
	 * set before calling any of this classes methods.
	 */
	public AbstractResultsValidator() {

	}

	/**
	 * Instantiates an abstract results validator with a validation strategy.
	 * The instance can now be used without further modification.
	 * 
	 * @param validationStrategy
	 *            the strategy used to validate
	 */
	public AbstractResultsValidator(IValidationStrategy validationStrategy) {
		this.setValidationStrategy(validationStrategy);
	}

	@Override
	public final boolean resultIsValid(IResultsPacket resultsPacket) {
		String packetID = resultsPacket.getPacketId();
		
		if (resultsDrawer.resultIsSaved(packetID)) {
			IResultsPacket savedResult = resultsDrawer.getResultForComparison(packetID);
			return validationStrategy.compareWithSavedResult(resultsPacket, savedResult);
		} else {
			IWorkPacket workPacket = retrieveWorkPacket(resultsPacket.getPacketId());
			return validationStrategy.validateNewResult(workPacket,	resultsPacket);
		}
	}

	
	

	/**
	 * Retrieves a work packet using the result's packet ID. This work packet
	 * will be used to validate the results.
	 * 
	 * @param packetID
	 *            the packet ID used to track the initial work packet
	 * @return the initial work packet
	 */
	protected abstract IWorkPacket retrieveWorkPacket(String packetID);

	/*
	 * { return resultsPacket . equals ( resultsCache .get( resultsPacket ) .
	 * getPacketId ()); }
	 */

	@Override
	public final void setValidationStrategy(
			IValidationStrategy validationStrategy) {
		if (null != validationStrategy)
			this.validationStrategy = validationStrategy;
	}

}
