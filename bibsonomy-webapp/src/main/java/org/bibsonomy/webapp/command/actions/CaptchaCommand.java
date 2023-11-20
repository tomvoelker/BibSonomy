/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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

/**
 * @author dzo
 */
public interface CaptchaCommand {

	/**
	 * @return the recaptcha_challenge_field
	 */
	public String getRecaptcha_challenge_field();

	/**
	 * @param recaptchaChallengeField the recaptcha_challenge_field to set
	 */
	public void setRecaptcha_challenge_field(String recaptchaChallengeField);

	/**
	 * @return the recaptcha_response_field
	 */
	public String getRecaptcha_response_field();

	/**
	 * @param recaptchaResponseField the recaptcha_response_field to set
	 */
	public void setRecaptcha_response_field(String recaptchaResponseField);

	/**
	 * @param captchaHTML
	 */
	public void setCaptchaHTML(String captchaHTML);

	/**
	 * @return captcha html
	 */
	public String getCaptchaHTML();

}