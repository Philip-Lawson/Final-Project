package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.persistencestubs.DeviceDetailsManagerStub;
import uk.ac.qub.finalproject.persistencestubs.UserDetailsJDBCStub;
import uk.ac.qub.finalproject.server.RegistrationPack;
import uk.ac.qub.finalproject.serverstubs.EmailSenderStub;

public class UserDetailsManagerTest {
	
	UserDetailsManager test;
	UserDetailsJDBCStub dbStub;
	DeviceDetailsManagerStub detailsManagerStub; 
	EmailSenderStub emailStub;
	String validEmail, invalidEmail;

	@Before
	public void setUp() throws Exception {
		dbStub = new UserDetailsJDBCStub();
		emailStub = new EmailSenderStub();
		test = new UserDetailsManager(detailsManagerStub, dbStub);
		test.setEmailSender(emailStub);
		validEmail = "valid@email.com";
		invalidEmail = "invalid email";
	}

	@After
	public void tearDown() throws Exception {
		dbStub = null;
		test = null;
		detailsManagerStub = null;
		emailStub = null;
	}

	@Test
	public void testRetrieveDedicatedUserEmails() {
		test.retrieveDedicatedUserEmails();		
		assertTrue(dbStub.isGetUserEmailsCalled());
	}

	@Test
	public void testChangeEmailAddressValidEmail() {
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(validEmail);
		dbStub.setEmailChanged(true);
		
		assertTrue(test.changeEmailAddress(registrationPack));
	}
	
	@Test
	public void testChangeEmailAddressValidEmailFaultyDatabase() {
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(validEmail);
		dbStub.setEmailChanged(false);
		
		assertFalse(test.changeEmailAddress(registrationPack));
	}
	
	@Test
	public void testChangeEmailAddressInvalidEmail() {
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(invalidEmail);
		assertFalse(test.changeEmailAddress(registrationPack));
	}

	@Test
	public void testRegisterUserValidEmail() {
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(validEmail);
		
		dbStub.setEmailRegistered(true);
		assertTrue(test.registerUser(registrationPack));
	}
	
	@Test
	public void testRegisterUserValidEmailFaultyDatabase() {
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(validEmail);
		
		dbStub.setEmailRegistered(false);
		assertFalse(test.registerUser(registrationPack));
	}
	
	@Test
	public void testRegisterUserInvalidEmail(){
		RegistrationPack registrationPack = new RegistrationPack();
		registrationPack.setEmailAddress(invalidEmail);
		assertFalse(test.registerUser(registrationPack));
	}

	@Test
	public void testCheckForAchievementMilestoneInvalidEmail() {
		dbStub.setValidResults(Achievements.GOLD.getAwardThreshold());
		emailStub.setEmailSent(false);
		test.checkForAchievementMilestone(invalidEmail);
		
		assertFalse(emailStub.emailSent());
		
	}
	
	@Test
	public void testCheckForAchievementMilestoneOutsideRange() {
		dbStub.setValidResults(Achievements.GOLD.getAwardThreshold() + 2);
		emailStub.setEmailSent(false);
		test.checkForAchievementMilestone(validEmail);
		
		assertFalse(emailStub.emailSent());
	}
	
	@Test
	public void testCheckForAchievementMilestoneGold() {
		dbStub.setValidResults(Achievements.GOLD.getAwardThreshold());
		emailStub.setEmailSent(false);
		test.checkForAchievementMilestone(validEmail);
		
		assertTrue(emailStub.emailSent());
	}
	
	@Test
	public void testCheckForAchievementMilestoneSilver() {
		dbStub.setValidResults(Achievements.SILVER.getAwardThreshold());
		emailStub.setEmailSent(false);
		test.checkForAchievementMilestone(validEmail);
		
		assertTrue(emailStub.emailSent());
	}
	
	@Test
	public void testCheckForAchievementMilestoneBronze() {
		dbStub.setValidResults(Achievements.BRONZE.getAwardThreshold());
		emailStub.setEmailSent(false);
		test.checkForAchievementMilestone(validEmail);
		
		assertTrue(emailStub.emailSent());
	}
	

	
}
