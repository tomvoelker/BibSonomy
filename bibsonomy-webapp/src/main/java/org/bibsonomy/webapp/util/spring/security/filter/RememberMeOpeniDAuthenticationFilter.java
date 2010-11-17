package org.bibsonomy.webapp.util.spring.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.RequestLogic;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * @author dzo
 * @version $Id$
 */
public class RememberMeOpeniDAuthenticationFilter extends OpenIDAuthenticationFilter {
	
	private SecurityContextRepository repo;
	private String projectRoot;
	
	/**
	 * @param projectRoot the projectRoot to set
	 */
	public void setProjectRoot(String projectRoot) {
		this.projectRoot = projectRoot;
	}

	@Override
	protected String obtainUsername(HttpServletRequest req) {
		final RequestLogic requestLogic = new RequestLogic(req);
		final CookieLogic cookieLogic = new CookieLogic();
		cookieLogic.setRequestLogic(requestLogic);
		
		final String openId = cookieLogic.getOpenId();
		if (openId != null) {
			return openId;
		}
		// TODO: throw exception?!
		return super.obtainUsername(req);
	}
	
	@Override
	protected boolean requiresAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
		final RequestLogic requestLogic = new RequestLogic(request);
		final CookieLogic cookieLogic = new CookieLogic();
		cookieLogic.setRequestLogic(requestLogic);
		
		final boolean hasCookie = cookieLogic.hasOpenIDCookie();
		final boolean notLoggedIn = !this.repo.containsContext(request);
		return hasCookie && notLoggedIn; // TODO: already authenticated by the openID provider
	}
	
	@Override
	protected String buildReturnToUrl(HttpServletRequest request) {
		return this.projectRoot + this.getFilterProcessesUrl().replaceFirst("\\/", "");
	}

	/**
	 * @param repo the repo to set
	 */
	public void setRepo(final SecurityContextRepository repo) {
		this.repo = repo;
	}
}
