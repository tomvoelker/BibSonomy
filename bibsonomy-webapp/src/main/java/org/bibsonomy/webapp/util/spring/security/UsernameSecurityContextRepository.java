package org.bibsonomy.webapp.util.spring.security;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bibsonomy.util.spring.security.RemoteOnlyUserDetails;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.spring.security.authentication.SessionAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

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

	@Deprecated
	private static final String REQ_ATTRIB_USER = "user";

	private static final String ATTRIBUTE_CREDS = "ATTRIBUTE_CREDS";
	
	/**
	 * Delivers details for each given user.
	 */
	private UserDetailsService service;
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	/**
	 * Checks for a user name in the session. If one is found, the corresponding
	 * user details are extracted and the user is stored as request attribute.
	 * 
	 * @see org.springframework.security.web.context.SecurityContextRepository#loadContext(org.springframework.security.web.context.HttpRequestResponseHolder)
	 */
	@Override
	public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
		final HttpServletRequest request = requestResponseHolder.getRequest();
		final SecurityContextImpl securityContext = new SecurityContextImpl();
		SessionAuthenticationToken authentication = null;
		
		final String username = getLoginUser(request);
		if (present(username)) {
			/*
			 * user name found in session -> get the corresponding user
			 */
			final UserDetails user = this.service.loadUserByUsername(username);
			authentication = new SessionAuthenticationToken(user, user.getAuthorities());
			/*
			 * For backwards compatibility, we add the user
			 * as request attribute (used by old servlets and JSPs).
			 * TODO: remove when all old jsp sites are ported to the new spring system
			 */
			request.setAttribute(REQ_ATTRIB_USER, ((UserAdapter)user).getUser());
			
		}
		final Object creds = getSessionAttribute(request, ATTRIBUTE_CREDS);
		if (creds != null) {
			if (authentication == null) {
				authentication = new SessionAuthenticationToken(null, null);
			}
			authentication.setCreds(creds);
			removeSessionAttribute(request, ATTRIBUTE_CREDS);
		}
		if (authentication != null) {
			securityContext.setAuthentication(authentication);
		}
		return securityContext;
	}
	
	
	/**
	 * Stores the user name in the session.
	 * 
	 * @see org.springframework.security.web.context.SecurityContextRepository#saveContext(org.springframework.security.core.context.SecurityContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void saveContext(final SecurityContext context, final HttpServletRequest request, final HttpServletResponse response) {
		this.setLoginUser(request, context.getAuthentication());
	}

	/**
	 * The name of the logged in user is stored in the session. This method 
	 * extracts the name from the session.
	 * 
	 * @param request
	 * @return
	 */
	private static String getLoginUser(final HttpServletRequest request) {
		return (String) getSessionAttribute(request, ATTRIBUTE_LOGIN_USER_NAME);
	}


	private static Object getSessionAttribute(final HttpServletRequest request, String attr) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		
		return session.getAttribute(attr);
	}
	
	private static void removeSessionAttribute(final HttpServletRequest request, String attr) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(attr);
	}
	
	
	
	private void setLoginUser(final HttpServletRequest request, final Authentication authentication) {
		if (this.authenticationTrustResolver.isAnonymous(authentication)) {
			return;
		}
		
		/*
		 * If an authentication is present, we store the user name in the 
		 * session. Note that we /always/ store it - also when it already 
		 * contained in the session (i.e., we don't check for 
		 * !this.containsContext(request)). Thus, the session should time out
		 * after XX minutes of /inactivity/.
		 * 
		 */
		if (present(authentication)) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				final UserDetails user = (UserDetails) principal;
				final String loginUsername = user.getUsername();
				final HttpSession session = request.getSession(true);
				session.setAttribute(ATTRIBUTE_LOGIN_USER_NAME, loginUsername);
				if (principal instanceof RemoteOnlyUserDetails) {
					// this happens in cases when SAML credentials are sent that could not be mapped to a local user
					//  -> remember credentials here so they can be handled after a redirect to the entry controller
					Object creds = authentication.getCredentials();
					if (creds instanceof SAMLCredential) {
						session.setAttribute(ATTRIBUTE_CREDS, creds);
					}
				}
			} else {
				final HttpSession session = request.getSession(false);
				if (session != null) {
					session.removeAttribute(ATTRIBUTE_LOGIN_USER_NAME);
					session.removeAttribute(ATTRIBUTE_CREDS);
				}
			}

			
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
