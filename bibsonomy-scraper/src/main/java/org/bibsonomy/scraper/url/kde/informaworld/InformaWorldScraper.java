package org.bibsonomy.scraper.url.kde.informaworld;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import com.sun.net.ssl.SSLContext;

/**
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraper extends AbstractUrlScraper {

	private static final String info = "Informaworld Scraper: This scraper parses a publication from " + href("http://www.informaworld.com/", "informaworld");

	private static final String INFORMAWORLD_HOST_NAME  = "informaworld.com";
	private static final String INFORMAWORLD_ABSTRACT_PATH = "/smpp/content~content=";

	private static final String PATTERN_ID = "content=([^~]*)";

	private static final String INFORMAWORLD_BIBTEX_PATH = "/smpp/content~db=all";
	private static final String INFORMAWORLD_BIBTEX_DOWNLOAD_PATH = "/smpp/content?file.txt&tab=citation&popup=&group=&expanded=&mode=&maction=&backurl=&citstyle=endnote&showabs=false&format=file&toemail=&subject=&fromname=&fromemail=&content={id}&selecteditems={sid}";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + INFORMAWORLD_HOST_NAME), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern pattern = Pattern.compile("content=([^~]*)");

	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String id = null;
		String cookie = null;
		
		try {
			cookie = getCookie(sc.getUrl());
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		if(cookie != null){
			final Matcher matcher = pattern.matcher(sc.getUrl().getPath());
			if(matcher.find())
				id = matcher.group(1);
	
			final String citUrl = "http://www." + INFORMAWORLD_HOST_NAME + (INFORMAWORLD_BIBTEX_DOWNLOAD_PATH.replace("{id}", id)).replace("{sid}", id.substring(1));
	
			try {
				sc.setUrl(new URL(citUrl));
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
			
			final EndnoteToBibtexConverter bib = new EndnoteToBibtexConverter();
			String bibResult = null;
			try {
				bibResult = bib.processEntry(getContent(sc.getUrl(), cookie));
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}
	
			if(bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");
		}else
			throw new ScrapingFailureException("cookie is missing");
	}
	
	private String getCookie(URL abstractUrl) throws IOException{
		HttpURLConnection urlConn = null;

		urlConn = (HttpURLConnection) abstractUrl.openConnection();

		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);

		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		urlConn.connect();

		List<String> cookieContent = urlConn.getHeaderFields().get("Set-Cookie");

		String cookie = "";
		//TODO
		if(cookieContent != null){
			for (String crumb : cookieContent) {
				if(cookie.equals(""))
					cookie = crumb;
				else
					cookie = cookie + ";" + crumb;
			}
		}
		urlConn.disconnect();

		return cookie;
	}

	private String getContent(URL queryURL, String cookie) throws IOException{

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);

		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

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
