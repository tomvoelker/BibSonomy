package org.bibsonomy.scraper.url.kde.ams;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for ams.allenpress.com
 * @author tst
 * @version $Id$
 */
public class AmsScraper extends UrlScraper {
	
	private static final String INFO = "For references from ams.allenpress.com";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*ams.allenpress.com"), UrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pattern = Pattern.compile("doi=([^&]*)[&]?");
	
	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
			sc.setScraper(this);
			
			final Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if (matcher.find()) {
				final String doi = matcher.group(1).replace("%2F", "/");
				
				final String downloadUrl = "http://ams.allenpress.com/perlserv/?request=download-citation&t=endnote&f=1520-0485_38_1669&doi=" + doi + "&site=amsonline";
				
				try {
					final String endnote = sc.getContentAsString(new URL(downloadUrl));
					
					if(endnote != null){
						
						final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
						final String bibtex = converter.processEntry(endnote);
						
						if(bibtex != null){
							sc.setBibtexResult(bibtex);
							return true;
						}else
							throw new ScrapingFailureException("failure during converting to bibtex");
					}else
						throw new ScrapingFailureException("failure during download");
					
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
			}else
				throw new PageNotSupportedException("not found DOI in URL");
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
