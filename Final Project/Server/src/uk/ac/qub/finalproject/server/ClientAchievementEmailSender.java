/**
 * 
 */
package uk.ac.qub.finalproject.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import uk.ac.qub.finalproject.persistence.Achievements;


/**
 * @author Phil
 *
 */
public class ClientAchievementEmailSender {

	private static String SENDER_EMAIL = "";
	private static String EMAIL_PASSWORD = "";

	private AbstractClientAchievementEmailFactory emailBuilder;

	public void sendEmail(String emailAddress, Achievements type) {
		String emailTitle = emailBuilder.buildEmailTitle(type);
		String emailBody = emailBuilder.buildEmail(type);
		
		String host = "mail."
				+ SENDER_EMAIL.substring(SENDER_EMAIL.lastIndexOf("@") + 1,
						SENDER_EMAIL.length());

		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getDefaultInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(SENDER_EMAIL,
								EMAIL_PASSWORD);
					}
				});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SENDER_EMAIL));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					emailAddress));
			message.setSubject(emailTitle);
			message.setText(emailBody);

			// send the message
			Transport.send(message);		

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public static void changeEmailAddress(String senderEmail){
		SENDER_EMAIL = senderEmail;
	}
	
	public static void changePassword(String emailPassword){
		EMAIL_PASSWORD = emailPassword;
	}

}
