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
package org.bibsonomy.webapp.util.spring.i18n;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * To fix feature request SPR-9456
 * @see "https://jira.springsource.org/browse/SPR-9456"
 * 
 * @author dzo
 */
public class LocaleChangeInterceptor extends org.springframework.web.servlet.i18n.LocaleChangeInterceptor {
	private static final Log log = LogFactory.getLog(LocaleChangeInterceptor.class);

	private String defaultLocale;
	private List<String> supportedLocales;
	
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws ServletException {

		try {
			String newLocale = request.getParameter(this.getParamName());

			if (newLocale != null) {
				// check, if selected new locale is supported, otherwise use default locale
				if (!this.supportedLocales.contains(newLocale)) {
					newLocale = defaultLocale;
				}

				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				if (localeResolver == null) {
					throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
				}

				localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
			}

			return true;
		} catch (final IllegalArgumentException e) {
			log.info("parameter for lang was not correct.", e);
		}
		return true;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * @param supportedLocales the supportedLocales to set
	 */
	public void setSupportedLocales(List<String> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}
}
