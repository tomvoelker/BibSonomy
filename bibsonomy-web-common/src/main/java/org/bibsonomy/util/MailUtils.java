/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
public class MailUtils {
	private static final Log log = LogFactory.getLog(MailUtils.class);
	
	private static final String PLAIN_MAIL_CONTENT_TYPE = "text/plain; charset=UTF-8";
	private static final String HTML_MAIL_CONTENT_TYPE = "text/html; charset=UTF-8";
	
	/*
	 * The following constants are configured
	 */
	private String projectName;
	private String projectHome;
	private String projectBlog;
	private String projectEmail;
	private String projectRegistrationFromAddress;
	private String projectJoinGroupRequestFromAddress;
	
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
		final Object[] messagesParameters = new Object[]{userName,
			projectName,
			projectHome,
			projectBlog,
			projectEmail,
			absoluteURLGenerator.getUserUrlByUserName(userName)};
		/*
		 * Format the message "mail.registration.body" with the given parameters.
		 */
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
				groupName,
				loginUser.getName(),
				reason,
				projectHome,
				// TODO: why toLowerCase?
				UrlUtils.safeURIEncode(groupName).toLowerCase(),
				UrlUtils.safeURIEncode(loginUser.getName()).toLowerCase(),
				projectName.toLowerCase(),
				projectEmail,
				absoluteURLGenerator.getGroupSettingsUrlByGroupName(groupName, Integer.valueOf(1))
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
			groupName, deniedUserName,
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
		final String messageBody = messageSource.getMessage("mail.joinGroupRequest.denied.body", messagesParameters, locale);
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
			absoluteURLGenerator.getGroupUrlByGroupName(group.getName()),
			absoluteURLGenerator.getGroupSettingsUrlByGroupName(group.getName(), null),
			projectHome,
			projectEmail
		};
		
		/*
		 * Format the message "mail.groupInvite.body" with the given parameters.
		 */
		final String messageBody    = messageSource.getMessage("mail.group.activation.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.group.activation.subject", messagesParameters, locale);

		/*
		 * send an e-Mail to the group (from our registration Adress)
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
	 * 
	 * @param group
	 * @param requestingUser
	 * @param locale
	 * @return 
	 */
	public boolean sendGroupDeclineNotification(final String groupName, final String declineMessage, User requestingUser, final Locale locale) {
		final Object[] messagesParameters = new Object[] {
				requestingUser.getName(),
				groupName,
				declineMessage,
				projectHome,
				projectEmail
		};
		
		/*
		 * Format the message "mail.groupInvite.body" with the given parameters.
		 */
		final String messageBody    = messageSource.getMessage("mail.group.decline.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.group.decline.subject", messagesParameters, locale);
		
		/*
		 * send an e-Mail to the group (from our registration Adress)
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
				invitedUser.getName(),
				loginUser.getName(),
				groupName,
				//absoluteURLGenerator.getSettingsUrlWithSelectedTab(3),
				projectHome,
				// TODO: why toLowerCase?
				UrlUtils.safeURIEncode(groupName).toLowerCase(),
				UrlUtils.safeURIEncode(loginUser.getName()).toLowerCase(),
				projectName.toLowerCase(),
				projectEmail,
				absoluteURLGenerator.getSettingsUrlWithSelectedTab(3)
		};
		
		/*
		 * Format the message "mail.groupInvite.body" with the given parameters.
		 */
		final String messageBody    = messageSource.getMessage("mail.groupInvite.body", messagesParameters, locale);
		final String messageSubject = messageSource.getMessage("mail.groupInvite.subject", messagesParameters, locale);

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
			sendPlainMail(recipient,  messageSubject, messageBody, projectRegistrationFromAddress);
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
			this.sendHTMLMail(new String[] { this.projectEmail }, messageSubject, messageBody, this.projectJoinGroupRequestFromAddress);
		} catch (final MessagingException e) {
			log.fatal("Could not send group request mail: " + e.getMessage());
		}
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
	 * The name of the project.
	 * 
	 * @param projectName
	 */
	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	/**
	 * The base URL of the project.
	 * 
	 * @param projectHome
	 */
	public void setProjectHome(final String projectHome) {
		this.projectHome = projectHome;
	}

	/** 
	 * A URL to the blog of the project.
	 * 
	 * @param projectBlog
	 */
	public void setProjectBlog(final String projectBlog) {
		this.projectBlog = projectBlog;
	}

	/**
	 * The email address users can use to contact the project admins. 
	 * 
	 * @param projectEmail
	 */
	public void setProjectEmail(final String projectEmail) {
		this.projectEmail = projectEmail;
	}

	/**
	 * The From: address of registration mails. 
	 * 
	 * @param projectRegistrationFromAddress
	 */
	public void setProjectRegistrationFromAddress(final String projectRegistrationFromAddress) {
		this.projectRegistrationFromAddress = projectRegistrationFromAddress;
	}

	/**
	 * The From: address of join group request mails. 
	 * 
	 * @param projectJoinGroupRequestFromAddress
	 */
	public void setProjectJoinGroupRequestFromAddress(final String projectJoinGroupRequestFromAddress) {
		this.projectJoinGroupRequestFromAddress = projectJoinGroupRequestFromAddress;
	}

	/**
	 * A host which accepts SMTP requests and should be used for sending mails.
	 * 
	 * @param mailHost
	 */
	public void setMailHost(final String mailHost) {
		props.put("mail.smtp.host", mailHost);
	}

	/** A message source to format mail messages.
	 * @param messageSource
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * must be a absolute not relative url generator
	 * 
	 * @param absoluteURLGenerator the absoluteURLGenerator to set
	 */
	public void setAbsoluteURLGenerator(URLGenerator absoluteURLGenerator) {
		this.absoluteURLGenerator = absoluteURLGenerator;
	}

}
