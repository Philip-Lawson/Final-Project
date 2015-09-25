/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.io.Serializable;

/**
 * A POJO representing the client device's registration pack. This is used to
 * store the device's ID, email address and the version number of the app they
 * have installed.
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
	private int versionCode;

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

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

}
