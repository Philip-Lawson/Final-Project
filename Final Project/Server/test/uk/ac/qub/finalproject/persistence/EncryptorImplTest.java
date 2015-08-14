package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EncryptorImplTest {

	EncryptorImpl test;
	@Before
	public void setUp() throws Exception {
		test = new EncryptorImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncrypt() {
		String toEncrypt = "abcdefg";
		byte[] encryptedString = test.encrypt(toEncrypt);
		
		assertEquals(toEncrypt, test.decrypt(encryptedString));
		
	}
	
	@Test
	public void testStringIsEncrypted() {
		String toEncrypt = "abcdefg";
		byte[] encryptedString = test.encrypt(toEncrypt);
		
		assertFalse(toEncrypt.equals(new String(encryptedString)));
		
	}

}
