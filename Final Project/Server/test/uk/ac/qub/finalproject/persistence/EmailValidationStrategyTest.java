package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailValidationStrategyTest {
	
	EmailValidationStrategy test;
	String validEmail, emailWithPlusSign, blankEmail, missingFullStop, missingAtSign, noEmailBody, noEmailProvider;

	@Before
	public void setUp() throws Exception {
		test = new EmailValidationStrategy();
		validEmail = "jhhjj@gmail.com";
		emailWithPlusSign = "fsg+gs@aff.com";
		blankEmail = "";
		missingFullStop = "dggf@sfsf";
		missingAtSign = "asdasdd.asdd";
		noEmailBody = "@sgdsg.com";
		noEmailProvider = "dasdsad@";
	}

	@After
	public void tearDown() throws Exception {
		test = null;
	}

	@Test
	public void testValidEmail() {
		assertTrue(test.emailIsValid(validEmail));
	}
	
	@Test
	public void testEmailWithPlusSign(){
		assertTrue(test.emailIsValid(emailWithPlusSign));
	}
	
	@Test
	public void testBlankEmail(){
		assertTrue(test.emailIsValid(blankEmail));
	}
	
	@Test
	public void testEmailNoFullStop(){
		assertFalse(test.emailIsValid(missingFullStop));
	}
	
	@Test
	public void testEmailNoAtSign(){
		assertFalse(test.emailIsValid(missingAtSign));
	}
	
	@Test
	public void testEmailNoBody(){
		assertFalse(test.emailIsValid(noEmailBody));
	}
	
	@Test
	public void testEmailNoProvider(){
		assertFalse(test.emailIsValid(noEmailProvider));
	}

}
