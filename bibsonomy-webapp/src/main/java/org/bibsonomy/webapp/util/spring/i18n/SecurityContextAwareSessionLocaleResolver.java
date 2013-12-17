package org.bibsonomy.webapp.util.spring.i18n;

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
	
	@Override
	protected Locale determineDefaultLocale(final HttpServletRequest request) {
		/*
		 * check if an user is logged in to use the user's default language
		 */
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if ((principal != null) && (principal instanceof UserAdapter)) {
				final User user = ((UserAdapter) principal).getUser();
				final String lang = user.getSettings().getDefaultLanguage();
				final Locale locale = new Locale(lang);
				/*
				 * save it in the session
				 */
				WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, locale);
				return locale;
			}
		}
		
		/*
		 * else use the default application locale
		 */
		return super.determineDefaultLocale(request);
	}

}
