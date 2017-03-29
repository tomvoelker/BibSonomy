/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * @author schwass
 */
public class JoinGroupCommand extends BaseCommand implements CaptchaCommand {
	/**
	 * Contains the HTML-Code to view the reCaptcha. Is filled ONLY by the controller!
	 * Any validator must check, that the user did not fill this field.
	 */
	private String captchaHTML;
	/**
	 * The (encoded) challenge the user has to solve. Is given as a request parameter by 
	 * the reCaptcha form.
	 */
	private String recaptcha_challenge_field;
	/**
	 * The response to the captcha, the user entered.
	 */
	private String recaptcha_response_field;
	/**
	 * reason for user to join group
	 */
	private String reason;
	/**
	 * share documents with the group
	 */
	private boolean userSharedDocuments;
	/**
	 * group user want join
	 */
	private String group;
	
	private final int reasonMaxLen;
	
	/**
	 * user to be denied for joining group
	 */
	private String deniedUser;
	
	/**
	 * true if the request comes form join group page
	 */
	private boolean joinRequest;

	/**
	 * @param reasonMaxLen
	 */
	public JoinGroupCommand(final int reasonMaxLen) {
		this.reasonMaxLen = reasonMaxLen;
	}
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getRecaptcha_challenge_field()
	 */
	@Override
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setRecaptcha_challenge_field(java.lang.String)
	 */
	@Override
	public void setRecaptcha_challenge_field(final String recaptchaChallengeField) {
		this.recaptcha_challenge_field = recaptchaChallengeField;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getRecaptcha_response_field()
	 */
	@Override
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setRecaptcha_response_field(java.lang.String)
	 */
	@Override
	public void setRecaptcha_response_field(final String recaptchaResponseField) {
		this.recaptcha_response_field = recaptchaResponseField;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setCaptchaHTML(java.lang.String)
	 */
	@Override
	public void setCaptchaHTML(final String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getCaptchaHTML()
	 */
	@Override
	public String getCaptchaHTML() {
		return captchaHTML;
	}

	/**
	 * @param reason
	 */
	public void setReason(final String reason) {
		this.reason = reason;
	}

	/**
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param group
	 */
	public void setGroup(final String group) {
		this.group = group;
	}
	
	/**
	 * @return userSharedDocuments
	 */
	public boolean isUserSharedDocuments() {
		return this.userSharedDocuments;
	}

	/**
	 * @param userSharedDocuments
	 */
	public void setUserSharedDocuments(boolean userSharedDocuments) {
		this.userSharedDocuments = userSharedDocuments;
	}

	/**
	 * @return group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param deniedUser
	 */
	public void setDeniedUser(final String deniedUser) {
		this.deniedUser = deniedUser;
	}

	/**
	 * @return denied user
	 */
	public String getDeniedUser() {
		return deniedUser;
	}

	/**
	 * @return the reasonMaxLen
	 */
	public int getReasonMaxLen() {
		return reasonMaxLen;
	}


	/**
	 * @return true if the request come from join group page, false if user will see the join group page
	 */
	public boolean isJoinRequest() {
		return joinRequest;
	}


	/**
	 * @param joinRequest the joinRequest to set
	 */
	public void setJoinRequest(boolean joinRequest) {
		this.joinRequest = joinRequest;
	}

}
