package org.bibsonomy.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;


/**
 * @author rja
 * @version $Id$
 */
public class MailUtils {

	private static final Logger log = Logger.getLogger(MailUtils.class);
	/*
	 * The following constants are configured using JNDI.
	 */
	private String projectName;
	private String projectHome;
	private String projectBlog;
	private String projectEmail;
	private String projectRegistrationFromAddress;
	/**
	 * Prefix of the property files holding the localized messages. 
	 */
	private final static String messagesFileNamePrefix = "messages";
	/**
	 * Stores the properties for mailing (mail host). 
	 */
	final Properties props = new Properties();
	

	/** Sends the registration mail to the user and to the project admins.
	 * 
	 * @param userName - the name of the user which registered. 
	 * @param userEmail - the email address of the user which registered.
	 * @param inetAddress - 
	 * @param locale - a locale to use for localization
	 * @return <code>true</code>, if the email could be send without errors.
	 */
	public boolean sendRegistrationMail (final String userName, final String userEmail, final String inetAddress, final Locale locale) {
		final Object[] messagesParameters = new Object[]{userName, projectName, projectHome, projectBlog, projectEmail};
		/*
		 * Format the message "mail.registration.body" with the given parameters.
		 */
		final String messageBody    = getFormattedMessage(locale, "mail.registration.body", messagesParameters);
		final String messageSubject = getFormattedMessage(locale, "mail.registration.subject", messagesParameters);

		/*
		 * set the recipients
		 */
		final String recipient[] = new String[] {userEmail};
		try {
			sendMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send registration mail: " + e.getMessage());
		}
		return false;
	}

	/** Inserts the parameters into the message.
	 * 
	 * @param language
	 * @param country
	 * @param messageKey
	 * @param messageParameters
	 * @return
	 */
	private String getFormattedMessage(final Locale locale, final String messageKey, final Object[] messageParameters) {
		/*
		 * get the locale for the given language and country.
		 */
		final ResourceBundle messages = ResourceBundle.getBundle(messagesFileNamePrefix, locale);
		/*
		 * prepare a formatter for the given locale
		 */
		final MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		/*
		 * load the message
		 */
		formatter.applyPattern(messages.getString(messageKey));
		/*
		 * format the message
		 */
		return formatter.format(messageParameters);
	}


	/** Sends a mail to the given recipients
	 * @param recipients
	 * @param subject
	 * @param message
	 * @param from
	 * @throws MessagingException
	 */
	private void sendMail(final String recipients[ ], final String subject, final String message, final String from) throws MessagingException {
		boolean debug = false;

		// create some properties and get the default Session
		final Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		final Message msg = new MimeMessage(session);

		// set the from and to address
		final InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		final InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Optional : You can also set your custom headers in the Email if you Want
		//msg.addHeader("X-Sent-By", "Bibsonomy-Bot");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setText(message);
		Transport.send(msg);
	}


	/** The name of the project.
	 * 
	 * @param projectName
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	/** The base URL of the project.
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}


	/** A URL to the blog of the project.
	 * 
	 * @param projectBlog
	 */
	public void setProjectBlog(String projectBlog) {
		this.projectBlog = projectBlog;
	}


	/** The email address users can use to contact the project admins. 
	 * 
	 * @param projectEmail
	 */
	public void setProjectEmail(String projectEmail) {
		this.projectEmail = projectEmail;
	}


	/** The From: address of registration mails. 
	 * 
	 * @param projectRegistrationFromAddress
	 */
	public void setProjectRegistrationFromAddress(String projectRegistrationFromAddress) {
		this.projectRegistrationFromAddress = projectRegistrationFromAddress;
	}

	/** A host which accepts SMTP requests and should be used for sending mails.
	 * 
	 * @param mailHost
	 */
	public void setMailHost(String mailHost) {
		props.put("mail.smtp.host", mailHost);
	}


}
