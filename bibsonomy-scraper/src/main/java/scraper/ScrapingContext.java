package scraper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class is used to pass all relevant data from scraper to scraper and 
 * back. Note that a scraper should also be able to cope with 
 * <code>null</code> URLs.
 * 
 */
public class ScrapingContext {

	/**
	 * The URL of the web page where the entries to scrape are.
	 */
	private URL url = null;
	/**
	 * The text the user selected on the web page. Extracted by JavaScript 
	 * method window.getSelection().
	 */
	private String selectedText = null;
	/**
	 * The content of the web page behind url. Will be retrieved 
	 * automatically by @link #getPageContent() if not available.
	 */
	private String pageContent = null;
	/**
	 * The result of the scraping process. A String of one ore more bibtex 
	 * entries. If a scraper is successful bibtexResult must be not null and
	 * must contain one or more valid bibtex entries.
	 */
	private String bibtexResult = null;
	/**
	 * An additional result of the scraper which is stored in the database to 
	 * enhance later scraping results. The format of metaResult is up to the 
	 * scraper and will be stored as is. May be null.
	 */
	private String metaResult = null;
	/**
	 * The scraper which was successful in scraping.
	 */
	private Scraper scraper = null;

	/**
	 * To log errors (particularly when downloading a page for an URL)
	 */
	private static final Logger log = Logger.getLogger(ScrapingContext.class);


	/**
	 * Initially, only the URL is given (and therefore somehow mandatory). 
	 * 
	 * @param url URL of the web page to scrape.
	 */
	public ScrapingContext(URL url) {
		this.url = url;
	}

	/**
	 * Gets the content of the current URL as String.
	 * If the URL is not accessible, we set it the content to "". 
	 * When scrapers see empty content although they need it, they should return "false" anyway. 
	 * 
	 * @return the page content of current url in a string
	 * @throws ScrapingException 
	 */
	public String getPageContent() throws ScrapingException  {
		if (pageContent == null) {
			pageContent = getContentAsString(url);
		}
		return pageContent;
	}

	/**
	 * Reads from an URL and writes the content into a string.
	 * @param url the url to scrape
	 * @return String which holds the page content.
	 * @throws ScrapingException 
	 * @throws Exception  
	 */
	public String getContentAsString(URL url) throws ScrapingException {
		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setAllowUserInteraction(false);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(false);
			urlConn.setUseCaches(false);
			/*
			 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
			 * pages require it to download content.
			 */
			urlConn.setRequestProperty(
					"User-Agent",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
			urlConn.connect();
			
			// extract encoding from header
			String charSet = null;
			String contentType = urlConn.getContentType();
			if(contentType != null){
				int charsetPosition = contentType.indexOf("charset=");
				if(charsetPosition > -1){
					charSet = contentType.substring(charsetPosition + 8);
					
					// get only charset
					int charsetEnding = charSet.indexOf(";");
					if(charsetEnding > -1)
						charSet = contentType.substring(0, charsetEnding);
					// default encoding
				}else
					charSet = "UTF-8";
	
			// default encoding
			}else
				charSet = "UTF-8";
			
			StringWriter out = new StringWriter();
			InputStreamReader in = new InputStreamReader(urlConn.getInputStream(), charSet.trim().toUpperCase());
			int b;
			while ((b = in.read()) >= 0) {
				out.write(b);
			}
			urlConn.disconnect();
			in.close();
			out.flush();
			out.close();
			return out.toString();
		} catch (ConnectException cex) {
			log.fatal("Could not get content for URL " + url.toString() + " : " + cex.getMessage());
			throw new ScrapingException(cex);
		} catch (IOException ioe) {
			log.fatal("Could not get content for URL " + url.toString() + " : " + ioe.getMessage());
			throw new ScrapingException(ioe);
		}
	}

	/**
	 * @return get bibtex entries
	 */
	public String getBibtexResult() {
		return bibtexResult;
	}

	/**
	 * @param bibtexResult save bibtex entries 
	 */
	public void setBibtexResult(String result) {
		this.bibtexResult = result;
	}

	/**
	 * @return current snippet
	 */
	public String getSelectedText() {
		return selectedText;
	}

	/**
	 * @param snippet set snippet sent by user
	 */
	public void setSelectedText(String snippet) {
		if (!"".equals(snippet)) {
			this.selectedText = snippet;
		}
	}

	/**
	 * @return the current url
	 */
	public URL getUrl() {
		return url;
	}

	/** set the URL
	 * @param url
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	public String getMetaResult() {
		return metaResult;
	}

	public void setMetaResult(String metaResult) {
		this.metaResult = metaResult;
	}

	public Scraper getScraper() {
		return scraper;
	}

	public void setScraper(Scraper scraper) {
		this.scraper = scraper;
	}

}
