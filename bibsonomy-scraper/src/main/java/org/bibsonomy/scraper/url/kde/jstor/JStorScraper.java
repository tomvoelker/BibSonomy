package org.bibsonomy.scraper.url.kde.jstor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class JStorScraper implements Scraper {

	private static final String info = "JStor Scraper: This Scraper parses a publication from http://www.jstor.org/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String JSTOR_HOST_NAME  = "http://www.jstor.org";
	private static final String JSTOR_ABSTRACT_PATH = "/pss/";
	private static final String JSTOR_EXPORT_PATH = "/action/exportSingleCitation?singleCitation=true&suffix=";
	private static final String JSTOR_DOWNLOAD_PATH = "/action/downloadSingleCitation?format=bibtex&include=abs&singleCitation=true&noDoi=yesDoi&suffix={id}&downloadFileName={id}";
	
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
			if(url.startsWith(JSTOR_HOST_NAME)) {
				
				String id = null;
				if(url.startsWith(JSTOR_HOST_NAME + JSTOR_ABSTRACT_PATH)) {
					id = url.substring(url.indexOf(JSTOR_ABSTRACT_PATH) + JSTOR_ABSTRACT_PATH.length());
				}
				
				if(url.startsWith(JSTOR_HOST_NAME + JSTOR_EXPORT_PATH)) {
					id = url.substring(url.indexOf("&suffix=") + "&suffix=".length());
				}
				
				if(id != null) {
					String downloadLink = new String(JSTOR_HOST_NAME + JSTOR_DOWNLOAD_PATH.replace("{id}", id));
					
					// get cookies from the server
					String cookies = null;
					try {
						cookies = getCookies(sc.getUrl());
					} catch (IOException ex) {
						throw new InternalFailureException("Failed to download Cookies for " + downloadLink);
					}
					
					//download the bibtex file from the server
					String bibtex = null;
					try {
						bibtex = getContent(new URL(downloadLink), cookies);
					} catch (IOException ex) {
						throw new InternalFailureException("Failed to download the bibtex file.");
					}
					
					if(bibtex != null) {
						//delete the information in the bibtex file
						bibtex = bibtex.replace("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\nJSTOR CITATION LIST\r\n\r\n\r\n", "");
						
						//replace CR+LF with LF
						bibtex = bibtex.replace("\r\n", "\n");
						
						//Convert to UTF-8. Because Server sends a ISO8859-1 encoded string
						try {
							bibtex = new String(bibtex.getBytes("ISO8859-1"), "UTF-8");
						} catch (UnsupportedEncodingException ex) {
							throw new InternalFailureException("Could not convert to UTF-8!");
						}
						
						/*
						 * returns itself to know, which scraper scraped this
						 */
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						
						return true;
					} else {
						
						throw new ScrapingFailureException("Bibtex result is null!");
					}
				} else {
					//missing id
					throw new ScrapingFailureException("ID is missing!");
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
		
		//seems that the application needs this cookie even before getting the other cookies, strange :-\
		urlConn.setRequestProperty("Cookie", "I2KBRCK=1");
		
		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		
		StringBuffer cookieString = new StringBuffer();
		
		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}
		
		//This is neccessary, otherwise we don't get the Bibtex file.
		cookieString.append("I2KBRCK=1");
		
		urlConn.disconnect();
		
		return cookieString.toString();
	}

}
