package filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.database.DBLogicUserInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import beans.UserBean;

/**
 * This Filter reads user information/settings from DB or Cookies and makes it available
 * for following filters/servlets/JSPs.
 *
 */
public class InitUserFilter implements Filter {

	public static final String STATIC_RESOURCES = "/resources";

	private final static Logger log = Logger.getLogger(InitUserFilter.class);

	/**
	 * The filter configuration object we are associated with.  If this value
	 * is null, this filter instance is not currently configured.
	 */
	protected FilterConfig filterConfig = null;

	/**
	 * Constants to describe Cookie and Bean informations
	 */
	public static final String USER_COOKIE_NAME     	   = "_currUser";
	public static final String SETTINGS_COOKIE_NAME		   = "_styleSettings";
	public static final String REQ_ATTRIB_USER      	   = "user";
	public static final String REQ_ATTRIB_LANGUAGE         =  SessionLocaleResolver.class.getName() + ".LOCALE";	
	public static final String REQ_ATTRIB_LOGIN_USER       = "loginUser";
	public static final String BIBTEX_NUM_ENTRIES_PER_PAGE = "bibtex.entriesPerPage";
	public static final String BOOKMARK_NUM_ENTRIES_PER_PAGE = "bookmark.entriesPerPage";
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
	 * @param filterConfig The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		/*
		 * if true, we use X.509 certificates instead of passwords in DB for authentication
		 */
		this.useX509forAuth = "true".equals(this.filterConfig.getInitParameter("useX509forAuth"));
	}



	/** 
	 * This method does the main work
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;

		String requPath = httpServletRequest.getServletPath();
		/*
		 * ignore resource files (CSS, JPEG/PNG, JavaScript) ... 
		 */
		if (requPath.startsWith(STATIC_RESOURCES)) {
			chain.doFilter(request, response);
			return;
		} 


		/*
		 * try to authenticate with the given username & password;
		 * if successful, get user details
		 */

		DBLogicUserInterfaceFactory dbLogicFactory = new DBLogicUserInterfaceFactory();
		dbLogicFactory.setDbSessionFactory(new IbatisDBSessionFactory());
		User loginUser = null;
				
		String userCookie = getCookie(httpServletRequest,USER_COOKIE_NAME);
		
		// check user authentication
		try {			
			if (userCookie != null) {
				log.info("found user cookie");
				/* 
				 * user has Cookie set: try to authenticate 
				 */
				String userCookieParts[] = userCookie.split("%20");
				if (userCookieParts.length >= 2) {
					/*
					 * all two parts of cookie available
					 */
					LogicInterface logic = dbLogicFactory.getLogicAccess(userCookieParts[0], userCookieParts[1]);
					loginUser = logic.getUserDetails(userCookieParts[0]);								
				} else {
					/*
					 * something is wrong with the cookie: log!
					 */
					String ip_address = ((HttpServletRequest)request).getHeader("x-forwarded-for");
					log.warn("Someone manipulated the user cookie (IP: " + ip_address + ") : " + userCookie);
				}			
			} else if (useX509forAuth) {
				/*
				 * special handling for SAP X.509 certificates
				 */
				try {
					log.info("no user cookie found, trying X.509");
					/*
					 * get user name from client certificate
					 */
					String uname = getUserName(httpServletRequest);
					/*
					 *  FIXME: here we should put user into DB, if not already contained, i.e., 
					 *  INSERT IGNORE INTO user VALUES ...  
					 */
					try {
						LogicInterface logic = dbLogicFactory.getLogicAccess(uname, "*");
						loginUser = logic.getUserDetails(uname);
					} catch (ValidationException e) {
					}
										
					/*
					 * this should not be neccessary, if we got the user from the database ...
					 */
					if (loginUser == null) {
						loginUser = new User();
						loginUser.setName(uname);
					}
				} catch (Exception e)  {
					log.info("certificate authentication failed: " + e);
				}				
			} else if (HttpServletRequest.BASIC_AUTH.equals(httpServletRequest.getAuthType())) {
				/*
				 * HTTP BASIC AUTHENTICATION
				 */
				log.info("auth via http basic auth");
				
				// get password sent by client in HTTP Authentication Header
				String userpassDecoded = getUserPassFromHTTPBasicAuthHeader(httpServletRequest);
				    
				/*
				 * FIXME: The password is expected to be already MD5-encoded (as in the cookie).
				 * This is typically not the case (i.e., user enters password in browser), but 
				 * we decided to do it this way because we implemented this mechanism exclusively
				 * for integration of publication lists into CMS. There cookie handling is often
				 * difficult and one does not want the cleartext password to be written into the 
				 * CMS (at least in our (eecs) scenario).
				 */
				
				// try to authenticate user
				LogicInterface logic = dbLogicFactory.getLogicAccess(httpServletRequest.getRemoteUser(), userpassDecoded);
				loginUser = logic.getUserDetails(httpServletRequest.getRemoteUser());
			}
		}
		catch (ValidationException valEx) {
			log.info(valEx.getMessage());
		}
		
		if (loginUser == null) {
			log.info("user not found in DB or user has no cookie set");
			/*
			 * user is not in DB/authenticated properly: get/set values from Cookie 
			 */
			loginUser = new User();

			String settingsCookie = getCookie(httpServletRequest, SETTINGS_COOKIE_NAME);
			if (settingsCookie != null) {
				log.info("found settings cookie");
				/*
				 * user has a settings cookie: fill bean from its values
				 */
				String[] settingsCookieParts = settingsCookie.split(",");

				// check for manipulated cookie
				if (settingsCookieParts.length == 5) {
					log.info("settings cookie is valid, using it");
					loginUser.getSettings().setTagboxStyle		(Integer.parseInt(settingsCookieParts[0]));
					loginUser.getSettings().setTagboxSort		(Integer.parseInt(settingsCookieParts[1]));
					loginUser.getSettings().setTagboxMinfreq	(Integer.parseInt(settingsCookieParts[2]));
					loginUser.getSettings().setTagboxTooltip	(Integer.parseInt(settingsCookieParts[3]));
					loginUser.getSettings().setListItemcount	(Integer.parseInt(settingsCookieParts[4]));
				}
			}
		}

		/*
		 * put bean into request for following Servlets/JSPs
		 */
		httpServletRequest.setAttribute(REQ_ATTRIB_LOGIN_USER, loginUser);
		
		
		// set list lengths to default value, if not present
		if (httpServletRequest.getParameter(BOOKMARK_NUM_ENTRIES_PER_PAGE) == null) {
			httpServletRequest.setAttribute(BOOKMARK_NUM_ENTRIES_PER_PAGE, loginUser.getSettings().getListItemcount());
		}
		if (httpServletRequest.getParameter(BIBTEX_NUM_ENTRIES_PER_PAGE) == null) {
			httpServletRequest.setAttribute(BIBTEX_NUM_ENTRIES_PER_PAGE, loginUser.getSettings().getListItemcount());
		}		
		
		/*
		 * for backwards compatibility, we copy here the data from the user object into a
		 * "BibSonomy 1" UserBean Object
		 */				
		// TODO copy contents loginUser -> user
		UserBean userBean = createUserBean(loginUser);
		httpServletRequest.setAttribute(REQ_ATTRIB_USER, userBean);

		// add default language to request if no language is set	
		if (httpServletRequest.getSession().getAttribute(REQ_ATTRIB_LANGUAGE) == null)
			httpServletRequest.getSession().setAttribute(REQ_ATTRIB_LANGUAGE, new Locale(userBean.getDefaultLanguage()));
		
		log.info("finished: " + loginUser);

		// Pass control on to the next filter
		chain.doFilter(request, response);		

	}

	
	/** Returns the value of a cookie with the given name.
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookie (HttpServletRequest request, String cookieName) {
		Cookie cookieList[] = request.getCookies();
		if (cookieList != null) {
			for (Cookie theCookie:cookieList) {
				if (theCookie.getName().equals(cookieName)) {
					return theCookie.getValue();
				}
			}
		}
		return null;
	}


	/** Returns the password contained in a HTTP (Basic) authentication header.
	 * 
	 * @author rja
	 * @param httpServletRequest
	 * @return
	 * @throws IOException
	 */
	private String getUserPassFromHTTPBasicAuthHeader(HttpServletRequest httpServletRequest) {
		// get the user:password from the header
		final String userpassEncoded = httpServletRequest.getHeader("Authentication").substring(6);

		// Decode it, using any base 64 decoder
		final String userpassDecoded = new String (Base64.decodeBase64(userpassEncoded.getBytes()));
		
		// extract password from string
		int p = userpassDecoded.indexOf(":");
		if (p != -1) {
			// password after :
			return userpassDecoded.substring(p+1);
		}
		return null; 
	}



	/* ***************************************************
	 * 
	 * the following methods are X.509 specific
	 * 
	 * written by Torsten Leidig 
	 * 
	 * ***************************************************/




	public static String getUserName(HttpServletRequest request) throws CertificateException {
		return getUserIdFromCertificate(getCert(request));
	}

	public static X509Certificate getCert(HttpServletRequest request) throws CertificateException {
		X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
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
		byte[] decodedBytes = Base64.decodeBase64(certificate.getBytes());
		ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate)cf.generateCertificate(bais);
	}



	public static String getUserIdFromCertificate(X509Certificate cert) {

		if (cert == null) {
			return null;
		}

		final String subjectDN = cert.getSubjectDN().getName();
		return extractUidFromDN(subjectDN);
	}

	public static String extractUidFromDN(String subjectDN) {
		String uid = null;
		StringTokenizer tokenizer = new StringTokenizer(subjectDN, ",");
		if (tokenizer.hasMoreTokens()) {
			uid = tokenizer.nextToken();
		}

		int idx = uid.indexOf('=');
		if (idx < 0) {
			return null;
		}
		uid = uid.substring(idx + 1).trim().toUpperCase();
		return uid.trim();
	}

	private static UserBean createUserBean(User loginUser) {
		
		// general info
		UserBean userBean = new UserBean();
		userBean.setEmail(loginUser.getEmail());
		userBean.setHomepage(loginUser.getHomepage() == null? null : loginUser.getHomepage().toString());
		userBean.setName(loginUser.getName());
		userBean.setOpenurl(loginUser.getOpenURL());
		userBean.setRealname(loginUser.getRealname());
		userBean.setRole(loginUser.getRole());
		userBean.setApiKey(loginUser.getApiKey());
		
		//settings
		userBean.setTagboxMinfreq(loginUser.getSettings().getTagboxMinfreq());
		userBean.setTagboxSort(loginUser.getSettings().getTagboxSort());
		userBean.setTagboxStyle(loginUser.getSettings().getTagboxStyle());
		userBean.setTagboxTooltip(loginUser.getSettings().getTagboxTooltip());
		userBean.setItemcount(loginUser.getSettings().getListItemcount());
		userBean.setDefaultLanguage(loginUser.getSettings().getDefaultLanguage());
		
		//basket size
		userBean.setPostsInBasket(loginUser.getBasket().getNumPosts());
				
		//groups
		for(Group g : loginUser.getGroups()) {
			/* 
			 * Ignore public, private, friends - because we don't want to see 
             * them on the basket page and also not in the "groups" menu. The 
             * UserBean contains a method "getAllGroups" to get those groups, 
             * too.
			 */
			if (!GroupID.isSpecialGroupId(g.getGroupId())) {
				userBean.addGroup(g.getName());
			}
		}
		return userBean;
	}
}