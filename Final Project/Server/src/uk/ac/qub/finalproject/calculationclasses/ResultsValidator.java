/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import uk.ac.qub.finalproject.persistence.DeviceDetailsManager;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * 
 * @author Phil
 *
 */
public class ResultsValidator implements IResultValidator {

	private ResultsPacketManager resultsDrawer;

	/**
	 * Used to validate individual results packets.
	 */
	private IValidationStrategy validationStrategy;

	/**
	 * Default constructor. IF this is called the validation strategy must be
	 * set before calling any of this classes methods.
	 */
	public ResultsValidator() {

	}

	public ResultsValidator(ResultsPacketManager resultsDrawer) {
		this.resultsDrawer = resultsDrawer;
	}

	public ResultsValidator(ResultsPacketManager resultsPacketManager,
			DeviceDetailsManager deviceDetailsManager) {
		this(resultsPacketManager);
	}

	/**
	 * Instantiates an abstract results validator with a validation strategy.
	 * The instance can now be used without further modification.
	 * 
	 * @param validationStrategy
	 *            the strategy used to validate
	 */
	public ResultsValidator(IValidationStrategy validationStrategy) {
		this.setValidationStrategy(validationStrategy);
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
