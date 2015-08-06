/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * @author Phil
 *
 */
public class EncryptorImpl implements Encryptor {

	private KeyGenerator keyGenerator;
	private SecretKey secretKey;
	private Cipher cipher;
	
	public EncryptorImpl(){
		initializeElements();
	}

	public byte[] encrypt(String toEncrypt) {

		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(toEncrypt.getBytes());
			return encryptedBytes;
		} catch (InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {

		}

		return toEncrypt.getBytes();
	}

	public String decrypt(byte[] toDecrypt) {		

		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(toDecrypt);
			return new String(decryptedBytes);

		} catch (InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {

		}

		return "";
	}

	private void initializeElements() {
		try {
			if (null == keyGenerator)
				keyGenerator = KeyGenerator.getInstance("AES");

			if (null == secretKey)
				secretKey = keyGenerator.generateKey();

			if (null == cipher)
				cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

		}
	}

}
