/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util.spring.security.web.logouthandler;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @author Jens Illig
 */
public class RedirectHandler implements LogoutHandler {
	private static final Log log = LogFactory.getLog(RedirectHandler.class);
	private String parameterName;
	private Map<String, String> redirectUrls;
	
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if ((parameterName == null) || (redirectUrls == null)) {
			return;
		}
		String paramValue = request.getParameter(parameterName);
		if (paramValue == null) {
			return;
		}
		String redirectUrl = redirectUrls.get(paramValue);
		if (redirectUrl == null) {
			return;
		}
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException ex) {
			log.error("cannot redirect to '" + redirectUrl + "'", ex);
			// ok, we better go on instead of throwing an exception
		}
	}


	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return this.parameterName;
	}


	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}


	/**
	 * @return the redirectUrls
	 */
	public Map<String, String> getRedirectUrls() {
		return this.redirectUrls;
	}


	/**
	 * @param redirectUrls the redirectUrls to set
	 */
	public void setRedirectUrls(Map<String, String> redirectUrls) {
		this.redirectUrls = redirectUrls;
	}

	
}
