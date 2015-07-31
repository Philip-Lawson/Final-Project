/**
 * 
 */
package finalproject.poc.persistence;

/**
 * @author Phil
 *
 */
public interface Encryptor {
	public byte[] encrypt(String toEncrypt);
	public String decrypt(byte[] toDecrypt);
}
