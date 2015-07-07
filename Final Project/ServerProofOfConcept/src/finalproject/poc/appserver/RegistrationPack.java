/**
 * 
 */
package finalproject.poc.appserver;

import java.io.Serializable;

/**
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

	public boolean hasEmailAddress(){
		return null!= emailAddress ;
	}

}
