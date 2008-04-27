package com.atlassian.confluence.extra.webdav.servlet;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Stripped away i18n stuff from the original version...
 * 
 * @author Christian Schenk
 */
public final class WebdavUtil {

	private WebdavUtil() {
	}

	public static String getText(String s) {
		return s;
	}

	public static String getText(String s, String s1) {
		return s;
	}

	public static String getText(String s, Object[] objects) {
		return s;
	}

	@SuppressWarnings("unchecked")
	public static String getText(String s, List list) {
		return s;
	}

	/**
	 * Returns the HTTP status text for the HTTP or WebDav status code specified
	 * by looking it up in the static mapping. This is a static function.
	 * 
	 * @param statusCode
	 *            HTTP or WebDAV status code
	 * @return A string with a short descriptive phrase for the HTTP status code
	 *         (e.g., "OK").
	 */
	public static String getStatusMessage(int statusCode) {
		switch (statusCode) {
		case WebdavResponse.SC_OK:
			return "OK";
		case WebdavResponse.SC_CREATED:
			return "CREATED";
		case WebdavResponse.SC_ACCEPTED:
			return "ACCEPTED";
		case WebdavResponse.SC_NO_CONTENT:
			return "NO_CONTENT";
		case WebdavResponse.SC_MOVED_PERMANENTLY:
			return "MOVED_PERMANENTLY";
		case WebdavResponse.SC_MOVED_TEMPORARILY:
			return "MOVED_TEMPORARILY";
		case WebdavResponse.SC_NOT_MODIFIED:
			return "NOT_MODIFIED";
		case WebdavResponse.SC_BAD_REQUEST:
			return "BAD_REQUEST";
		case WebdavResponse.SC_UNAUTHORIZED:
			return "UNAUTHORIZED";
		case WebdavResponse.SC_FORBIDDEN:
			return "FORBIDDEN";
		case WebdavResponse.SC_NOT_FOUND:
			return "NOT_FOUND";
		case WebdavResponse.SC_INTERNAL_SERVER_ERROR:
			return "INTERNAL_SERVER_ERROR";
		case WebdavResponse.SC_NOT_IMPLEMENTED:
			return "NOT_IMPLEMENTED";
		case WebdavResponse.SC_BAD_GATEWAY:
			return "BAD_GATEWAY";
		case WebdavResponse.SC_SERVICE_UNAVAILABLE:
			return "SERVICE_UNAVAILABLE";
		case WebdavResponse.SC_CONTINUE:
			return "CONTINUE";
		case WebdavResponse.SC_METHOD_NOT_ALLOWED:
			return "METHOD_NOT_ALLOWED";
		case WebdavResponse.SC_CONFLICT:
			return "CONFLICT";
		case WebdavResponse.SC_PRECONDITION_FAILED:
			return "PRECONDITION_FAILED";
		case WebdavResponse.SC_REQUEST_TOO_LONG:
			return "REQUEST_TOO_LONG";
		case WebdavResponse.SC_UNSUPPORTED_MEDIA_TYPE:
			return "UNSUPPORTED_MEDIA_TYPE";
			// WebDav Status Codes
		case WebdavResponse.SC_MULTI_STATUS:
			return "MULTI_STATUS";
		case WebdavResponse.SC_UNPROCESSABLE_ENTITY:
			return "UNPROCESSABLE_ENTITY";
		case WebdavResponse.SC_INSUFFICIENT_SPACE_ON_RESOURCE:
			return "INSUFFICIENT_SPACE_ON_RESOURCE";
		case WebdavResponse.SC_METHOD_FAILURE:
			return "METHOD_FAILURE";
		case WebdavResponse.SC_LOCKED:
			return "LOCKED";
		default:
			return StringUtils.EMPTY;
		}
	}
}