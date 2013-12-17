package org.bibsonomy.webapp.util.spring.i18n;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * To fix feature request SPR-9456
 * @see "https://jira.springsource.org/browse/SPR-9456"
 * 
 * @author dzo
 */
public class LocaleChangeInterceptor extends org.springframework.web.servlet.i18n.LocaleChangeInterceptor {
	private static final Log log = LogFactory.getLog(LocaleChangeInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		try {
			return super.preHandle(request, response, handler);
		} catch (final IllegalArgumentException e) {
			log.info("parameter for lang was not correct.", e);
		}
		return true;
	}
}
