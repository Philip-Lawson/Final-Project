/**
 * 
 */
package com.example.appproofofconcept;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Phil
 *
 */
public class EmailValidationStrategy {
	
	private static final String BASIC_EMAIL_REGEX = "/.+@.+\\..+/i";
	
	public static boolean emailIsValid(String emailAddress){
		if (null == emailAddress) {
			return false;
		} else {
			Pattern pattern = Pattern.compile(BASIC_EMAIL_REGEX);
			Matcher matcher = pattern.matcher(emailAddress);
			
			return matcher.matches();
		}		
	}

}
