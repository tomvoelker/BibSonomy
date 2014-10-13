/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.services.information;

import java.util.Locale;

import javax.mail.MessagingException;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.springframework.context.MessageSource;

/**
 * A generic class for mail information using antlr's StringTemplate system
 * 
 * @author dzo
 */
public class MailInformationService implements InformationService {
	private static final Log log = LogFactory.getLog(MailInformationService.class);
	
	/** the logic must be an admin logic */
	protected LogicInterface logic;
	
	private MailUtils mailer;
	
	private MessageSource messageSource;
	private String subjectKey;
	private String templateKey;

	private String fromAddress;
	
	
	@Override
	public void createdPost(String username, Post<? extends Resource> post) {
		final User userToInform = this.logic.getUserDetails(username);
		if (!this.userWantsToBeInformed(userToInform)) {
			return; // user doesn't what to be informed
		}
		final Locale locale = LocaleUtils.toLocale(userToInform.getSettings().getDefaultLanguage());
		final String template = getTemplate(username, locale);
		final StringTemplate stringTemplate = new StringTemplate(template, DefaultTemplateLexer.class);
		this.setAttributes(stringTemplate, userToInform, post);
		
		try {
			this.mailer.sendMail(new String[]{ getMailAddress(userToInform) }, getSubject(locale), stringTemplate.toString(), this.fromAddress);
		} catch (final MessagingException e) {
			log.error("error sending mail message to " + username, e);
		}
	}
	
	/**
	 * the subject of the mail
	 * @param locale
	 * @return the subject for the specified locale
	 */
	protected String getSubject(final Locale locale) {
		return this.messageSource.getMessage(this.subjectKey, null, locale);
	}

	/**
	 * return the template for the specified locale
	 * @param username 
	 * @param locale
	 * @return the template
	 */
	protected String getTemplate(final String username, final Locale locale) {
		return this.messageSource.getMessage(this.templateKey, null, locale);
	}
	
	/**
	 * @param userToInform
	 * @return <code>true</code> iff the user wants to get the mail
	 */
	protected boolean userWantsToBeInformed(final User userToInform) {
		return true;
	}

	/**
	 * @param userToInform
	 * @return the mail address to send the mail to
	 */
	protected String getMailAddress(final User userToInform) {
		return userToInform.getEmail();
	}

	/**
	 * sets the basic informations
	 * @param stringTemplate
	 * @param userToInform
	 * @param post
	 */
	protected void setAttributes(StringTemplate stringTemplate, User userToInform, Post<? extends Resource> post) {
		stringTemplate.setAttribute("reciever", userToInform);
		stringTemplate.setAttribute("post", post);
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	/**
	 * @param subjectKey the subjectKey to set
	 */
	public void setSubjectKey(String subjectKey) {
		this.subjectKey = subjectKey;
	}

	/**
	 * @param templateKey the templateKey to set
	 */
	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}

	/**
	 * @param mailer the mailer to set
	 */
	public void setMailer(MailUtils mailer) {
		this.mailer = mailer;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
}
