package filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.auth.Ldap;
import org.bibsonomy.webapp.util.auth.LdapUserinfo;
import org.bibsonomy.webapp.util.auth.OpenID;
import org.bibsonomy.webapp.util.auth.OpenIdConsumerManager;
import org.bibsonomy.webapp.util.spring.factorybeans.AdminLogicFactoryBean;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import servlets.listeners.InitialConfigListener;

/**
 * This Filter reads user information/settings from DB or Cookies and makes it
 * available for following filters/servlets/JSPs.
 * 
 */
public class InitUserFilter implements Filter {
	private final static Log log = LogFactory.getLog(InitUserFilter.class);
	
	/*
	 * All X.509 users get the same password in the database, since it is never
	 * used for authentication.
	 */
	private static final String X509_GENERIC_PASSWORD = "*";
	
	/**
	 * the resources (css, js) path
	 */
	public static final String STATIC_RESOURCES = "/resources";
	
	/**
	 * the api path
	 */
	public static final String API = "/api";

	/**
	 * The filter configuration object we are associated with. If this value is
	 * null, this filter instance is not currently configured.
	 */
	protected FilterConfig filterConfig = null;

	/**
	 * OpenID authentication functionality
	 */
	protected OpenID openIDLogic = null;
	protected OpenIdConsumerManager manager = null;

	/**
	 * Constants to describe Cookie and Bean informations
	 */
	public static final String USER_COOKIE_NAME = "_currUser";
	
	/**
	 * TODO: improve documentation
	 */
	public static final String OPENID_COOKIE_NAME = "_openIDUser";
	
	/**
	 * TODO: improve documentation
	 */
	public static final String REQ_ATTRIB_USER = "user";
	
	/**
	 * TODO: improve documentation
	 */
	public static final String REQ_ATTRIB_LANGUAGE = SessionLocaleResolver.class.getName() + ".LOCALE";
	
	/**
	 * Name of the Attribute that stores the default language in the servers context.xml 
	 */
	public static final String PROJECT_DEFAULT_LANGUAGE = "project.defaultLocale";
	
	/**
	 * TODO: improve documentation
	 */
	public static final String REQ_ATTRIB_LOGIN_USER = "loginUser";
	
	/**
	 * TODO: improve documentation
	 */
	public static final String REQ_ATTRIB_LOGIN_USER_PASSWORD = "loginUserPassword";
	
	/**
	 * Enables X.509 authentication.
	 */
	public static boolean useX509forAuth = false;

