package helpers;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import resources.Resource;

public class Spammer {

	private static Random generator = new Random();
	
	/** Checks, if a request contains a spammer cookie. 
	 * A spammer cookie always contains a "3", otherwise not.
	 * 
	 * @param request to check if cookie contained
	 * 
	 * @return <code>true</code> if cookie contained in request
	 */
	public static boolean hasSpammerCookie (HttpServletRequest request) {
		String cookie = getCookie(request, constants.HTTP_COOKIE_SPAMMER_KEY); 
		return cookie != null && cookie.contains(String.valueOf(constants.HTTP_COOKIE_SPAMMER_CONTAINS));
	}
	
	/** Returns the cookie with the specified name.
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getCookie (HttpServletRequest request, String name) {
		if (request.getCookies() != null) {			
			for (Cookie cookie:request.getCookies()) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	
	/** Sets the spammer Cookie in the response, if the 
	 * request does not already contain it.
	 * 
	 * @param request to check if cookie already contained
	 * @param response to save the cookie
	 */
	public static void addSpammerCookie (HttpServletRequest request, HttpServletResponse response, boolean spammer) {
		/*
		 * build cookie value as first 10 characters of hashed date
		 */
		String value = Resource.hash(new Date().toString()).substring(0, 10);
		/* 
		 * spammers cookies contain always a "3", others never contain a "3" (i.e. HTTP_COOKIE_SPAMMER_CONTAINS) 
		 */
		if (spammer) {
			/* A SPAMMER: make sure, that spammer value is contained */
			int pos = generator.nextInt(value.length());
			value = value.substring(0, pos) + constants.HTTP_COOKIE_SPAMMER_CONTAINS + value.substring(pos + 1, value.length());
		} else {
			/* NOT A SPAMMER: replace spammer value */
			value = value.replace(constants.HTTP_COOKIE_SPAMMER_CONTAINS, '0');
		}
		/*
		 * set cookie
		 */
		Cookie cookie = new Cookie (constants.HTTP_COOKIE_SPAMMER_KEY, value);
		cookie.setPath("/");
		cookie.setMaxAge(3600*24*365);
		response.addCookie(cookie);
	}
	
}
