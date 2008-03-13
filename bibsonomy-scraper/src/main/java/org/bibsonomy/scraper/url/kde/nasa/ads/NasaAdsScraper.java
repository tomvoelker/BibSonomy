package org.bibsonomy.scraper.url.kde.nasa.ads;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;


/**
 * Scarper for NASA ADS.
 * It collects bibtex snippets and single referenzes (html page or bibtex page).  
 * @author tst
 */
public class NasaAdsScraper implements Scraper {
	
	private static final String INFO = "NasaAdsScraper: Extracts publications from http://adsabs.harvard.edu/ . Publications can be entered as a marked bibtex snippet (one or more publications) or by the page of a single reference.";
	
	/*
	 * host of nasa ads
	 */
	private static final String URL_NASA_ADS_HOST = "adsabs.harvard.edu";
	
	/*
	 * supported content types
	 */
	private static final String NASA_ADS_CONTENT_TYPE_PLAIN = "text/plain";
	
	private static final String NASA_ADS_CONTENT_TYPE_HTML = "text/html";
	
	/*
	 * description text from a bibtex link
	 */
	private static final String BIBTEX_LINK_VALUE = "Bibtex entry for this abstract";
	
	/*
	 * pattern for link and its href
	 */
	private static final String PATTERN_LINK = "<a(.*)</a>";
	
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * This scraper collects bibtex snippets and single referenzes (html page or bibtex page).
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(URL_NASA_ADS_HOST)){
		
			/*
			 * check of snippet
			 */
			if(sc.getSelectedText() != null){
				sc.setBibtexResult(sc.getSelectedText());
				sc.setScraper(this);
				return true;
				
			/*
			 * no snippet, check content from url
			 */
			}else{
				
				/*
				 * changed code from ScrapingContext.getContentAsString
				 * the desicion what do depends on the type of the content 
				 */
				HttpURLConnection urlConn = null;
				try {
					urlConn = (HttpURLConnection) sc.getUrl().openConnection();
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
					StringWriter out = new StringWriter();
					InputStream in = new BufferedInputStream(urlConn.getInputStream());
					int b;
					while ((b = in.read()) >= 0) {
						out.write(b);
					}
					urlConn.disconnect();
					in.close();
					out.flush();
					out.close();
					String nasaAdsContent = out.toString();
					
					/*
					 * if bibtex content, then use this content as snippet
					 */
					if(urlConn.getContentType().startsWith(NASA_ADS_CONTENT_TYPE_PLAIN)){
						
						sc.setBibtexResult(nasaAdsContent);
						sc.setScraper(this);
						return true;
						
					/*
					 * if html content, search link to bibtex content
					 */
					}else if(urlConn.getContentType().startsWith(NASA_ADS_CONTENT_TYPE_HTML)){
						
						Pattern linkPattern = Pattern.compile(PATTERN_LINK);
						Matcher linkMatcher = linkPattern.matcher(nasaAdsContent);
						
						/*
						 * check all links
						 */
						while(linkMatcher.find()){
							String link = linkMatcher.group();
							// check bibtex link
							if(link.contains(BIBTEX_LINK_VALUE)){
								// extract href from link
								Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
								Matcher hrefMatcher = hrefPattern.matcher(link);
								if(hrefMatcher.find()){
									String href = hrefMatcher.group();
									// get URL
									String bibtexURL = href.substring(6, href.length()-1);
									// get snippet
									String bibtexSnippet = sc.getContentAsString(new URL(bibtexURL));
									sc.setBibtexResult(bibtexSnippet);
									sc.setScraper(this);
									return true;
								}
							}
						}
					}
				} catch (ConnectException cex) {
					throw new ScrapingException(cex);
				} catch (IOException ioe) {
					throw new ScrapingException(ioe);
				}
			}
			throw new ScrapingException("NasaADSScraper: Not supported nasa ads page. no bibtex link in html.");
		}
		return false;
	}

}
