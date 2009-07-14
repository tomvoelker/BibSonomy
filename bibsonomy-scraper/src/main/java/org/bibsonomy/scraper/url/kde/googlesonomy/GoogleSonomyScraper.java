package org.bibsonomy.scraper.url.kde.googlesonomy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * This scraper suports download links from the GoogleSonomy Firefox plugin
 * 
 * @author tst
 * @version $Id$
 */
public class GoogleSonomyScraper extends AbstractUrlScraper {
	
	private static final String INFO = "GoogleSonomy Scraper: This scraper supports download links from the GoogleSonomy Firefox Plugin.";

	private static final String HOST = "scholar.google.";
	
	private static final String PATH = "/scholar.bib";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH + ".*")));
	
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		
		try {
			// get cookie
			String cookie = null;
			cookie = getCookie(sc.getUrl());
			
			if(cookie != null){
				// add :CF=4 to cookie value GSP=ID=
				int index = cookie.indexOf(";", cookie.indexOf("GSP=ID="));
				cookie = cookie.substring(0, index) + ":CF=4" + cookie.substring(index);
				
				// download bibtex
				String bibtex = null;
				bibtex = getContentWithCookie(sc.getUrl(), cookie);
				
				if(bibtex != null){
					// append url
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("bibtex download failed");
				
			}else
				throw new ScrapingFailureException("Cannot get cookie");
			
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
		
	}

	private String getContentWithCookie(URL downloadURL, String cookie) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) downloadURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setRequestProperty("Cookie", cookie);
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */

		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		// connect
		urlConn.connect();

		// read citation
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		
		return out.toString();
	}

	/**
	 * Gets the cookie which is needed to extract the content of pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @param downloadUrl
	 * @return The value of the cookie.
	 * @throws IOException
	 */
	private String getCookie(URL downloadUrl) throws IOException{
		HttpURLConnection urlConn = (HttpURLConnection) downloadUrl.openConnection();

		urlConn.setAllowUserInteraction(true);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setFollowRedirects(true);
		urlConn.setInstanceFollowRedirects(false);
		
		urlConn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();
		
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		
		StringBuffer cookieString = new StringBuffer();
		
		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}
		
		urlConn.disconnect();
		return cookieString.toString();
	}
	
	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	
}
