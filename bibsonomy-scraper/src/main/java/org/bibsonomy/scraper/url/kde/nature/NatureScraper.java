package org.bibsonomy.scraper.url.kde.nature;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;

import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * Scraper for publication from nature.com
 * @author tst
 */
public class NatureScraper implements Scraper, UrlScraper {

	/**
	 * Host
	 */
	private static final String HOST = "nature.com";
	
	/**
	 * INFO
	 */
	private static final String INFO = "Nature Scraper: Scraper for publications from <a herf=\"http://www.nature.com/\">nature.com</a>. Author: KDE";
	
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
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}

}
