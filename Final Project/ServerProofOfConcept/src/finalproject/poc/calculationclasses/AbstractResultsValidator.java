/**
 * 
 */
package finalproject.poc.calculationclasses;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Phil
 *
 */
public abstract class AbstractResultsValidator implements IResultValidator {

	private ConcurrentMap<String, IResultsPacket> resultsCache = new ConcurrentHashMap<String, IResultsPacket>();
	private IValidationStrategy validationStrategy;

	public AbstractResultsValidator() {

	}

	public AbstractResultsValidator(IValidationStrategy validationStrategy) {
		this.setValidationStrategy(validationStrategy);
	}

	@Override
	public final boolean resultIsValid(IWorkPacket workPacket,
			IResultsPacket resultsPacket) {
		if (resultIsCached(resultsPacket)) {
			return compareWithCachedResult(resultsPacket);
		} else {
			boolean isValid = validationStrategy.validateResult(workPacket,
					resultsPacket);

			if (isValid) {
				resultsCache.put(resultsPacket.getPacketId(), resultsPacket);
			}

			return isValid;
		}
	}

	public final boolean resultIsCached(IResultsPacket resultsPacket) {
		return resultsCache.containsKey(resultsPacket.getPacketId());
	}

	private final boolean compareWithCachedResult(IResultsPacket resultsPacket) {
		return resultsPacket.equals(resultsCache.get(resultsPacket)
				.getPacketId());
	}

	public final void setValidationStrategy(
			IValidationStrategy validationStrategy) {
		this.validationStrategy = validationStrategy;
	}

}
