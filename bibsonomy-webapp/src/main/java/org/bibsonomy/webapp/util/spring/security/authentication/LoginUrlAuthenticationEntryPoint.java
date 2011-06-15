package org.bibsonomy.webapp.util.spring.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.springframework.security.core.AuthenticationException;

/**
 * @author dzo
 * @version $Id$
 */
public class LoginUrlAuthenticationEntryPoint extends org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {

	private static final String NOTICE_PARAM_NAME = "notice";

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
		}
		
		return urlToUse;
	}
}
