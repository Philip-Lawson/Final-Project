/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This email validation strategy checks if an email address is valid. In this
 * context valid means that the email address is in the format 'aaa@bb.cc'.
 * 
 * @author Phil
 *
 */
public class EmailValidationStrategy {

	private static final String EMAIL_REGEX = "^([a-zA-Z0-9+_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

	/**
	 * Determines if the email address is valid.
	 * 
	 * @param emailAddress
	 * @return true f the email address is valid.
	 */
	public boolean emailIsValid(String emailAddress) {
		if (null == emailAddress) {
			return false;
		} else {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			Matcher matcher = pattern.matcher(emailAddress);

			return matcher.matches();
		}
	}

}
