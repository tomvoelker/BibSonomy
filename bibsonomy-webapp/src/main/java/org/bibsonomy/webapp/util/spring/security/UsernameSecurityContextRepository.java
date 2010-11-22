package org.bibsonomy.webapp.util.spring.security;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import filters.InitUserFilter;

/**
 * implements {@link SecurityContextRepository}
 * - saves the <code>username</code> of the logged in user in the session attribute {@value #ATTRIBUTE_LOGIN_USER_NAME}
 * - loads the security context by retrieving the username from the session and loading the userdetails from the provided
 * UserdetailsSerivce {@link #service}
 * 
 * @author dzo
 * @version $Id$
 */
public class UsernameSecurityContextRepository implements SecurityContextRepository {
	private static final String ATTRIBUTE_LOGIN_USER_NAME = "LOGIN_USER_NAME";
	
	private static String getLoginUser(final HttpServletRequest request) {
		final HttpSession session = request.getSession();
		if (session == null) {
			return null;
		}
		
		return (String) session.getAttribute(ATTRIBUTE_LOGIN_USER_NAME);
	}
	
	private UserDetailsService service;
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	@Override
	public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
		final HttpServletRequest request = requestResponseHolder.getRequest();
		final SecurityContextImpl securityContext = new SecurityContextImpl();
		
		final String username = getLoginUser(request);
		if (present(username)) {
			final UserDetails user = this.service.loadUserByUsername(username);
			final Authentication authentication = new SessionAuthenticationToken(user, user.getAuthorities());
			securityContext.setAuthentication(authentication);
			
			request.setAttribute(InitUserFilter.REQ_ATTRIB_USER, ((UserAdapter)user).getUser());
		}
		
		return securityContext;
	}

	@Override
	public void saveContext(final SecurityContext context, final HttpServletRequest request, final HttpServletResponse response) {
		this.setLoginUser(request, context.getAuthentication());
	}
	
	private void setLoginUser(final HttpServletRequest request, final Authentication authentication) {
		if (this.authenticationTrustResolver.isAnonymous(authentication)) {
            return;
        }
		
		if (present(authentication) && !this.containsContext(request)) {
			final UserDetails user = (UserDetails) authentication.getPrincipal();
			final String loginUsername = user.getUsername();
			final HttpSession session = request.getSession(true);
			session.setAttribute(ATTRIBUTE_LOGIN_USER_NAME, loginUsername);
		}
	}
	
	@Override
	public boolean containsContext(HttpServletRequest request) {
		return getLoginUser(request) != null;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(UserDetailsService service) {
		this.service = service;
	}

}
