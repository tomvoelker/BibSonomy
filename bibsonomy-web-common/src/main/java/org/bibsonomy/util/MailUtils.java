/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.services.URLGenerator;
import org.springframework.context.MessageSource;


/**
 * @author rja
 */
@Setter
public class MailUtils {
	private static final Log log = LogFactory.getLog(MailUtils.class);
	
	private static final String PLAIN_MAIL_CONTENT_TYPE = "text/plain; charset=UTF-8";
	private static final String HTML_MAIL_CONTENT_TYPE = "text/html; charset=UTF-8";
	
	/*
	 * The following constants are configured
	 */

	/** The name of the project. */
	private String projectName;

	/** The base URL of the project. */
	private String projectHome;

	/** A URL to the blog of the project. */
	private String projectBlog;

	/** The email address users can use to contact the project admins. */
	private String projectEmail;

	/**  The From: address of registration mails. */
	private String projectRegistrationFromAddress;

	/** The From: address of join group request mails. */
	private String projectJoinGroupRequestFromAddress;

	/** The configured mail address for error reporting */
	private String projectReportEmail;
	
	private MessageSource messageSource;
	
	private URLGenerator absoluteURLGenerator;

	/** Stores the properties for mailing (mail host). */
	private final Properties props = new Properties();

	/**
	 * Sends the activation mail to the user.
	 * 
	 * @param userName - the name of the user which registered. 
	 * @param userEmail - the email address of the user which registered.
	 * @param inetAddress - TODO: unused
	 * @param locale - a locale to use for localization
	 * @return <code>true</code>, if the email could be send without errors.
	 */
	public boolean sendActivationMail(final String userName, final String userEmail, final String inetAddress, final Locale locale) {
		final Object[] messagesParameters = new Object[]{
			userName, // 0
			this.projectName, // 1
			this.projectHome, // 2
			this.projectBlog, // 3
			this.projectEmail, // 4
			this.absoluteURLGenerator.getUserUrlByUserName(userName)}; // 5
		
		final String messageBody = messageSource.getMessage("mail.activation.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.activation.subject", messagesParameters, locale);

		/*
		 * set the recipients
		 */
		final String[] recipient = {userEmail};
		try {
			sendPlainMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send registration mail: " + e.getMessage());
		}
		return false;
	}
	
	/** 
	 * Sends the registration mail to the user and to the project admins.
	 * 
	 * @param userName - the name of the user which registered. 
	 * @param userEmail - the email address of the user which registered.
	 * @param activationCode - user activation code
	 * @param inetAddress - TODO: unused!!!
	 * @param locale - a locale to use for localization
	 * @return <code>true</code>, if the email could be send without errors.
	 */
	public boolean sendRegistrationMail (final String userName, final String userEmail, final String activationCode, final String inetAddress, final Locale locale) {
		final Object[] messagesParameters = new Object[]{userName, projectName, projectHome, projectBlog, projectEmail, activationCode};
		/*
		 * Format the message "mail.registration.body" with the given parameters.
		 */
		final String messageBody = messageSource.getMessage("mail.registration.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.registration.subject", messagesParameters, locale);

		/*
		 * set the recipients
		 */
		final String[] recipient = {userEmail};
		try {
			sendPlainMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send registration mail: " + e.getMessage());
		}
		return false;
	}
	
