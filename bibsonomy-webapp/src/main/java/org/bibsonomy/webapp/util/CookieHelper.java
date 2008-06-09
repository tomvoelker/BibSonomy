package org.bibsonomy.webapp.util;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.Cookie;

import org.bibsonomy.util.StringUtils;

/** Some methods to help handling cookies.
 * 
 * @author rja
 * @version $Id$
 */
public class CookieHelper {

	/**
	 * The name of the cookie which holds the spammer cookie.
	 */
	private static final String HTTP_COOKIE_SPAMMER_KEY = "_lPost";
	/**
	 * The character, which the cookie only contains, if the user is a spammer.
	 */
	private static final char HTTP_COOKIE_SPAMMER_CONTAINS = '3';
	
	/**
	 * Used to generate random cookies.
	 */
	private static Random generator = new Random();
	
	
	/** Checks, if a request contains a spammer cookie. 
	 * A spammer cookie always contains a "3", other cookies not.
	 * 
	 * @param cookies - to check if spammer cookie contained
	 * 
	 * @return <code>true</code> if cookie contained in request
	 */
	public static boolean hasSpammerCookie (final Cookie[] cookies) {
		final String cookie = getCookie(cookies, HTTP_COOKIE_SPAMMER_KEY); 
		return cookie != null && cookie.contains(String.valueOf(HTTP_COOKIE_SPAMMER_CONTAINS));
	}
	
	/** 
	 * Returns the cookie with the specified name.
	 * 
	 * @param cookies 
	 * @param name
	 * @return The value of the named cookie or <code>null</code>, if the cookie could not be found.
	 */
	private static String getCookie (final Cookie[] cookies, final String name) {
		if (cookies != null) {			
			for (final Cookie cookie:cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	
	/** Creates a cookie which indicates, if a user is a spamemr. 
	 * 
	 * @param spammer - a boolean indicating wether the user is a spammer or not.
	 * @return The cookie. 
	 */
	public static Cookie getSpammerCookie (final boolean spammer) {
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
			value = value.substring(0, pos) + HTTP_COOKIE_SPAMMER_CONTAINS + value.substring(pos + 1, value.length());
		} else {
			/* NOT A SPAMMER: replace spammer value */
			value = value.replace(HTTP_COOKIE_SPAMMER_CONTAINS, '0');
		}
		/*
		 * create cookie
		 */
		final Cookie cookie = new Cookie (HTTP_COOKIE_SPAMMER_KEY, value);
		cookie.setPath("/");
		cookie.setMaxAge(3600*24*365);
		return cookie;
	}
}
