package org.bibsonomy.scraper.url.kde.acs;

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
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class ACSScraper extends UrlScraper {

	private static final String info = "ACS Publication Scraper: This Scraper parses a publication from " + href("http://www.acs.org/", "ACS");

	private static final String ACS_HOST_NAME  = "http://pubs.acs.org";
	private static final String ACS_ABSTRACT_PATH = "/cgi-bin/abstract.cgi/";
	private static final String ACS_BIBTEX_PATH = "/wls/journals/citation2/Citation";
	private static final String ACS_BIBTEX_PARAMS = "?format=bibtex&submit=1&includeAbstract=citation&mode=GET";

	private static final Pattern pathPatternAbstract = Pattern.compile(ACS_ABSTRACT_PATH + ".*");
	private static final Pattern pathPatternBibtex = Pattern.compile(ACS_BIBTEX_PATH + ".*");
	
	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		final Pattern hostPattern = Pattern.compile(".*" + "pubs.acs.org");
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, pathPatternBibtex));
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, pathPatternAbstract));
	}

	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		final String path = sc.getUrl().getPath();
		sc.setScraper(this);
		URL citationURL = sc.getUrl();

		if (path.startsWith(ACS_ABSTRACT_PATH)) {
			final String id = path.substring(path.indexOf("/abs/") + 5, path.indexOf(".html"));
			try {
				citationURL = new URL(ACS_HOST_NAME + ACS_BIBTEX_PATH + "?jid=" + id);
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		} 


		String bibResult = null;

		try {
			String cookie = getCookie(citationURL);
			bibResult = getACSContent(new URL(ACS_HOST_NAME + ACS_BIBTEX_PATH + ACS_BIBTEX_PARAMS), cookie);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}				

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
	}

	/** FIXME: refactor
	 * @param abstractUrl
	 * @return
	 * @throws IOException
	 */
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

	/** FIXME: refactor
	 * @param queryURL
	 * @param cookie
	 * @return
	 * @throws IOException
	 */
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

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}


}
