/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.io.Serializable;

/**
 * A dummy implementation of the data processor class.
 * @author Phil
 *
 */
public class DummyProcessor extends AbstractDataProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696565467124647685L;

	@Override
	protected Serializable processData(Serializable obj) {
		// TODO Auto-generated method stub
		Integer number;
		try {
			number = (Integer) obj;
		} catch (ClassCastException e) {			
			return null;
		}
		
		return number*2;
	}

}
