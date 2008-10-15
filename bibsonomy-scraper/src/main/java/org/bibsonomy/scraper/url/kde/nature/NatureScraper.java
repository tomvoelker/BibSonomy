package org.bibsonomy.scraper.url.kde.nature;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;

import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for publication from nature.com
 * @author tst
 */
public class NatureScraper implements Scraper {

	/**
	 * INFO
	 */
	private static final String INFO = "Scraper for publications from nature.com";
	
	/**
	 * pattern for links
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";
	
	/**
	 * pattern for href field
	 */
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";

	/**
	 * name from download link
	 */
	private static final String CITATION_DOWNLOAD_LINK_NAME = ">Export citation<";
	
	/**
	 * get INFO
	 */
	public String getInfo() {
		return INFO;
	}

	/**
	 * this scraper
	 */
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * Scrapes publications from nature.com
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith("nature.com")){
			sc.setScraper(this);
			
			// bibtex result
			String bibtex = null;
			
			try {
				// get publication page
				String publicationPage = sc.getPageContent();
				
				// extract download citation link
				Pattern linkPattern = Pattern.compile(PATTERN_LINK);
				Matcher linkMatcher = linkPattern.matcher(publicationPage);
				while(linkMatcher.find()){
					String link = linkMatcher.group();
					
					// check if link is download link
					if(link.contains(CITATION_DOWNLOAD_LINK_NAME)){
						
						// get href attribute
						Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
						Matcher hrefMatcher = hrefPattern.matcher(link);
						if(hrefMatcher.find()){
							String href = hrefMatcher.group();
							href = href.substring(6, href.length()-1);
							
							// download citation (as ris)
							String ris = sc.getContentAsString(new URL("http://" + sc.getUrl().getHost() + "/" + href));

							// convert ris to bibtex
							final RisToBibtexConverter converter = new RisToBibtexConverter();
							bibtex = converter.RisToBibtex(ris);
							
							// return bibtex
							if(bibtex != null){
								sc.setBibtexResult(bibtex);
								return true;
							}else
								throw new ScrapingFailureException("getting bibtex failed");

						}
					}
				}
				throw new PageNotSupportedException("Page not supported. Download URL is missing.");
			} catch (MalformedURLException ex) {
				throw new InternalFailureException(ex);
			}
		}
		return false;
	}

}
