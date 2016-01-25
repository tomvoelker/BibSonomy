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

import org.apache.commons.logging.Log;
import org.bibsonomy.common.exceptions.InternServerException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Methods to handle captchas.
 *
 * @author rja
 */
public class CaptchaUtil {

	/**
	 * Checks the captcha. If the response from the user does not match the
	 * captcha, an error is added.
	 *
	 * @param captcha
	 * @param errors
	 * @param log
	 * @param challenge
	 * @param response
	 * @param hostInetAddress
	 *            - the address of the client
	 * @throws InternServerException
	 *             - if checking the captcha was not possible due to an
	 *             exception. This could be caused by a non-reachable
	 *             captcha-server.
	 */
	public static void checkCaptcha(final Captcha captcha, final Errors errors,
			final Log log, final String challenge, final String response,
			final String hostInetAddress) throws InternServerException {
		/*
		 * check captcha response
		 */
		try {
			final CaptchaResponse res = captcha.checkAnswer(challenge,
					response, hostInetAddress);

			if (!res.isValid()) {
				/*
				 * invalid response from user
				 */
				/*
				 * check, that challenge response is given
				 */
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"recaptcha_response_field", "error.field.required");
				errors.rejectValue("recaptcha_response_field",
						"error.field.valid.captcha",
						"The provided security token is invalid.");
			} else if (res.getErrorMessage() != null) {
				/*
				 * valid response, but still an error
				 */
				log.warn("Could not validate captcha response: "
						+ res.getErrorMessage());
			}
		} catch (final Exception e) {
			log.fatal("Could not validate captcha response.", e);
			throw new InternServerException("error.captcha");
		}
	}

}
