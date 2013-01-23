package org.bibsonomy.webapp.util.spring.security.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.springframework.security.core.AuthenticationException;

/**
 * @author dzo
 * @version $Id$
 */
public class LoginUrlAuthenticationEntryPoint extends org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {

	private static final String NOTICE_PARAM_NAME = "notice";

	/**
	 * @see #getSpecialEntryPointUrls()
	 */
	private Map<String, String> specialEntryPointUrls = new HashMap<String, String>();

	@Override
	protected String determineUrlToUseForThisRequest(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) {
		final String urlToUse = super.determineUrlToUseForThisRequest(request, response, exception);
		
		/*
		 * if the cause of the exception is an AccessDeniedNoticeException
		 * append the notice param of the exception to the url
		 */
		if (exception.getCause() instanceof AccessDeniedNoticeException) {
			final AccessDeniedNoticeException noticeException = (AccessDeniedNoticeException) exception.getCause();
			return UrlUtils.setParam(urlToUse, NOTICE_PARAM_NAME, noticeException.getNotice());
		} /* else if (exception.getCause() instanceof SpecialAuthMethodRequiredException) {
			String url = specialEntryPointUrls.get(exception.getCause().getMessage());
			if (url != null) {
				return url;
			}
		}*/
		
		return urlToUse;
	}

	/**
	 * @return mapping from message values in {@link SpecialAuthMethodRequiredException} to urls that handle the authentication method login.
	 */
	public Map<String, String> getSpecialEntryPointUrls() {
		return this.specialEntryPointUrls;
	}

	/**
	 * @param entryPointUrls
	 * @see #getSpecialEntryPointUrls()
	 */
	public void setSpecialEntryPointUrls(Map<String, String> entryPointUrls) {
		this.specialEntryPointUrls = entryPointUrls;
	}
}
