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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
	private String projectFromAddress;
	/**
	 * Prefix of the property files holding the localized messages. 
	 */
	private final static String messagesFileNamePrefix = "messages";
	/**
	 * This class is a singleton. 
	 */
	private static MailUtils instance = null;
	/**
	 * Stores the properties for mailing (mail host). 
	 */
	final Properties props = new Properties();
	
	/**
	 * @return An instance of the MailUtils.
	 * @throws NamingException 
	 */
	public static MailUtils getInstance() throws NamingException {
		if (instance == null) {
			instance = initializeInstance();
		}
		return instance;
	}

	private MailUtils () {
		// singleton
	}

	private static MailUtils initializeInstance() throws NamingException {
		final MailUtils mailUtils = new MailUtils();

		/*
		 * configure project properties
		 */
		final Context context = (Context) new InitialContext().lookup("java:/comp/env");
		mailUtils.projectName = (String) context.lookup("projectName");
		mailUtils.projectHome = (String) context.lookup("projectHome");
		mailUtils.projectBlog = (String) context.lookup("projectBlog");
		mailUtils.projectEmail = (String) context.lookup("projectEmail");
		mailUtils.projectFromAddress = (String) context.lookup("projectFromAddress");
		
		/*
		 * Set the host smtp address
		 */
		mailUtils.props.put("mail.smtp.host", context.lookup("mailhost"));
		
		return mailUtils;
	}


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
			sendMail(recipient,  messageSubject, messageBody, projectFromAddress);
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

}
