/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * The results validator validates all results that are sent from client
 * devices. It is essentially a co-ordinator class that allows for different
 * implementations of results caching and validation strategies to be used
 * without affecting core logic. <br></br> This is a single result-only validator and has
 * empty implementations of group validator methods. It is dependent on a
 * ResultsPacketManager cache to retrieve results and on a ValidationStrategy to
 * validate the results.
 * 
 * @author Phil
 *
 */
public class SingleResultsValidator implements IResultValidator {

	/**
	 * The results cache. This is used to retrieve results that have already
	 * been processed. This will hopefully make it quicker to validate
	 * duplicates.
	 */
	private ResultsPacketManager resultsDrawer;

	/**
	 * Used to validate individual results packets.
	 */
	private IValidationStrategy validationStrategy;

	/**
	 * Default constructor. IF this is called the validation strategy must be
	 * set before calling any of this classes methods.
	 */
	public SingleResultsValidator() {

	}

	public SingleResultsValidator(ResultsPacketManager resultsDrawer) {
		this.resultsDrawer = resultsDrawer;
	}

	public SingleResultsValidator(IValidationStrategy validationStrategy) {
		this.setValidationStrategy(validationStrategy);
	}
	
	public SingleResultsValidator(ResultsPacketManager resultsPacketManager,
			DeviceDetailsManager deviceDetailsManager) {
		this(resultsPacketManager);
	}
	
	public void setResultsPacketManager(ResultsPacketManager resultsPacketManager){
		this.resultsDrawer = resultsPacketManager;
	}
		

	@Override
	public boolean resultIsPending(String packetID) {
		return false;
	}

	@Override
	public final boolean resultIsValid(IResultsPacket resultsPacket,
			IWorkPacket initialData) {
		String packetID = resultsPacket.getPacketId();

		if (resultsDrawer.resultIsSaved(packetID)) {
			IResultsPacket savedResult = resultsDrawer
					.getResultForComparison(packetID);
			return validationStrategy.compareWithSavedResult(resultsPacket,
					savedResult);
		} else {
			return validationStrategy.validateNewResult(initialData,
					resultsPacket);
		}
	}

	@Override
	public final void setValidationStrategy(
			IValidationStrategy validationStrategy) {
		if (null != validationStrategy)
			this.validationStrategy = validationStrategy;
	}

	@Override
	public void addResultToGroup(IResultsPacket resultPacket,
			IWorkPacket initialData, String deviceID) {

	}

	@Override
	public void setGroupValidationStrategy(
			IGroupValidationStrategy groupValidationStrategy) {

	}

}
