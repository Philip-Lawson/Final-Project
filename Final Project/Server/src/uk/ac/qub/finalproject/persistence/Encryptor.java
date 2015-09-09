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

/**
 * @author Phil
 *
 */
public class Encryptor {

	private static final Encryptor uniqueInstance = new Encryptor();
	private static final String ALGORITHM = "AES";

	private Key secretKey;
	private Cipher cipher;

	

	public static Encryptor getEncryptor() {
		return uniqueInstance;
	}

	private Encryptor() {
		try {
			secretKey = new SecretKeySpec(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,}, "AES");
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

		}
	}

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
