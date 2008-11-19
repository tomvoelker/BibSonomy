package org.bibsonomy.scraper.url.kde.metapress;

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
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * Scraper for RIS citations from Metapress.com
 * @author tst
 * @version $Id$
 */
public class MetapressScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "Meta Press Scraper: Scraper for publications from <a href=\"http://metapress.com/home/main.mpx\">Meta Press</a>. Author: KDE";
	
	private static final String HOST = "metapress.com";
	
	private static final String PREFIX_DOWNLOAD_URL = "http://www.metapress.com/export.mpx?code=";
	
	private static final String SUFFIX_DOWNLOAD_URL = "&mode=ris";
	
	private static final String PATTERN_URL = "content/([^/]*)/";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
			
			Pattern patternHref = Pattern.compile(PATTERN_URL);
			Matcher matcherHref = patternHref.matcher(sc.getUrl().toString());
			
			if(matcherHref.find()){
				String url = PREFIX_DOWNLOAD_URL + matcherHref.group(1) + SUFFIX_DOWNLOAD_URL;
				
				String ris = null;
				try {
					URL downloadUrl = new URL(url);
					String cookie = getCookies(downloadUrl);
					ris = getContent(downloadUrl, cookie);
					
					if(ris!=null){
						RisToBibtexConverter converter = new RisToBibtexConverter();
						String bibtex = converter.RisToBibtex(ris);
						
						//replace /r with /n
						bibtex = bibtex.replace("\r", "\n");
						
						if(bibtex != null){
							sc.setBibtexResult(bibtex);
							return true;
						}else
							throw new ScrapingFailureException("convert to bibtex failed");
					}else
						throw new ScrapingFailureException("ris download failed");
					
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				} catch (IOException ex) {
					throw new InternalFailureException(ex);
				}
			}else
				throw new PageNotSupportedException("no RIS download available");
			
		}
		
		return false;
	}
	
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
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
