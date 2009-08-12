package filters;

import helpers.database.DBUserManager;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.UserUtils;

import beans.UserBean;

/**
 * This filter changes several settings for users. It does this by reading request 
 * parameters and changing values.
 *
 */
public class SessionSettingsFilter implements Filter {

	private final static Log log = LogFactory.getLog(SessionSettingsFilter.class);
	
    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
	protected FilterConfig filterConfig = null;
	
	/**
	 * Constants to describe Cookie and Bean informations
	 */
	public static final String USER_COOKIE_NAME     = "_currUser";
	public static final String OPENID_COOKIE_NAME	= "_openIDUser";
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
		if (requPath.startsWith(InitUserFilter.STATIC_RESOURCES) || requPath.startsWith(InitUserFilter.API)) {
	        chain.doFilter(request, response);
	        return;
		} 

		
		/*
		 * check user and get user information
		 */
		UserBean user = getUser(httpServletRequest);
		
		

		/*
		 * if there are parameters found which change something (e.g., list style), 
		 * propagate changes to DB/Cookie
		 */
		if (updateBean(httpServletRequest, user)) {
			if (user.getName() != null) {
				// user has been found in DB and is authenticated
				boolean validCKey = ActionValidationFilter.isValidCkey(request);
				if (validCKey) {
					log.info("updating DB");
					DBUserManager.setSettingsForUser(user);
				} else {
					log.info("user is valid, but its ckey is not valid, will not update DB! (hint: someone tried to trick the user?");
				}
			} else {
				// user is not authenticated: use cookie for settings
				log.info("updating cookie");
				setSettingsCookie((HttpServletResponse) response, user);
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
	
	
	/** Puts settings into cookie
	 * @param httpServletRequest
	 * @param user
	 */
	private void setSettingsCookie(HttpServletResponse response, UserBean user) {
		String settings = user.getTagboxStyle() + "," + 
						  user.getTagboxSort() + "," + 
						  user.getTagboxMinfreq() + "," + 
						  user.getTagboxTooltip() + "," +
						  user.getItemcount();
		Cookie settingsCookie = new Cookie (SETTINGS_COOKIE_NAME, settings);
		settingsCookie.setPath("/");
		settingsCookie.setMaxAge(3600*24*365);
		response.addCookie(settingsCookie);
	}


	/** Checks request parameters for updates to tagbox, etc. styles. 
	 * 
	 * @param httpServletRequest
	 * @param user
	 * @return
	 */
	private boolean updateBean(HttpServletRequest httpServletRequest, UserBean user) {
		/*
		 * TODO:
		 * an important point is missing here: avoiding CSRF attacks
		 * to accomplish this:
		 *  - request must be a POST (i.e., from /settings page or via JavaScript)
		 *  - request must contain valid key for the desired method
		 */
		boolean update = false;
		String paramValue = null;

		// style (list, cloud)
		paramValue = httpServletRequest.getParameter("style"); 
		if (paramValue != null) {
			if ("cloud".equals(paramValue)) user.setTagboxStyle(0); 
			else user.setTagboxStyle(1);
			update = true;
		}

		// sort order (alph, freq)
		paramValue = httpServletRequest.getParameter("sort"); 
		if (paramValue != null) {
			if ("alph".equals(paramValue)) user.setTagboxSort(0); // alph
			else user.setTagboxSort(1);                           // freq
			update = true;
		}

		// minmimal freq of tags to show
		paramValue = httpServletRequest.getParameter("minfreq"); 
		if (paramValue != null) {
			
			int minFreq = 0;
			try {
				minFreq = Integer.parseInt(paramValue);
			} catch (NumberFormatException e) {
				
			}
			user.setTagboxMinfreq(minFreq);
			update = true;
		}

		// show tooltip? (yes, no)
		paramValue = httpServletRequest.getParameter("tooltip"); 
		if (paramValue != null) {
			if ("no".equals(paramValue)) user.setTagboxTooltip(0); // no
			else user.setTagboxTooltip(1);                         // yes
			update = true;
		}

		// itemcount
		paramValue = httpServletRequest.getParameter("items"); 
		if (paramValue != null) {
			int itemCount = 10;
			try {
				itemCount = Integer.parseInt(paramValue);
			} catch (NumberFormatException e) {
				
			}
			user.setItemcount(itemCount);
			update = true;
		}
		
		// default language
		paramValue = httpServletRequest.getParameter("lang");
		if (paramValue != null) {
			user.setDefaultLanguage(paramValue);
			httpServletRequest.getSession().setAttribute(InitUserFilter.REQ_ATTRIB_LANGUAGE, new Locale(paramValue));
			update = true;
		}
		
		// Api key
		paramValue = httpServletRequest.getParameter("apikey");
		if (paramValue != null) {
			String apiKey = UserUtils.generateApiKey();
			user.setApiKey(apiKey);
			update = true;
		}
		
		paramValue = httpServletRequest.getParameter("logLevel");
		if (paramValue != null) {
			int logLevel = 0;
			try {
				logLevel = Integer.parseInt(paramValue);
			} catch (NumberFormatException e) {
				
			}
			user.setLogLevel(logLevel);
			update = true;
		}
		
		paramValue =  httpServletRequest.getParameter("confirmDelete");
		if (paramValue != null) {
			
			if("true".equals(paramValue))			
				user.setConfirmDelete("true");
			else
				user.setConfirmDelete("false");
		}
		
		
		return update;
	}

	
	/** Small helper method for Servlets to easily retrieve UserBean.
	 * @param request
	 * @return
	 */
	public static UserBean getUser(HttpServletRequest request) {
		return (UserBean) request.getAttribute(REQ_ATTRIB_USER);
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
