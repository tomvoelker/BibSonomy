package org.bibsonomy.email;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;

/**
 * Resolves URLs (e.g., gets long version of shortened URLs) 
 * and provides the title of web pages.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class UrlProvider {
	private static final String HTTP_COLON = "://";

	private static final Log log = LogFactory.getLog(UrlProvider.class);

	private HttpClient httpClient;
	
	private static final Pattern TITLE_COMPLETE = Pattern.compile("<title>(.+?)</title>", Pattern.CASE_INSENSITIVE);
	private static final Pattern TITLE_START = Pattern.compile("<title>(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern TITLE_END = Pattern.compile("^(.*)</title>", Pattern.CASE_INSENSITIVE);
	
	public UrlProvider() {
		httpClient = new HttpClient();
	}
	
	/**
	 * Fills url and title of bookmark.
	 * 
	 * @param url
	 * @return
	 */
	public Bookmark resolveUrl(final String url) {
		final Bookmark bookmark = new Bookmark();
		/*
		 * as a default, we set URL and title here in
		 * a very simplistic way ...
		 */
		bookmark.setUrl(url);
		bookmark.setTitle(getEmptyTitle(url));
		/*
		 * try to get the page
		 */
		try {
			final HttpMethod get = new GetMethod(url);
			get.setFollowRedirects(true);
			final int status = httpClient.executeMethod(get);
			if (status == 200) {
				/*
				 * FIXME: is this really the resolved URL?
				 */
				bookmark.setUrl(get.getURI().toString());
				final String title = getTitle(get);
				if (present(title)) bookmark.setTitle(title);
			}
		} catch (HttpException e) {
			log.warn("Could not get content for url " + url);
		} catch (IOException e) {
			log.warn("Could not get content for url " + url);
		}
		bookmark.recalculateHashes();
		return bookmark;
	}
	
	private String getTitle(final HttpMethod method) {
		try {
		final Header contentType = method.getResponseHeader("Content-Type");
		if (present(contentType)) {
			final String value = contentType.getValue();
			if (present(value) && value.startsWith("text/html")) { // FIXME: which types else to support?
				final Header contentEncoding = method.getResponseHeader("Content-Encoding");
				if (present(contentEncoding)) {
					return extractTitle(new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), contentEncoding.getValue())));
				} else {
					return extractTitle(new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream())));	
				}
			}
		}
		} catch (Exception e) {
			// ignore all errors
			log.warn("Could not get title for url", e);
		}
		return null;
	}

	protected String extractTitle(final BufferedReader reader) throws IOException {
		String line;
		final StringBuilder title = new StringBuilder();
		while ((line = reader.readLine()) != null) {
//			System.out.println(line);
			final Matcher completeMatcher = TITLE_COMPLETE.matcher(line);
			if (completeMatcher.find()) {
				return completeMatcher.group(1);
			}
			final Matcher startMatcher = TITLE_START.matcher(line);
			if (startMatcher.find()) {
				title.append(startMatcher.group(1).trim());
				continue;
			}
			final Matcher endMatcher = TITLE_END.matcher(line);
			if (endMatcher.find()) {
				title.append(" " + endMatcher.group(1).trim());
				return title.toString();
			}
			/*
			 * already something found but no "Ende in Sicht"  >  append
			 */
			if (title.length() > 0) {
				title.append(" " + line.trim());
			}
			
		}
		reader.close();
		return title.toString();
	}
	
	/**
	 * If no title could be found (e.g., for non-HTML pages),
	 * we use a part of the URL as title.
	 * 
	 * @param url
	 * @return
	 */
	protected String getEmptyTitle(final String url) {
		final int indexOfColon = url.indexOf(HTTP_COLON);
		if (indexOfColon > 0) {
			final String url2 = url.substring(indexOfColon + HTTP_COLON.length());
			/*
			 * skip everything before colon
			 */
			final int indexOfSlash = url2.indexOf("/");
			if (indexOfSlash > 0 ) {
				/*
				 * skip everything after first slash
				 */
				return url2.substring(0, indexOfSlash);
			}
			return url2;
		}
		return url;
		
	}
	
}
