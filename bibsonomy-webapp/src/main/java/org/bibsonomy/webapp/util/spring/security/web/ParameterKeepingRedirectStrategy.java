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
package org.bibsonomy.webapp.util.spring.security.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.web.DefaultRedirectStrategy;

/**
 * Adds Parameters from the previous request to the redirect url
 * 
 * @author jensi
 */
public class ParameterKeepingRedirectStrategy extends DefaultRedirectStrategy {
	private Collection<String> parameterNames;
	
	@Override
	public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, String url) throws IOException {
		
		super.sendRedirect(request, new HttpServletResponseWrapper(response) {
			@Override
			public String encodeRedirectURL(String url) {
				StringBuilder sb = new StringBuilder(url);
				char sep = (url.indexOf('?') >= 0) ? '&' : '?';
				for (String parameterName : parameterNames) {
					String param = request.getParameter(parameterName);
					if (param != null) {
						sb.append(sep).append(parameterName).append('=').append(param);
						sep = '&';
					}
				}
				return super.encodeRedirectURL(sb.toString());
			}
		}, url);
	}

	/**
	 * @return the parameterNames
	 */
	public Collection<String> getParameterNames() {
		return this.parameterNames;
	}

	/**
	 * @param parameterNames the parameterNames to set
	 */
	public void setParameterNames(Collection<String> parameterNames) {
		this.parameterNames = parameterNames;
	}
}
