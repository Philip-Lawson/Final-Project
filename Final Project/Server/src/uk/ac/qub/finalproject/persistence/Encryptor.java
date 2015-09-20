/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import uk.ac.qub.finalproject.server.implementations.Implementations;

/**
 * The Encryptor Singleton encapsulates the methods needed to encrypt and
 * decrypt sensitive client information.
 * 
 * @author Phil
 *
 */
public class Encryptor {

	private static Encryptor uniqueInstance;
	private static final String ALGORITHM = "AES";

	private Key secretKey;
	private Cipher cipher;

	public static Encryptor getEncryptor() {
		if (null == uniqueInstance) {
			synchronized (Encryptor.class) {
				if (null == uniqueInstance) {
					uniqueInstance = new Encryptor();
				}
			}
		}

		return uniqueInstance;
	}

	private Encryptor() {
		try {
			secretKey = new SecretKeySpec(Implementations.getSecretKeySpec(),
					"AES");
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

		}
	}

	/**
	 * Encrypts a string and returns a byte array of the encrypted string.
	 * 
	 * @param toEncrypt
	 * @return
	 */
	public synchronized byte[] encrypt(String toEncrypt) {

		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(toEncrypt.getBytes());
			return encryptedBytes;
		} catch (InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {

		}

		return toEncrypt.getBytes();
	}

	/**
	 * Decrypts the byte array and returns the original stirng.
	 * 
	 * @param toDecrypt
	 * @return
	 */
	public synchronized String decrypt(byte[] toDecrypt) {

		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(toDecrypt);
			return new String(decryptedBytes);

		} catch (InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}

		return "";
	}

}
