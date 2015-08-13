/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * @author Phil
 *
 */
public abstract class AbstractValidationStrategy implements IValidationStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * finalproject.poc.calculationclasses.IValidationStrategy#validateNewResult
	 * (finalproject.poc.calculationclasses.IWorkPacket,
	 * finalproject.poc.calculationclasses.IResultsPacket)
	 */
	@Override
	public abstract boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket);

	/**
	 * Provides a basic bitwise comparison as a default for comparing two
	 * results that should be identical. If a more in depth comparison is
	 * required, this method should be overridden in the concrete class.
	 */
	@Override
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(resultsPacket.getResult());
			byte[] resultsData = baos.toByteArray();

			oos.writeObject(savedResult.getResult());
			byte[] savedResultsData = baos.toByteArray();

			return Arrays.equals(resultsData, savedResultsData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
