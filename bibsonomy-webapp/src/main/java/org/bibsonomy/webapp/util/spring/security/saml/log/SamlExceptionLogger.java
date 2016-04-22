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
package org.bibsonomy.webapp.util.spring.security.saml.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLLogger;

/**
 * Unfortunately the provided SAMLLogger does not log exceptions, so we need this class.
 * 
 * @author Jens Illig
 */
public class SamlExceptionLogger implements SAMLLogger {
	private static final Log log = LogFactory.getLog(SamlExceptionLogger.class);
	private SAMLLogger regularSamlLogger;
	
	
	@Override
	public void log(String operation, String result, SAMLMessageContext context) {
		regularSamlLogger.log(operation, result, context);
	}

	@Override
	public void log(String operation, String result, SAMLMessageContext context, Exception e) {
		if ((e != null) && !SAMLConstants.SUCCESS.equals(result)) {
			log.error("Exception during SAML operation '" + operation + "'", e);
		}
		regularSamlLogger.log(operation, result, context, e);
	}

	@Override
	public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
		if ((e != null) && !SAMLConstants.SUCCESS.equals(result)) {
			log.error("Exception during SAML operation '" + operation + "'", e);
		}
		regularSamlLogger.log(operation, result, context, a, e);
	}

	/**
	 * @param regularSamlLogger the regularSamlLogger to set
	 */
	public void setRegularSamlLogger(SAMLLogger regularSamlLogger) {
		this.regularSamlLogger = regularSamlLogger;
	}
}
