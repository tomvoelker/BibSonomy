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
package org.bibsonomy.webapp.util.spring.i18n;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * @author dzo
 */
public class SecurityContextAwareSessionLocaleResolver extends SessionLocaleResolver {

	private List<String> supportedLocales;
	
	@Override
	protected Locale determineDefaultLocale(final HttpServletRequest request) {
		/*
		 * check if an user is logged in to use the user's default language,
		 * only if we support it
		 */
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal instanceof UserAdapter) {
				final User user = ((UserAdapter) principal).getUser();
				final String lang = user.getSettings().getDefaultLanguage();
				final Locale locale = new Locale(lang);

				if (this.supportedLocales.contains(lang)) {
					// save it in the session, if supported
					WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, locale);
					return locale;
				}
			}
		}
		
		/*
		 * else use the default application locale
		 */
		return super.determineDefaultLocale(request);
	}

	/**
	 * @param supportedLocales the supportedLocale to set
	 */
	public void setSupportedLocale(List<String> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}
}