	/** 
	 * Sends the join request mail to the group admins.
	 * 
	 * @param groupName the name of the group to join
	 * @param groupMail the mail address of the group
	 * @param loginUser the n
	 * @param reason the reason to join the group
	 * @param locale the locale of the mail
	 * @return <code>true</code> iff mail was sent
	 */
	public boolean sendJoinGroupRequest(final String groupName, final String groupMail, final User loginUser, final String reason, final Locale locale) {
		final Object[] messagesParameters = new Object[]{
			groupName, // 0
			loginUser.getName(), // 1
			reason, // 2
			this.projectHome, // 3
			// TODO: why toLowerCase?
			UrlUtils.safeURIEncode(groupName).toLowerCase(), // 4
			UrlUtils.safeURIEncode(loginUser.getName()).toLowerCase(), // 5
			this.projectName.toLowerCase(), // 6
			this.projectEmail, // 7
			this.absoluteURLGenerator.getGroupSettingsUrlByGroupName(groupName, 1), // 8
			this.projectBlog // 9
		};
		
		/*
		 * Format the message "mail.registration.body" with the given parameters.
		 */
		final String messageBody    = messageSource.getMessage("mail.joinGroupRequest.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.joinGroupRequest.subject", messagesParameters, locale);
		
		/*
		 * send an e-Mail to the group (from our registration Adress)
		 */
		try {
			sendPlainMail(new String[] {groupMail},  messageSubject, messageBody, projectJoinGroupRequestFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send join group request mail: " + e.getMessage());
		}
		return false;
	}
	
	/** 
	 * Sends the join group denied mail to the user who requested to join the group.
	 * @param groupName 
	 * @param deniedUserName 
	 * @param deniedUserEMail 
	 * @param reason 
	 * @param locale 
	 * @return <code>true</code> iff mail was sent
	 */
	public boolean sendJoinGroupDenied(final String groupName, final String deniedUserName, final String deniedUserEMail, final String reason, final Locale locale) {
		final Object[] messagesParameters = new Object[]{
			groupName, 
			deniedUserName,
			reason,
			projectHome,
			// TODO: remove null values
			null,
			null,
			// TODO: why to lower case?
			projectName.toLowerCase(),
			projectEmail
		};
		/*
		 * Format the message "mail.registration.body" with the given parameters.
		 */
		final String messageBody;
		if (present(reason)) {
			messageBody = messageSource.getMessage("mail.joinGroupRequest.denied.bodyWithReason", messagesParameters, locale);
		} else {			
			messageBody = messageSource.getMessage("mail.joinGroupRequest.denied.body", messagesParameters, locale);
		}
		final String messageSubject = messageSource.getMessage("mail.joinGroupRequest.denied.subject", messagesParameters, locale);
	
		/*
		 * set the recipients
		 */
		final String[] recipient = {deniedUserEMail};
		try {
			sendPlainMail(recipient, messageSubject, messageBody, projectJoinGroupRequestFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send Deny JoinGrouprequest mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * 
	 * @param group
	 * @param requestingUser
	 * @param locale
	 * @return 
	 */
	public boolean sendGroupActivationNotification(final Group group, User requestingUser, final Locale locale) {
		final Object[] messagesParameters = new Object[] {
			UserUtils.getNiceUserName(requestingUser, true),
			group.getName(),
			this.absoluteURLGenerator.getGroupUrlByGroupName(group.getName()),
			this.absoluteURLGenerator.getGroupSettingsUrlByGroupName(group.getName(), null),
			this.projectHome,
			this.projectEmail
		};
		
		final String messageBody = messageSource.getMessage("mail.group.activation.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.group.activation.subject", messagesParameters, locale);

		/*
		 * send an e-mail to user who requested the group
		 */
		try {
			sendPlainMail(new String[] {requestingUser.getEmail()},  messageSubject, messageBody, projectEmail);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send group activation notification mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * @param groupName 
	 * @param declineMessage 
	 * @param requestingUser
	 * @param locale
	 * @return <code>true</code> iff mail was sent successfully
	 */
	public boolean sendGroupDeclineNotification(final String groupName, final String declineMessage, User requestingUser, final Locale locale) {
		final Object[] messagesParameters = new Object[] {
				requestingUser.getName(), // 0
				groupName, // 1
				declineMessage, // 2
				this.projectHome, // 3
				this.projectEmail, // 4
				this.projectName, // 5
				this.projectBlog // 6
		};
		
		final String messageSubject = messageSource.getMessage("mail.group.decline.subject", messagesParameters, locale);
		final String messageBody = messageSource.getMessage("mail.group.decline.body", messagesParameters, locale);
		
		/*
		 * send an e-mail to the user
		 */
		try {
			sendPlainMail(new String[] {requestingUser.getEmail()},  messageSubject, messageBody, projectEmail);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send group decline notification mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * sends a group invite mail to the invited user
	 * 
	 * @param groupName
	 * @param loginUser
	 * @param invitedUser
	 * @param locale
	 * @return <code>true</code> if mail was send successful
	 */
	public boolean sendGroupInvite(final String groupName, final User loginUser, final User invitedUser, final Locale locale) {
		final Object[] messagesParameters = new Object[]{
				invitedUser.getName(), // 0
				loginUser.getName(), // 1
				groupName, // 2
				this.projectHome, // 3
				// TODO: why toLowerCase?
				this.projectBlog, // 4
				UrlUtils.safeURIEncode(loginUser.getName()).toLowerCase(), // 5
				this.projectName, // 6
				this.projectEmail, // 7
				this.absoluteURLGenerator.getSettingsUrlWithSelectedTab(3) // 8
		};
		
		/*
		 * Format the message "mail.group.invite.body" with the given parameters.
		 */
		final String messageBody = messageSource.getMessage("mail.group.invite.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.group.invite.subject", messagesParameters, locale);

		/*
		 * send an e-Mail to the group (from our registration Adress)
		 */
		try {
			sendPlainMail(new String[] {invitedUser.getEmail()},  messageSubject, messageBody, projectJoinGroupRequestFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send join group request mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Method to send the password reminder mail
	 * 
	 * @param userName
	 * @param userEmail
	 * @param inetAddress TODO: unused!!!
	 * @param locale
	 * @param maxmin 
	 * @param tmppw 
	 * @return true, if the mail could be send without errors
	 */
	public boolean sendPasswordReminderMail(final String userName, final String userEmail, final String inetAddress, final Locale locale, final int maxmin, final String tmppw){
		final Object[] messagesParameters = new Object[]{userName, projectName, projectHome, projectBlog, projectEmail, maxmin, tmppw};
		
		final String messageBody = messageSource.getMessage("reminder.mail.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("reminder.mail.subject", messagesParameters, locale);
		
		/*
		 * set the recipients
		 */
		final String[] recipient = {userEmail};
		try {
			sendPlainMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send reminder mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Method to send an eMail notification regarding the auto-sync 
	 * @param userName
	 * @param userEmail
	 * @param syncClientName
	 * @param locale
	 * @return true, if the mail could be send without errors
	 */
	public boolean sendSyncErrorMail(final String userName, final String userEmail, final String syncClientName, final Locale locale){
		final Object[] messagesParameters = new Object[]{userName, projectName, projectHome, projectBlog, syncClientName};
		
		final String messageBody = messageSource.getMessage("mail.sync.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.sync.subject", messagesParameters, locale);
		
		/*
		 * set the recipients
		 */
		final String[] recipient = {userEmail};
		try {
			this.sendHTMLMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
			return true;
		} catch (final MessagingException e) {
			log.fatal("Could not send reminder mail: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * @param requestedGroup
	 */
	public void sendGroupRequest(final Group requestedGroup) {
		try {
			// TODO: use project default locale?
			final Locale locale = Locale.ENGLISH;
			final GroupRequest groupRequest = requestedGroup.getGroupRequest();
			final String userName = groupRequest.getUserName();
			final String userUrl = this.absoluteURLGenerator.getUserUrlByUserName(userName);
			final String groupAdminPage = this.absoluteURLGenerator.getAdminUrlByName("group");
			final Object[] messagesParameters = { requestedGroup.getName(), userName, userUrl, requestedGroup.getDescription(), groupRequest.getReason(), groupAdminPage };
			final String messageBody = messageSource.getMessage("grouprequest.mail.body", messagesParameters, locale);
			final String messageSubject = messageSource.getMessage("grouprequest.mail.subject", messagesParameters, locale);
			
			// TODO: currently using projectEmail, maybe we want a special mail address?
			this.sendHTMLMail(new String[] { this.projectEmail }, messageSubject, messageBody, this.projectEmail);
		} catch (final MessagingException e) {
			log.fatal("Could not send group request mail: " + e.getMessage());
		}
	}

	public boolean sendReportMail(final String subjectKey, final String bodyKey, final Object[] subjectParameters, final Object[] bodyParameters, final Locale locale) {
		if (present(this.projectReportEmail)) {
			try {
				final String messageSubject = messageSource.getMessage(subjectKey, subjectParameters, locale);
				final String messageBody = messageSource.getMessage(bodyKey, bodyParameters, locale);

				this.sendHTMLMail(new String[]{this.projectReportEmail}, messageSubject, messageBody, this.projectReportEmail);
			} catch (final MessagingException e) {
				log.fatal("Could not send report mail: " + e.getMessage());
				return false;
			}

			return true;
		} else {
			log.warn("Could not send report mail due to the project's report e-mail address has not been set.");
			return false;
		}
	}

	public boolean sendUnableToMatchRelationMail(final String title, final String interhash, final String personId, final String receivermail) {
		try {
			final String postUrl = absoluteURLGenerator.getPublicationUrlByInterHash(interhash);
			final Object[] messagesParameters = { postUrl, title, personId };

			final Locale locale = Locale.ENGLISH;
			final String messageSubject = messageSource.getMessage("database.exception.systemTag.addRelation.subject", null, locale);
			final String messageBody = messageSource.getMessage("database.exception.systemTag.addRelation.body", messagesParameters, locale);

			this.sendHTMLMail(new String[]{receivermail}, messageSubject, messageBody, receivermail);
		} catch (final MessagingException e) {
			log.fatal("Could not send mail to report matching failed: " + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Sends a plain mail to the given recipients
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @param from
	 * @throws MessagingException
	 */
	public void sendPlainMail(final String[] recipients, final String subject, final String content, final String from) throws MessagingException {
		sendMail(recipients, subject, content, from, PLAIN_MAIL_CONTENT_TYPE);
	}
	
	/**
	 * sends a html mail to the given recipients
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @param from
	 * @throws MessagingException
	 */
	public void sendHTMLMail(final String[] recipients, final String subject, final String content, final String from) throws MessagingException {
		sendMail(recipients, subject, content, from, HTML_MAIL_CONTENT_TYPE);
	}
	
	/**
	 * @param recipients
	 * @param subject
	 * @param content
	 * @param from
	 * @param contentType 
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private void sendMail(final String[] recipients, final String subject, final String content, final String from, String contentType) throws AddressException, MessagingException {
		// create some properties and get the default Session
		final Session session = Session.getDefaultInstance(props, null);
		
		// create a message
		final Message message = new MimeMessage(session);

		// set the from and to address
		final InternetAddress addressFrom = new InternetAddress(from);
		message.setFrom(addressFrom);

		final InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		message.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		message.setSubject(subject);
		message.setContent(content, contentType);
		Transport.send(message);
	}

	/**
	 * A host which accepts SMTP requests and should be used for sending mails.
	 * 
	 * @param mailHost
	 */
	public void setMailHost(final String mailHost) {
		props.put("mail.smtp.host", mailHost);
	}

}
