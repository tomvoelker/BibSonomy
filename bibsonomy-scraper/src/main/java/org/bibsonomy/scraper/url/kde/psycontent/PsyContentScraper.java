package org.bibsonomy.scraper.url.kde.psycontent;

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
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class PsyContentScraper implements Scraper{

	private static final String info = "PsyContent Scraper: This Scraper parses a publication from http://psycontent.metapress.com/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String PSYCONTENT_HOST_NAME  = "http://psycontent.metapress.com";
	private static final String PSYCONTENT_ABSTRACT_PATH = "/content/";
	private static final String PSYCONTENT_RIS_PATH = "/export.mpx?mode=ris&code=";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		
		if(sc.getUrl() != null) {
			
			String url = sc.getUrl().toString();
			if(url.startsWith(PSYCONTENT_HOST_NAME)) {
				sc.setScraper(this);
				
				//get the ID of the article
				String id = null;
				id = url.substring(url.indexOf(PSYCONTENT_ABSTRACT_PATH) + PSYCONTENT_ABSTRACT_PATH.length(), url.indexOf("/?p="));
				
				//let's see if we got an ID
				if(id != null) {
					
					String downloadLink = PSYCONTENT_HOST_NAME + PSYCONTENT_RIS_PATH + id;
					//Store the cookies in a String
					String cookies = null;
					try {
						cookies = getCookies(sc.getUrl());
					} catch (IOException ex) {
						throw new InternalFailureException("Could not store cookies from " + sc.getUrl());
					}
					
					String risFile = null;
					try {
						risFile = getContent(new URL(downloadLink), cookies); 
					} catch (IOException ex) {
						throw new InternalFailureException("The url "+ downloadLink + " is not valid");
					}
					
					if(risFile != null) {
						//replace all CR+LF to LF
						risFile = risFile.replaceAll("\r\n", "\n");
						
						//convert the ris file to bibtex
						String bibtex = null;
						bibtex = (new RisToBibtexConverter()).RisToBibtex(risFile);
						
						if(bibtex != null) {
							sc.setBibtexResult(bibtex);
							return true;
						} else {
							throw new ScrapingFailureException("Conversion from Ris to bibtex failed");
						}
					} else {
						throw new ScrapingFailureException("Ris download failed. Result is null!");
					}
				} else {
					// missing id
					throw new PageNotSupportedException("ID for donwload link is missing.");
				}
			}
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

}
