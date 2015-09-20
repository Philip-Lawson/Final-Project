/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import javafx.util.StringConverter;

/**
 * This number formatter is used to format the numbers on the line chart to
 * avoid showing floating points when natural numbers are used exclusively.
 * 
 * @author Phil
 *
 */
public class NumberFormatter extends StringConverter<Number> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.StringConverter#fromString(java.lang.String)
	 */
	@Override
	public Number fromString(String arg0) {
		Number num = Double.parseDouble(arg0);
		return num.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.StringConverter#toString(java.lang.Object)
	 */
	@Override
	public String toString(Number arg0) {
		if (arg0.intValue() != arg0.doubleValue()) {
			return "";
		} else {
			return "" + (arg0.intValue());
		}
	}

}
