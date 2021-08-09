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
		this.errorMessage = errorMessage;
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