	/**
	 * Take this filter out of service.
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		/*
		 * if true, we use X.509 certificates instead of passwords in DB for
		 * authentication
		 */
		useX509forAuth = "true".equals(this.filterConfig.getInitParameter("useX509forAuth"));
		try {
			this.manager = new OpenIdConsumerManager();
		} catch (ConsumerException ex) {
			log.error("Could not initialize OpenID Consumer Manager.", ex);
		}
		this.openIDLogic = new OpenID();
		this.openIDLogic.setManager(manager);
	}

	/**
	 * This method does the main work
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		final String requPath = httpServletRequest.getServletPath();
		/*
		 * ignore resource files (CSS, JPEG/PNG, JavaScript) ...
		 */
		if (requPath.startsWith(STATIC_RESOURCES) || requPath.startsWith(API)) {
			
			//workaround that prevent the the 404 error to break because of no loginUser is set
			//if user requests a site that doesn't exists
			request.setAttribute(REQ_ATTRIB_LOGIN_USER, new User());
			
			chain.doFilter(request, response);
			return;
		}

		/*
		 * try to authenticate with the given username & password; if
		 * successful, get user details
		 */

		final DBLogicUserInterfaceFactory dbLogicFactory = new DBLogicUserInterfaceFactory();
		final IbatisDBSessionFactory ibatisDBSessionFactory = new IbatisDBSessionFactory();
		dbLogicFactory.setDbSessionFactory(ibatisDBSessionFactory);
		User loginUser = null;

		final String userCookie = getCookie(httpServletRequest, USER_COOKIE_NAME);
		final String openIDCookie = getCookie(httpServletRequest, OPENID_COOKIE_NAME);

		// check user authentication
		try {
			/*
			 * X.509 comes first, such that cookies can't override it ... (and
			 * we can use blanco passwords in the database)
			 */
			if (useX509forAuth) {
				/*
				 * special handling for SAP X.509 certificates
				 */
				try {
					log.debug("no user cookie found, trying X.509");
					/*
					 * get user name from client certificate
					 */
					final String uname = getUserName(httpServletRequest);
					/*
					 */
					try {
						/*
						 * We use a fixed ("*") empty password - since auth is 
						 * done using certificates. Since X.509 auth comes first,
						 * nobody can use a Cookie to be another user.
						 */
						final LogicInterface logic = dbLogicFactory.getLogicAccess(uname, X509_GENERIC_PASSWORD);
						loginUser = logic.getUserDetails(uname);
					} catch (AccessDeniedException e) {
						loginUser = createX509User(ibatisDBSessionFactory, uname);
					}

					/*
					 * this should not be neccessary, if we got the user from
					 * the database ...
					 */
					if (loginUser == null) {
						loginUser = new User();
						loginUser.setName(uname);
					}
				} catch (final Exception e) {
					log.debug("Certificate authentication failed.", e);
				}
			} else if (userCookie != null) {
				log.debug("found user cookie");
				/*
				 * user has Cookie set: try to authenticate
				 */
				final String userCookieParts[] = userCookie.split("%20");
				
				if (userCookieParts.length >= 2) {
					final String userName = decode(userCookieParts[0]);
					/*
					 * all two parts of cookie available
					 */
					final String userPass = userCookieParts[1];
					/*
					 * check if user is listed in table ldapUser
					 * if so check if it is required to authenticate again against ldap-server
					 * if not use standard login method
					 */

					LogicInterface logic = null;

					logic = dbLogicFactory.getLogicAccess(userName, userPass);
					loginUser = logic.getUserDetails(userName);

						
					/* 
					 * check, if it is an ldap user and if it has to re-auth agains ldap server. if so, do it.
					 */
					// if user database authentication was successful
					// check if user is listed in ldapUser table
					if (null != loginUser.getLdapId())
					{
					
						// get date of last authentication against ldap server
						Date userLastAccess = loginUser.getLastLdapUpdate();
						
						// TODO: get timeToReAuth from tomcat's environment, so a user can adjust it without editing code  
						int timeToReAuth =  18  *60*60; // seconds
						Date dateNow = new Date();
						// timeDiff is in seconds
						long timeDiff = (dateNow.getTime() - userLastAccess.getTime())/1000;						
						
						log.debug("last access of user "+userName+" was on "+userLastAccess.toString()+ " ("+(timeDiff/3600)+" hours ago = "+ " ("+(timeDiff/60)+" minutes ago = "+timeDiff+" seconds ago)");
					
						/*
						 *  check lastAccess - re-auth required?
						 *  if time of last access is too far away, re-authenticate against ldap server to check
						 *  whether password is same or user exists anymore
						 */
						
						if ( timeDiff > timeToReAuth ) {
							// re-auth
							log.debug("last access time is up - ldap re-auth required -> throw reauthrequiredException");
							
							/*
							 * check credentials against ldap server
							 * if login is not correct redirect to login page
							 * if it is correct use standard login method 
							 */
							Ldap ldap = new Ldap();
							LdapUserinfo ldapUserinfo = new LdapUserinfo();
							log.debug("loginUser = " + loginUser.getName());
							log.debug("Trying to re-auth user " + userName + " via LDAP (uid="+loginUser.getLdapId()+")");
					        ldapUserinfo = ldap.checkauth(loginUser.getLdapId(), userPass);
					        
							if (null == ldapUserinfo)
							{
								/*
								 * user credentials do not match --> show error message
								 * and go to login page
								 */
								log.debug("ra-auth of user " + userName + " failed.");
								loginUser = null;
							} else {
			
								log.debug("ra-auth of user " + userName + " succeeded.");
			
								loginUser = logic.getUserDetails(userName);
		
								// if ldap credentials are ok, update lastAccessTimestamp
								if (null != logic.updateUser(loginUser, UserUpdateOperation.UPDATE_LDAP_TIMESTAMP)) {
									log.debug("update LDAP-Timestamp of user " + userName + " successful");
								} else {
									log.error("error on updating LDAP-Timestamp of user " + userName + "!");
								}
							}
							
						}
					}
				} else {
					/*
					 * something is wrong with the cookie: log!
					 */
					String ip_address = ((HttpServletRequest) request).getHeader("x-forwarded-for");
					log.warn("Someone manipulated the user cookie (IP: " + ip_address + ") : " + userCookie);
				}
			} else if (openIDCookie != null) {
				log.debug("found OpenID cookie");

				/*
				 * user has OpenID cookie set
				 */
				final String openIdCookieParts[] = openIDCookie.split("%20");
				if (openIdCookieParts.length >= 3) {
					final String userName = decode(openIdCookieParts[0]);
					final String openID = decode(openIdCookieParts[1]);
					final String password = openIdCookieParts[2];

					final HttpSession session = httpServletRequest.getSession(true);

					final RequestLogic requestLogic = new RequestLogic(httpServletRequest);

					final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					final ResponseLogic responseLogic = new ResponseLogic(httpServletResponse);
					final CookieLogic cookieLogic = new CookieLogic(requestLogic, responseLogic);

					/*
					 * check if cookie is still valid
					 */
					String openIDSession = (String) session.getAttribute(OpenID.OPENID_SESSION_ATTRIBUTE);

					if (openIDSession != null) {
						/*
						 * valid OpenID session
						 */
						LogicInterface logic = dbLogicFactory.getLogicAccess(userName, password);
						loginUser = logic.getUserDetails(userName);
					} else {
						/*
						 * OpenID session expired --> reauthenticate at OpenID
						 * provider
						 */
						StringBuffer referer = httpServletRequest.getRequestURL();
						String queryString = requestLogic.getParametersAsQueryString();

						if (queryString != null && queryString.length() > 1) referer.append(queryString);

						String returnTo = InitialConfigListener.getProjectHome() + "login?referer=" + URLEncoder.encode(referer.toString(), "UTF-8");

						/*
						 * delete old openid cookie
						 */
						cookieLogic.deleteOpenIDCookie();

						/*
						 * redirect user to openID provider
						 */
						String url;
						try {
							url = openIDLogic.authOpenIdRequest(requestLogic, openID, InitialConfigListener.getProjectHome(), returnTo.toString(), false);
							httpServletResponse.sendRedirect(url);
						} catch (OpenIDException ex) {
							log.error("OpenID provider url not valid");
							ex.printStackTrace();
						}
						return;
					}
				} else {
					/*
					 * something is wrong with the cookie: log!
					 */
					String ip_address = ((HttpServletRequest) request).getHeader("x-forwarded-for");
					log.warn("Someone manipulated the openid cookie (IP: " + ip_address + ") : " + openIDCookie);
				}
			} else {
				final String[] auth = decodeAuthHeader(httpServletRequest);
				if (auth != null && auth.length == 2) {
					/*
					 * HTTP BASIC AUTHENTICATION
					 */
					log.debug("Authentication for user '" + auth[0] + "' using HTTP basic auth.");

					/*
					 * FIXME: The password is expected to be already MD5-encoded
					 * (as in the cookie). This is typically not the case (i.e.,
					 * user enters password in browser), but we decided to do it
					 * this way because we implemented this mechanism
					 * exclusively for integration of publication lists into
					 * CMS. There cookie handling is often difficult and one
					 * does not want the cleartext password to be written into
					 * the CMS (at least in our (eecs) scenario).
					 */

					// try to authenticate user
					final LogicInterface logic = dbLogicFactory.getLogicAccess(auth[0], auth[1]);
					loginUser = logic.getUserDetails(auth[0]);
				}
			}
		} catch (AccessDeniedException valEx) {
			log.debug("Login failed.", valEx);
		}

		if (loginUser == null) {
			log.debug("user not found in DB or user has no cookie set");
			/*
			 * user is not in DB/authenticated properly: get/set values from
			 * Cookie
			 */
			loginUser = new User();
		}

		/*
		 * put bean into request for following Servlets/JSPs
		 */
		httpServletRequest.setAttribute(REQ_ATTRIB_LOGIN_USER, loginUser);

		/*
		 * For backwards compatibility, we add the user
		 * as request attribute (used by old servlets and JSPs).
		 */
		httpServletRequest.setAttribute(REQ_ATTRIB_USER, loginUser);

		// add default language to request if no language is set
		if (httpServletRequest.getSession().getAttribute(REQ_ATTRIB_LANGUAGE) == null) {
			if(loginUser.getSettings().getDefaultLanguage() == null) {
				httpServletRequest.getSession().setAttribute(REQ_ATTRIB_LANGUAGE, new Locale((String) httpServletRequest.getSession().getServletContext().getAttribute(PROJECT_DEFAULT_LANGUAGE)));
			} else {
				httpServletRequest.getSession().setAttribute(REQ_ATTRIB_LANGUAGE, new Locale(loginUser.getSettings().getDefaultLanguage()));

			}
			
		//if user changed language in /settings change the language to the new requested language
		} else {
			final String lang = loginUser.getSettings().getDefaultLanguage();
			final Locale sessionLang = (Locale) httpServletRequest.getSession().getAttribute(REQ_ATTRIB_LANGUAGE);
			if( lang != null && !sessionLang.getLanguage().equals(new Locale(lang))) {
				httpServletRequest.getSession().setAttribute(REQ_ATTRIB_LANGUAGE, new Locale(lang));
			}
		}

		log.debug("finished: " + loginUser);

		// Pass control on to the next filter
		chain.doFilter(request, response);
	}

	/**
	 * Creates a user in the database for X.509.
	 * 
	 * The password is set to {@link #X509_GENERIC_PASSWORD}, the user name to
	 * the given user name.
	 * 
	 * @param ibatisDBSessionFactory
	 * @param uname
	 * @return
	 * @throws Exception
	 */
	private User createX509User(final IbatisDBSessionFactory ibatisDBSessionFactory, final String uname) throws Exception {
		User loginUser;
		/*
		 * user not found in DB: create new user
		 * 
		 * TODO: use data from certificate
		 */
		loginUser = new User(uname);
		loginUser.setPassword(X509_GENERIC_PASSWORD);
		/*
		 * get admin DB access
		 */
		final AdminLogicFactoryBean adminLogicFactory = new AdminLogicFactoryBean();
		adminLogicFactory.setAdminUserName("admin");
		adminLogicFactory.setDbSessionFactory(ibatisDBSessionFactory);
		final LogicInterface adminLogic = (LogicInterface) adminLogicFactory.getObject();
		/*
		 * finally: create user
		 */
		adminLogic.createUser(loginUser);
		return loginUser;
	}

	/**
	 * Returns the value of a cookie with the given name.
	 * 
	 * @param request
	 * @param cookieName
	 * @return the value of a cookie with the given name
	 */
	public static String getCookie(HttpServletRequest request, String cookieName) {
		Cookie cookieList[] = request.getCookies();
		if (cookieList != null) {
			for (Cookie theCookie : cookieList) {
				if (theCookie.getName().equals(cookieName)) {
					return theCookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Extracts the auth header, decodes it and returns an array containing at
	 * position 0 the user name and at position 1 the user password.
	 * 
	 * @author rja
	 * @return <code>new String[]{username, password}</code> or
	 *         <code>null</code>, if no auth header found.
	 * 
	 * @throws IOException
	 */
	private String[] decodeAuthHeader(final HttpServletRequest request) {
		final String authHeader = request.getHeader("authorization");
		if (authHeader != null) {
			// Decode it, using any base 64 decoder
			final String userpassDecoded = new String(Base64.decodeBase64(authHeader.split("\\s+")[1].getBytes()));
			// split user name and password
			return userpassDecoded.split(":");
		}
		return null;
	}

	/*
	 * ***************************************************
	 * 
	 * the following methods are X.509 specific
	 * 
	 * written by Torsten Leidig
	 * 
	 * **************************************************
	 */

	/**
	 * Small helper method for Servlets to easily retrieve UserBean.
	 * @param request
	 * @return the user
	 */
	public static User getUser(HttpServletRequest request) {
		return (User) request.getAttribute(REQ_ATTRIB_USER);
	}

	private static String getUserName(HttpServletRequest request) throws CertificateException {
		return getUserIdFromCertificate(getCert(request));
	}

	private static X509Certificate getCert(HttpServletRequest request) throws CertificateException {
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (certs != null) {
			return certs[0]; // on index 0 shall be the client cert
		}

		// get cert from IIS reverse proxy send in request header
		return decodeIisCertificate(request.getHeader("SSL_CLIENT_CERT"));
	}

	private static X509Certificate decodeIisCertificate(String certificate) throws CertificateException {
		if (certificate == null) {
			return null; // no cert from IisProxy
		}
		final byte[] decodedBytes = Base64.decodeBase64(certificate.getBytes());
		final ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate) cf.generateCertificate(bais);
	}

	private static String getUserIdFromCertificate(final X509Certificate cert) {

		if (cert == null) {
			return null;
		}
		/*
		 * create new user
		 */
		final String subjectDN = cert.getSubjectX500Principal().getName();
		return extractUidFromDN(subjectDN);
	}

	private static String extractUidFromDN(final String subjectDN) {
		String uid = null;
		final StringTokenizer tokenizer = new StringTokenizer(subjectDN, ",");
		if (tokenizer.hasMoreTokens()) {
			uid = tokenizer.nextToken();
		}

		int idx = uid.indexOf('=');
		if (idx < 0) {
			return null;
		}
		return uid.substring(idx + 1).trim().toUpperCase().trim();
	}


	/**
	 * TODO: move to UrlUtils!!
	 * 
	 * Decodes a string with {@link URLDecoder#decode(String, String)} with
	 * UTF-8.
	 * 
	 * @param s
	 * @return
	 */
	private static String decode(final String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return s;
		}
	}

}