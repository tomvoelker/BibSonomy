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

import java.util.Locale;

/**
 * @author rja
 */
public interface Captcha {

	/** Creates the HTML string which displays the captcha.
	 * 
	 * @param locale - to determine the language for the captcha description.
	 * @return A piece of HTML code rendering the Captcha. 
	 */
	public String createCaptchaHtml(final Locale locale);
	
	/** Checks the response corresponding to the challenge.
	 * 
	 * @param challenge
	 * @param response
	 * @param remoteHostInetAddress
	 * @return A response containing errors and information about the validity.
	 */
	public CaptchaResponse checkAnswer(final String challenge, final String response, final String remoteHostInetAddress);
	
}
