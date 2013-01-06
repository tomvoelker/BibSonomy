package org.bibsonomy.scraper.url.kde.apa;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author hagen
 * @version $Id$
 */
public class APAScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "American Psychological Association";
	private static final String SITE_URL = "http://www.apa.org/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "psycnet.apa.org"), EMPTY_PATTERN));
	}
	
	private static final Pattern BUY_OPTION_LOCATION_PATTERN = Pattern.compile("fa=buy.*?id=([\\d\\-]++)");
	
	private static final Pattern UIDS_PAGE_PATTERN = Pattern.compile("<input[^>]*?id=\"srhLstUIDs\"[^>]*?value=\"([^\"]++)");

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		
		//Welcome to the story of scraping APA PsycNET
		scrapingContext.setScraper(this);
		
		//We have to proof the visit of several locations
		CookieManager cookieMan = new CookieManager();
		
		//This id is needed to build RIS download link
		String lstUIDs = null;
		
		//While buy action, the id is contained in the URL requested to scrape
		Matcher m = BUY_OPTION_LOCATION_PATTERN.matcher(scrapingContext.getUrl().toExternalForm());
		if (m.find()) {
			
			//Pattern matches requested URL
			lstUIDs = m.group(1);
			
		} else {
			
			//If scraping request is not during buy action, the id is contained in the page requested to scrape
			HttpURLConnection c = null;
			InputStream in = null;
			String page;
			try {
				c = openPageConnection(scrapingContext.getUrl(), cookieMan);
				if (!present(c)) throw new ScrapingException("Could not establish connection to requestet URL");
				in = c.getInputStream();
				page = WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(c.getHeaderField("Content-Type"))).toString();
			} catch (IOException ex) {
				throw new ScrapingException(ex);
			} catch (URISyntaxException ex) {
				throw new ScrapingException(ex);
			} finally {
				try {
					if (in != null) in.close();
				} catch (IOException e) {
				}
				if (c != null) c.disconnect();
			}
			
			//Is the page present?
			if (!present(page)) throw new ScrapingException("Could not get the page requested to scrape");
			
			//Search id in page
			m = UIDS_PAGE_PATTERN.matcher(page);
			if (m.find()) {
				lstUIDs = m.group(1);
			}
		}
		
		//Is the id present?
		if (!present(lstUIDs)) throw new ScrapingException("could not find lstUIDs");
		
		//Build link to RIS download
		URL risURL;
		try {
			risURL = new URL("http://psycnet.apa.org/index.cfm?fa=search.export&id=&lstUids=" + lstUIDs);
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		
		//download RIS exactly two times, because the first request will finally be redirected to a login page
		String ris = null;
		for (int i = 0; i < 2; i++) {
			HttpURLConnection c = null;
			InputStream in = null;
			try {
				c = openPageConnection(risURL, cookieMan);
				if (!present(c)) throw new ScrapingException("Could not establish connection to download URL");
				in = c.getInputStream();
				ris = WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(c.getHeaderField("Content-Type"))).toString();
			} catch (IOException ex) {
				throw new ScrapingException(ex);
			} catch (URISyntaxException ex) {
				throw new ScrapingException(ex);
			} finally {
				try {
					if (in != null) in.close();
				} catch (IOException e) {
				}
				if (c != null) c.disconnect();
			}
			if (ris.contains("Provider: American Psychological Association")) break;
		}
		
		//Convert RIS to BibTeX
		if (!present(ris)) throw new ScrapingException("Could not download citation");
		RisToBibtexConverter converter = new RisToBibtexConverter();
		String bibtex = converter.risToBibtex(ris);
		if (!present(bibtex)) throw new ScrapingException("Something went wrong while converting RIS to BibTeX");
		scrapingContext.setBibtexResult(bibtex);
		
		//success
		return true;
	}
	/**
	 * Neither apache commons HttpClient nor java.net can do this appropriately in a shorthand manner.
	 * 
	 * @param url the location to be connected to originally
	 * @param cookieMan the cookie manager
	 * @return the final, already connected connection that returned response code 200
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static HttpURLConnection openPageConnection(URL url, CookieManager cookieMan) throws IOException, URISyntaxException {
		boolean success = false;
		for (int i = 0; i < 7; i++) {
			HttpURLConnection c = null;
			try {
				c = (HttpURLConnection) url.openConnection();
				c.setInstanceFollowRedirects(false);
				for (Entry<String, List<String>> entry : cookieMan.get(url.toURI(), c.getRequestProperties()).entrySet()) {
					c.addRequestProperty(entry.getKey(), WebUtils.buildCookieString(entry.getValue()));
				}
				c.connect();
				cookieMan.put(url.toURI(), c.getHeaderFields());
				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					success = true;
					return c;
				}
			} finally {
				if (success == false && c != null) c.disconnect();
			}
			String location = c.getHeaderField("Location");
			if (!present(location)) return null;
			url = new URL(url, location);
		}
		return null;
	}

}
