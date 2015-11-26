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
package org.bibsonomy.webapp.util.spring.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.TeerGrube;
import org.bibsonomy.webapp.util.spring.security.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * an userpassword authentication filter with brute force protection
 * @see TeerGrube
 * 
 * @author dzo
 */
public class UsernamePasswordAuthenticationFilter extends org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter {
	private static final Log log = LogFactory.getLog(UsernamePasswordAuthenticationFilter.class);
	
	private TeerGrube grube;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		/*
		 * get username and inetAddress for look up in grube
		 */
		final String username = this.obtainUsername(request);
		final RequestLogic requestLogic = new RequestLogic(request);
		final String inetAddress = requestLogic.getInetAddress();
		
		this.handleWaiting(username, inetAddress);
		
		return super.attemptAuthentication(request, response);
	}
	
	/**
	 * If the user or IP has to wait, this happens here ...
	 * 
	 * @param username
	 * @param inetAddress
	 * @throws ServiceUnavailableException
	 */
	private void handleWaiting(final String username, final String inetAddress) throws ServiceUnavailableException {
		/*
		 * get the number of seconds the user has to wait
		 */
		final long remainingWaitSecondsIP = grube.getRemainingWaitSeconds(inetAddress);
		final long remainingWaitSecondsName = grube.getRemainingWaitSeconds(username);
		/*
		 * take the maximum
		 */
		final long waitingSeconds = (remainingWaitSecondsIP > remainingWaitSecondsName ? remainingWaitSecondsIP : remainingWaitSecondsName);
		/*
		 * check in how many seconds the user is allowed to use this service 
		 */
		if (waitingSeconds > 5) {
			/*
			 * either ip or user name is blocked for more than 5 seconds from now --> log and send error page 
			 */
			log.warn("user " + username + " from IP " + inetAddress + " tried to login but still has to wait for max(" 
					+ remainingWaitSecondsName + ", " + remainingWaitSecondsIP + ") = " + waitingSeconds + " seconds.");

			/* 
			 * send user error message.
			 */
			throw new ServiceUnavailableException("error.service_unavailable", waitingSeconds);
		}
	}
	
	/**
	 * @param grube the grube to set
	 */
	@Required
	public void setGrube(final TeerGrube grube) {
		this.grube = grube;
	}
}
