/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.util.captcha;

/**
 * class for representing a recaptcha2 response
 *
 * @author niebler
 */
public class ReCaptcha2Response implements CaptchaResponse {
	
	private final boolean isValid;
	private final String errorMessage;
	
	/**
	 * @param isValid
	 * @param errorMessage
	 */
	public ReCaptcha2Response(boolean isValid, String errorMessage) {
		this.isValid = isValid;
		String errorBuilder = "";
		if (errorMessage != null) {
			if (errorMessage.contains("missing-input-secret")) {
				errorBuilder = "The secret parameter is missing. ";
			}
			if (errorMessage.contains("invalid-input-secret")) {
				errorBuilder = "The secret parameter is invalid or malformed. ";
			}
			if (errorMessage.contains("missing-input-response")) {
				errorBuilder = "The response parameter is missing. ";
			}
			if (errorMessage.contains("invalid-input-response")) {
				errorBuilder = "The response parameter is invalid or malformed. ";
			}
			errorBuilder = errorBuilder.trim();
		} else {
			errorBuilder = null;
		}
		this.errorMessage = errorBuilder;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.CaptchaResponse#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.isValid;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.CaptchaResponse#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
