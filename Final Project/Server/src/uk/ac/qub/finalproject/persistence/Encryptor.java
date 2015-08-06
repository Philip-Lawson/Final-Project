/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

/**
 * @author Phil
 *
 */
public interface Encryptor {
	public byte[] encrypt(String toEncrypt);
	public String decrypt(byte[] toDecrypt);
}
