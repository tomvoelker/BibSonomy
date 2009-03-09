package org.bibsonomy.scraper.url.kde.anthrosource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class AnthroSourceScraper extends AbstractUrlScraper {

	private static final String info = "AnthroSource Scraper: This Scraper parses a publication from " + href("http://www.anthrosource.net/", "anthrosource");

	private static final String AS_HOST  = "anthrosource.net";
	private static final String AS_HOST_NAME  = "http://www.anthrosource.net";
	private static final String AS_ABSTRACT_PATH = "/doi/abs/";
	private static final String AS_BIBTEX_PATH = "/action/showCitFormats";
	private static final String AS_BIBTEX_DOWNLOAD_PATH = "/action/downloadCitation";
	private static final String AS_BIBTEX_PARAMS = "?include=cit&format=bibtex&direct=off&downloadFileName=bs&doi=";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		final Pattern hostPattern = Pattern.compile(".*" + AS_HOST);
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(AS_ABSTRACT_PATH + ".*")));
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(AS_BIBTEX_PATH + ".*")));
	}
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		String id = null;
		URL userURL = null;
		String url = sc.getUrl().toString();

		if(url.startsWith(AS_HOST_NAME + AS_ABSTRACT_PATH)) {
			userURL = sc.getUrl();
			int idStart = url.indexOf(AS_ABSTRACT_PATH) + AS_ABSTRACT_PATH.length();
			int idEnd = 0;

			if(url.contains("?prevSearch=")) {
				//TODO: really "?prevSeach" not "?prevSearch" ?
				idEnd = url.indexOf("?prevSeach=");
			} else {
				idEnd = url.length();
			}

			id = url.substring(idStart, idEnd);

		}

		else if(url.startsWith(AS_HOST_NAME + AS_BIBTEX_PATH)) {

			int idStart = url.indexOf("?doi=") + 5;
			int idEnd = url.length();

			id = url.substring(idStart, idEnd);

			try {
				userURL = new URL(AS_HOST_NAME + AS_ABSTRACT_PATH + id);
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}

		if(id != null){
			sc.setScraper(this);

			id = id.replaceAll("/", "%2F");

			String bibResult = null;

			try {
				URL citURL = new URL(AS_HOST_NAME + AS_BIBTEX_DOWNLOAD_PATH + AS_BIBTEX_PARAMS + id);
				bibResult = getContent(citURL, getCookies(userURL));

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

			if(bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");
		}else
			throw new PageNotSupportedException("Given URL is not supported by this Scraper.");
	}

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
	private String getContent(URL queryURL, String cookie) throws IOException {
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

		//This is neccessary, otherwise we don't get the Bibtex file.
		cookieString.append("I2KBRCK=1");

		urlConn.disconnect();

		return cookieString.toString();
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
