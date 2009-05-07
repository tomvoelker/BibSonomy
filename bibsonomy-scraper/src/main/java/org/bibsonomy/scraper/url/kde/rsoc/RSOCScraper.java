package org.bibsonomy.scraper.url.kde.rsoc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class RSOCScraper extends AbstractUrlScraper {

	private static final String info = "RSOC Scraper: This Scraper parses a journal from " + href("http://royalsocietypublishing.org/", "Royal Society Publishing");

	private static final String HOST  = "royalsocietypublishing.org";
	private static final String PATH  = "/content";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));

	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			String content = WebUtils.getContentAsString(sc.getUrl());
			
			// get link to download page
			Pattern downloadLinkPattern = Pattern.compile("<a href=\\\"([^\\\"]*)\\\">Download to citation manager</a>");
			Matcher downloadLinkMatcher = downloadLinkPattern.matcher(content);
			String downloadLink = null;
			if(downloadLinkMatcher.find()) // add type=bibtex to the end of the link
				downloadLink = "http://" + sc.getUrl().getHost() + downloadLinkMatcher.group(1) + "&type=bibtex";
			else
				throw new ScrapingFailureException("Download link is not available");
			
			// download bibtex directly
			String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if(bibtex != null){
				// clean up (whitespaces in bibtex key)
				String key = bibtex.substring(0, bibtex.indexOf(","));
				String rest = bibtex.substring(bibtex.indexOf(","));
				key = key.replaceAll("\\s", "");
				bibtex = key + rest;
				
				sc.setBibtexResult(bibtex);
				return true;
			}
			
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
		
		return false;
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	private String getContent(URL queryURL, String cookie) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		//insert cookie
		urlConn.setRequestProperty("Cookie", cookie);

		urlConn.connect();

		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();

		return out.toString();
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @return
	 * @throws IOException
	 */
	private String getCookies(URL queryURL) throws IOException {
		HttpURLConnection urlConn = null;

		urlConn = (HttpURLConnection) queryURL.openConnection();

		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);

		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");

		StringBuffer cookieString = new StringBuffer();

		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}

		urlConn.disconnect();

		return cookieString.toString();
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
