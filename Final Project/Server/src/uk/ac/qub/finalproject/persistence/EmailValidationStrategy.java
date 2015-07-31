/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Phil
 *
 */
public class EmailValidationStrategy {

	private static final String EMAIL_REGEX = "^([a-zA-Z0-9+_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

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
