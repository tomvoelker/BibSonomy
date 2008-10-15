package org.bibsonomy.scraper.url.kde.springer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;



public class SpringerLinkScraper implements Scraper {
	private static final String info = "SpringerLink Scraper: This scraper parses a publication page from <a href=\"http://springerlink.com/\">SpringerLink</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";
	private static final String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)";	

	private static final Logger log = Logger.getLogger(SpringerLinkScraper.class);

	private static final String SPRINGER_CITATION_URL = "http://springerlink.com/";
	private static final String SPRINGER_CITATION_URL2= "http://www.springerlink.com/";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && (sc.getUrl().toString().startsWith(SPRINGER_CITATION_URL) || sc.getUrl().toString().startsWith(SPRINGER_CITATION_URL2))) {
			sc.setScraper(this);

			// This Scraper might handle the specified url
			try {
				/*
				 *  guess Springer url
				 */
				String docid = null;
				Pattern p = Pattern.compile("content\\/(.+?)\\/");
				Matcher m = p.matcher(sc.getUrl().toString());
				if (m.find()) {
					docid = m.group(1);
				} else {
					return false;
				}
				
				/* 
				 * create query URL
				 */
				URL queryURL = new URL(("http://springerlink.com/export.mpx?code=" + docid + "&mode=ris"));

				/*
				 * download RIS file
				 */
				String RisResult = getRisFromSpringer(queryURL, getCookieFromSpringer());

				/*
				 * convert ris to bibtex
				 */
				String bibtexEntries = new RisToBibtexConverter().RisToBibtex(RisResult);
				//System.out.println("DEBUG: " + bibtexEntries);

				/*
				 * Job done
				 */
				if (bibtexEntries != null && !"".equals(bibtexEntries)) {
					sc.setBibtexResult(bibtexEntries);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			} catch (IOException e) {
				throw new InternalFailureException(e);
			}
		}
		// This Scraper can`t handle the specified url
		return false;
	}
	
	

	/** Downloads the RIS file for the specified URL from Springer, returns it as a String.
	 * 
	 * @param queryURL
	 * @param cookie needed to download the file.
	 * @return
	 * @throws IOException
	 */
	private String getRisFromSpringer(URL queryURL, String cookie) throws IOException {
		/*
		 * get RIS-file from springer
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
		urlConn.setRequestProperty("User-Agent", userAgent);
		//insert cookie
		urlConn.setRequestProperty("Cookie", cookie);
		urlConn.connect();
		StringWriter out = new StringWriter();
		InputStreamReader in = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		return out.toString();
	}

	
	
	/**
	 * Gets Cookie from SpringerLink and returns it as string.
	 * @return
	 * @throws IOException
	 */
	private String getCookieFromSpringer () throws IOException {
		/*
		 * receive cookie from springer
		 */
		URL mainURL = new URL(SPRINGER_CITATION_URL + "home/main.mpx");
		HttpURLConnection urlConn;
		urlConn = (HttpURLConnection) mainURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", userAgent);
		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookieContent = urlConn.getHeaderFields().get("Set-Cookie");
		//extract sessionID and store in cookie
		for (String crumb : cookieContent) {
			if (crumb.contains("ASP")){
				return crumb;
			}
		}
		urlConn.disconnect();
		return null;
	}
	
	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
