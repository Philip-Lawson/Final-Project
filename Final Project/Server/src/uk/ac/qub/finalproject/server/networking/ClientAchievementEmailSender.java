/**
 * 
 */
package uk.ac.qub.finalproject.server.networking;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import uk.ac.qub.finalproject.persistence.Achievements;
import uk.ac.qub.finalproject.persistence.LoggingUtils;
import uk.ac.qub.finalproject.server.implementations.Implementations;

/**
 * This class encapsulates the work needed to send an email to registered
 * clients. It uses a domain specific implementation of the client email factory
 * to send to correct email.
 * 
 * @author Phil
 *
 */
public class ClientAchievementEmailSender {

	private Logger logger = LoggingUtils
			.getLogger(ClientAchievementEmailSender.class);

	private static String SENDER_EMAIL = Implementations.getEmailAddress();
	private static String EMAIL_PASSWORD = Implementations.getEmailPassword();

	private AbstractClientAchievementEmailFactory emailBuilder = Implementations
			.getEmailFactory();

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
			logger.log(Level.FINE, ClientAchievementEmailSender.class.getName()
					+ " Problem sending email to users", e);
		}
	}

	public static void changeEmailAddress(String senderEmail) {
		SENDER_EMAIL = senderEmail;
	}

	public static void changePassword(String emailPassword) {
		EMAIL_PASSWORD = emailPassword;
	}

}
