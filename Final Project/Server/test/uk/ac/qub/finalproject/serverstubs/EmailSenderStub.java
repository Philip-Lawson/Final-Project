/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import uk.ac.qub.finalproject.persistence.Achievements;
import uk.ac.qub.finalproject.server.ClientAchievementEmailSender;

/**
 * @author Phil
 *
 */
public class EmailSenderStub extends ClientAchievementEmailSender {

	private Achievements emailType;
	
	private boolean emailSent = false;
		
	public Achievements getEmailTypeSent(){
		return emailType;
	}
	
	public void clearAchievement(){
		emailType = null;
	}
	
	public void setEmailSent(boolean sent){
		this.emailSent = sent;
	}
	
	public boolean emailSent(){
		return emailSent;
	}
	
	@Override
	public void sendEmail(String emailAddress, Achievements type){
		emailType = type;
		emailSent = true;
	}
}
