package filters;

import helpers.database.DBUserManager;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

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
	public static final String USER_COOKIE_NAME     = "_currUser";
	public static final String SETTINGS_COOKIE_NAME	= "_styleSettings";
	public static final String REQ_ATTRIB_USER      = "user";

	/**
     * Take this filter out of service.
     */
	public void destroy() {
		this.filterConfig = null;		
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
	
	
	/** 
	 * This method does the main work
	 * 
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
		 * check user and get user information
		 */
		UserBean user = null;
		
		String userCookie = getCookie(httpServletRequest,USER_COOKIE_NAME);

		if (userCookie != null) {
			log.info("found user cookie");
			/* 
			 * user has Cookie set: try to authenticate 
			 */
			String userCookieParts[] = userCookie.split("%20");
			user = DBUserManager.getSettingsForUser(userCookieParts[0], userCookieParts[1]);
		} 
		
		if (user == null) {
			log.info("user not found in DB or user has no cookie set");
			/*
			 * user is not in DB/authenticated properly: get/set values from Cookie 
			 */
			user = new UserBean();
			
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
					user.setTagboxStyle		(Integer.parseInt(settingsCookieParts[0]));
					user.setTagboxSort		(Integer.parseInt(settingsCookieParts[1]));
					user.setTagboxMinfreq	(Integer.parseInt(settingsCookieParts[2]));
					user.setTagboxTooltip	(Integer.parseInt(settingsCookieParts[3]));
					user.setItemcount		(Integer.parseInt(settingsCookieParts[4]));
				}
			}
		}

		/*
		 * put bean into request for following Servlets/JSPs
		 */
		httpServletRequest.setAttribute(REQ_ATTRIB_USER, user);
		log.info("finished: " + user);
		
		// Pass control on to the next filter
        chain.doFilter(request, response);		
        
	}

	/**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;		
	}

}
