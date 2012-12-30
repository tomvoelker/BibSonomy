package org.bibsonomy.scraper.url.kde.apa;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.DataOutputStream;
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
	
	private static final Pattern EXPORT_LINK_PATTERN = Pattern.compile("<a[^>]*?doActionTaskSingle\\('([^']++)[^>]++>Export");
	private static final Pattern UIDS_PATTERN = Pattern.compile("<input[^>]*?id=\"srhLstUIDs\"[^>]*?value=\"([^\"]++)");
	private static final Pattern GATEWAY_PATTERN = Pattern.compile("<input[^>]*?id=\"idGateway\"[^>]*?value=\"([^\"]++)");
	private static final Pattern ENDNOTE_LINK_PATTERN = Pattern.compile("<a href=\"([^\"]++)[^>]++>Download RIS");

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
		
		//First get the requested page
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
		
		//Get the URL for the download using the URL base of the requested page, since the form to submit for the download is on that page
		URL downloadURL;
		try {
			downloadURL = new URL(c.getURL(), "/index.cfm?fa=search.export");
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		
		//Meanwhile get the "Export" link from the page because we must have visited it
		Matcher m = EXPORT_LINK_PATTERN.matcher(page);
		if (!m.find()) throw new ScrapingException("Export link not found on requested page");
		URL exportURL;
		try {
			exportURL = new URL(c.getURL(), m.group(1));
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		
		//Get the Export page exactly once, although the first visit will be redirected to a login page and just the second try would result in the actual Export page
		String exportPage;
		for (int i = 0; i < 1; i++) {
			try {
				c = openPageConnection(exportURL, cookieMan);
				if (!present(c)) throw new ScrapingException("Could not establish connection to export page URL");
				in = c.getInputStream();
				exportPage = WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(c.getHeaderField("Content-Type"))).toString();
			} catch (MalformedURLException ex) {
				throw new ScrapingException(ex);
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
			if (exportPage.contains("<title>PsycNET - Display</title>")) break;
		}
		
		//Remember the export form being on the original requested page? Get some data from that form
		m = UIDS_PATTERN.matcher(page);
		if (!m.find()) throw new ScrapingException("UIDs not found on requested page");
		String lstUIDs = m.group(1);
		m = GATEWAY_PATTERN.matcher(page);
		if (!m.find()) throw new ScrapingException("Gateway not found on requested page");
		String gateway = m.group(1);
		
		//Submit the export form. In case of Endnote format, we will simply be offered a final download link on another download page, but if we do not want to be redirected to a login page, we must have done the checklist
		String exportResultPage;
		try {
			exportResultPage = submitExportForm(downloadURL, cookieMan, lstUIDs, gateway);
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		} catch (URISyntaxException ex) {
			throw new ScrapingException(ex);
		}
		
		//Now search for the simple download link on the final download page and build that URL
		if (!present(exportResultPage)) throw new ScrapingException("Could get the resulting page of endnote export");
		m = ENDNOTE_LINK_PATTERN.matcher(exportResultPage);
		if (!m.find()) throw new ScrapingException("Could not match link for endnote download");
		URL finalDownloadURL;
		try {
			finalDownloadURL = new URL(downloadURL, m.group(1));
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		
		//Yes, now we can simply download the Endnote file
		c = null;
		in = null;
		String ris;
		try {
			c = openPageConnection(finalDownloadURL, cookieMan);
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
		
		//Convert RIS to BibTeX
		if (!present(ris)) throw new ScrapingException("Could not download citation");
		RisToBibtexConverter converter = new RisToBibtexConverter();
		String bibtex = converter.risToBibtex(ris);
		if (!present(bibtex)) throw new ScrapingException("Something went wrong while converting RIS to BibTeX");
		scrapingContext.setBibtexResult(bibtex);
		
		//What a mess!
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
		for (int i = 0; i < 5; i++) {
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
				if (!success && c != null) c.disconnect();
			}
			String location = c.getHeaderField("Location");
			if (!present(location)) return null;
			url = new URL(url, location);
		}
		return null;
	}
	/**
	 * Neither apache commons HttpClient nor java.net can do this appropriately in a shorthand manner.
	 * 
	 * @param downloadURL the action URL
	 * @param cookieMan the cookie manager
	 * @param lstUIDs corresponding value from the form
	 * @param gateway corresponding value from the form
	 * @return the resulting http message
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static String submitExportForm(URL downloadURL, CookieManager cookieMan, String lstUIDs, String gateway) throws IOException, URISyntaxException {
		HttpURLConnection c = null;
		DataOutputStream out = null;
		InputStream in = null;
		try {
			c = (HttpURLConnection) downloadURL.openConnection();
			c.setInstanceFollowRedirects(false);
			c.setDoOutput(true);
			for (Entry<String, List<String>> entry : cookieMan.get(downloadURL.toURI(), c.getRequestProperties()).entrySet()) {
				c.addRequestProperty(entry.getKey(), WebUtils.buildCookieString(entry.getValue()));
			}
			c.connect();
			out = new DataOutputStream(c.getOutputStream());
			out.writeBytes("id=&lstSelectedUIDs=&lstUIDs=");
			out.writeBytes(lstUIDs);
			out.writeBytes("&records=records&displayFormat=&exportFormat=endnote&printDoc=0&returnURL=&gateway=");
			out.writeBytes(gateway);
			out.flush();
			cookieMan.put(downloadURL.toURI(), c.getHeaderFields());
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = c.getInputStream();
				return WebUtils.inputStreamToStringBuilder(in, WebUtils.extractCharset(c.getHeaderField("Content-Type"))).toString();
			}
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
			}
			try {
				if (in != null) in.close();
			} catch (IOException e) {
			}
			if (c != null) c.disconnect();
		}
		return null;
	}

}
