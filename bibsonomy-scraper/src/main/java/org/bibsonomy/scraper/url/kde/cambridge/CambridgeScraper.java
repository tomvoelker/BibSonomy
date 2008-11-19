package org.bibsonomy.scraper.url.kde.cambridge;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class CambridgeScraper implements Scraper, UrlScraper {
	
	private static final String info = "Cambridge Journals Scraper: This Scraper parses a journal from <a href=\"http://journals.cambridge.org/\"> Cambridge Journals</a>. Author: KDE";

	private static final String CAMBRIDGE_HOST  = "journals.cambridge.org";
	private static final String CAMBRIDGE_HOST_NAME  = "http://journals.cambridge.org";
	private static final String CAMBRIDGE_ABSTRACT_PATH = "/action/displayAbstract";
	private static final String CAMBRIDGE_BIBTEX_DOWNLOAD_PATH = "/action/exportCitation?org.apache.struts.taglib.html.TOKEN=51cf342977f2aaa784c6ddfa66c3572c&emailid=&Download=Download&displayAbstract=No&format=BibTex&componentIds=";
	
	private static final String PATTERN_GET_AID_FROM_URL_QUERY = "aid=([^&]*)";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				sc.setScraper(this);
				
				String url = sc.getUrl().toString();
				
				String id = null;
				URL citUrl = null;
				if(url.startsWith(CAMBRIDGE_HOST_NAME + CAMBRIDGE_ABSTRACT_PATH)) {
					Pattern idPattern = Pattern.compile(PATTERN_GET_AID_FROM_URL_QUERY);
					Matcher idMatcher = idPattern.matcher(url);
					if(idMatcher.find())
						id = idMatcher.group(1);
					else
						throw new ScrapingFailureException("No aid found.");
					
					try {
						citUrl = new URL(CAMBRIDGE_HOST_NAME + CAMBRIDGE_BIBTEX_DOWNLOAD_PATH + id);
					} catch (MalformedURLException ex) {
						throw new InternalFailureException(ex);
					}
				}
				
				String bibResult = null;
				try {
					bibResult = getContent(citUrl, getCookie(sc.getUrl()));
				} catch (IOException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

		}
		
		return false;
	}
	
	private String getCookie(URL abstractUrl) throws IOException{
		/*
		 * receive cookie from springer
		 */
		HttpURLConnection urlConn = null;
		
		urlConn = (HttpURLConnection) abstractUrl.openConnection();
		
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
		List<String> cookieContent = urlConn.getHeaderFields().get("Set-Cookie");
		//extract sessionID and store in cookie
		
		StringBuffer cookieString = new StringBuffer();
		
		for(String cookie : cookieContent) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}
		
		urlConn.disconnect();
		
		return cookieString.toString();
	}
	
	private String getContent(URL queryURL, String cookie) throws IOException{
		
		/*
		 * get BibTex-File from ACS
		 */
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

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + CAMBRIDGE_HOST), Pattern.compile(CAMBRIDGE_ABSTRACT_PATH + ".*")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
