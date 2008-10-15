package org.bibsonomy.scraper.url.kde.ams;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for ams.allenpress.com
 * @author tst
 * @version $Id$
 */
public class AmsScraper implements Scraper {
	
	private static final String INFO = "For references from ams.allenpress.com";

	private static final String HOST = "ams.allenpress.com";
	
	private static final String PATTERN = "doi=([^&]*)[&]?";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			sc.setScraper(this);
			String doi = null;
			
			String url = sc.getUrl().toString();
			Pattern pattern = Pattern.compile(PATTERN);
			Matcher matcher = pattern.matcher(url);
			if(matcher.find())
				doi = matcher.group(1);
			
			if(doi != null){
				// littel cleanup
				doi = doi.replace("%2F", "/");
				
				String downloadUrl = "http://ams.allenpress.com/perlserv/?request=download-citation&t=endnote&f=1520-0485_38_1669&doi=" + doi + "&site=amsonline";
				
				try {
					String endnote = null;
					endnote = sc.getContentAsString(new URL(downloadUrl));
					
					if(endnote != null){
						String bibtex = null;
						
						EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
						bibtex = converter.processEntry(endnote);
						
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
		return false;
	}

}
