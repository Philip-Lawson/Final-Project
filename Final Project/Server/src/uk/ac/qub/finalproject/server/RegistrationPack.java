/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.io.Serializable;

/**
 * This is a POJO that contains all the details needed to identify a specific
 * android device. Currently this s just their device ID and potentially an
 * email address, but that could change in the future.
 * 
 * @author Phil
 *
 */
public class RegistrationPack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5605818805008317998L;

	private String androidID;
	private String emailAddress;

	public String getAndroidID() {
		return androidID;
	}

	public void setAndroidID(String androidID) {
		this.androidID = androidID;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean hasEmailAddress() {
		return null != emailAddress;
	}

}
