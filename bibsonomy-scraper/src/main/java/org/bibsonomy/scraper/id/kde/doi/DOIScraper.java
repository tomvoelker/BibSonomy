package org.bibsonomy.scraper.id.kde.doi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gives Access to the URL behind a given DOI.
 * @author tst
 * @version $Id$
 */
public class DOIScraper {

	/**
	 * Resolves DOI to a URL
	 * @param doi DOI as String
	 * @return URL from the referenced DOI resource, null if resolve failed
	 * @throws IOException
	 */
	public static URL getUrlForDoi(String doi) throws IOException{
		URL scrapingUrl = null;
		URL doiUrl = new URL("http://dx.doi.org/" + doi);
		
		HttpURLConnection urlConn = null;
		
		urlConn = (HttpURLConnection) doiUrl.openConnection();
		
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setInstanceFollowRedirects(false);
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		
		urlConn.connect();

		// get URL to redirected resource
		String location = urlConn.getHeaderFields().get("Location").get(0).toString();

		urlConn.disconnect();
		
		// build from DOI resolved URL
		scrapingUrl = new URL(location);

		return scrapingUrl;
	}
	
}
