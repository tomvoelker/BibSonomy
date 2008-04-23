package org.bibsonomy.scraper.url.kde.acs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class ACSScraper implements Scraper {

	private static final String info = "BioMed Central Scraper: This Scraper parses a publication from http://www.biomedcentral.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String ACS_HOST_NAME  = "http://pubs.acs.org";
	private static final String ACS_ABSTRACT_PATH = "/cgi-bin/abstract.cgi/";
	private static final String ACS_BIBTEX_PATH = "/wls/journals/citation2/Citation";
	private static final String ACS_BIBTEX_PARAMS = "?format=bibtex&submit=1&includeAbstract=citation&mode=GET";
	
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
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			String url = sc.getUrl().toString();
			
			if(url.startsWith(ACS_HOST_NAME)) {
				URL citationURL = null;
				
				if(url.startsWith(ACS_HOST_NAME + ACS_ABSTRACT_PATH)) {
					int idStart = url.indexOf("/abs/") + 5;
					int idEnd = url.indexOf(".html");
					String id = url.substring(idStart, idEnd);
					
					try {
						citationURL = new URL(ACS_HOST_NAME + ACS_BIBTEX_PATH + "?jid=" + id);
					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					}
				} 
				
				if (url.startsWith(ACS_HOST_NAME + ACS_BIBTEX_PATH + "?jid=")) {
					try {
						citationURL = new URL(url);
					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					}
				}
				

				String bibResult = null;
				
				try {
					String cookie = getCookie(citationURL);
					bibResult = getACSContent(new URL(ACS_HOST_NAME + ACS_BIBTEX_PATH + ACS_BIBTEX_PARAMS), cookie);
				} catch (IOException ex) {
					ex.printStackTrace();
				}				
				
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
	
					return true;
				}
			}
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
		
		//TODO
		for (String crumb : cookieContent) {
			//System.out.println(crumb);
			if (crumb.contains("JSESSIONID")){
				return crumb;
			}
		}
		urlConn.disconnect();
		
		return null;
	}
	
	private String getACSContent(URL queryURL, String cookie) throws IOException{
		
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
}
