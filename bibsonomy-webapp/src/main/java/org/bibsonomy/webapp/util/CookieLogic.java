package org.bibsonomy.webapp.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;

/** Some methods to help handling cookies.
 * 
 * @author rja
 * @version $Id$
 */
public class CookieLogic implements RequestAware, ResponseAware {
	private static final String SPLIT = "%20";

	private static final Log log = LogFactory.getLog(CookieLogic.class);
	
	/**
	 * Used to generate random cookies.
	 */
	private static Random generator = new Random();
	
	
	private RequestLogic requestLogic;
	private ResponseLogic responseLogic;

	/**
	 * The cookie which authenticates the user.
	 */
	private String cookieUser = "_currUser";
	/**
	 * The cookie which authenticates an openID user
	 */
	private final String openIDCookie = "_openIDUser";
	/**
	 * The name of the cookie which holds the spammer cookie.
	 */
	private String cookieSpammer = "_lPost";
	/**
	 * The character, which the cookie only contains, if the user is a spammer.
	 */
	private final char SPAMMER_COOKIE_CONTAINS = '3';
	/**
	 * The age (in seconds) a cookie will stay at most in the browser. Default: One year.  
	 */
	private int cookieAge = 3600 * 24 * 365;
	/**
	 * The path on the server the cookie is valid for. Default: root path. 
	 */
	private String cookiePath = "/";
	
	/**
	 * Default Constructor 
	 */
	public CookieLogic() {
	}
	
	/** Constructor to set request and response logic.
	 * @param requestLogic
	 * @param responseLogic
	 */
	public CookieLogic(RequestLogic requestLogic, ResponseLogic responseLogic) {
		this.requestLogic = requestLogic;
		this.responseLogic = responseLogic;
	}
	
	/** Checks, if a request contains a spammer cookie. 
	 * A spammer cookie always contains a "3", other cookies not.
	 * 
	 * @return <code>true</code> if cookie contained in request
	 */
	public boolean hasSpammerCookie() {
		final String cookie = getCookie(requestLogic.getCookies(), cookieSpammer); 
		return cookie != null && cookie.contains(String.valueOf(SPAMMER_COOKIE_CONTAINS));
	}
	
	/** 
	 * Returns the cookie with the specified name.
	 * 
	 * @param cookies 
	 * @param name
	 * @return The value of the named cookie or <code>null</code>, if the cookie could not be found.
	 */
	private static String getCookie(final Cookie[] cookies, final String name) {
		if (cookies != null) {			
			for (final Cookie cookie:cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	
	/** Adds a cookie which indicates, if a user is a spamemr. 
	 * 
	 * @param spammer - a boolean indicating wether the user is a spammer or not.
	 */
	public void addSpammerCookie(final boolean spammer) {
		/*
		 * build cookie value as first 10 characters of hashed date
		 */
		String value = StringUtils.getMD5Hash(new Date().toString()).substring(0, 10);
		/* 
		 * spammers cookies contain always a "3", others never contain a "3" (i.e. HTTP_COOKIE_SPAMMER_CONTAINS) 
		 */
		if (spammer) {
			/* A SPAMMER: make sure, that spammer value is contained */
			int pos = generator.nextInt(value.length());
			value = value.substring(0, pos) + SPAMMER_COOKIE_CONTAINS + value.substring(pos + 1, value.length());
		} else {
			/* NOT A SPAMMER: replace spammer value */
			value = value.replace(SPAMMER_COOKIE_CONTAINS, '0');
		}
		/*
		 * create cookie
		 */
		addCookie(cookieSpammer, value);
	}
	
	/**
	 * Sets a cookie which authenticates the user.
	 * <br/>
	 * The cookie contains the username and the hashed password - separated by whitespace (%20).
	 * 
	 * @param username - the user's name.
	 * @param passwordHash - the user's password, already MD5-hashed!
	 */
	@Deprecated
	public void addUserCookie(final String username, final String passwordHash) {
		addCookie(cookieUser, encode(username) + SPLIT + passwordHash);
	}
	
	/** Add the openID cookie.
	 * 
	 * @param username
	 * @param openID
	 * @param passwordHash
	 */
	@Deprecated
	public void addOpenIDCookie(final String username, final String openID, final String passwordHash) {
		addCookie(openIDCookie, encode(username) + SPLIT + encode(openID) + SPLIT + passwordHash); 
	}
	
	/** Adds a cookie to the response. Sets default values for path and maxAge. 
	 * 
	 * @param key - The key identifying this cookie.
	 * @param value - The value of the cookie.
	 */
	private void addCookie(final String key, final String value) {
		log.debug("Adding cookie " + key + ": " + value);
		final Cookie cookie = new Cookie(key, value);
		cookie.setPath(cookiePath);
		cookie.setMaxAge(cookieAge);
		responseLogic.addCookie(cookie);
	}
	
	/** Encodes a string with {@link URLEncoder#encode(String, String)} with UTF-8.
	 * TODO: extract method
	 * @param s
	 * @return
	 */
	private static String encode(final String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return s;
		}
	}
	
	/** Checks, if the request contains any cookies.
	 * 
	 * @return <code>true</code>, if the request contains a cookie.
	 */
	public boolean containsCookies() {
		final Cookie[] cookies = requestLogic.getCookies();
		return cookies != null && cookies.length > 0;
	}
		
	/**
	 * The logic to access the HTTP request. Neccessary for getting cookies.
	 *
	 * @param requestLogic
	 */
	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	/**
	 * The logic to access the HTTP response. Neccessary for setting cookies.
	 * @param responseLogic
	 */
	@Override
	public void setResponseLogic(ResponseLogic responseLogic) {
		this.responseLogic = responseLogic;
	}

	/** The cookie which authenticates the user.
	 * @param cookieUser
	 */
	@Deprecated
	public void setCookieUser(String cookieUser) {
		this.cookieUser = cookieUser;
	}

	/** The name of the cookie which holds the spammer cookie.
	 * @param cookieSpammer
	 */
	public void setCookieSpammer(String cookieSpammer) {
		this.cookieSpammer = cookieSpammer;
	}

	/** The age (in seconds) a cookie will stay at most in the browser. Default: One year.
	 * @param cookieAge
	 */
	public void setCookieAge(int cookieAge) {
		this.cookieAge = cookieAge;
	}

	/** The path on the server the cookie is valid for. Default: root path ("/").
	 * @param cookiePath
	 */
	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}
}
