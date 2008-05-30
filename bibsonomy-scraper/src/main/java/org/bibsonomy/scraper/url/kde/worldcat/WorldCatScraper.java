package org.bibsonomy.scraper.url.kde.worldcat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.url.RisToBibtexConverter;

/**
 * Scraper for http://www.worldcat.org 
 * @author tst
 */
public class WorldCatScraper implements Scraper {

	private static final String INFO = "Scraper for publications from http://www.worldcat.org.";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper)this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith("worldcat.org")){
			if(sc.getUrl().getPath().startsWith("/oclc/")){
				try {
					String bibtex = getBibtex(sc.getUrl(), sc, false);
					
					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						return true;
					}
				} catch (MalformedURLException ex) {
					throw new ScrapingException(ex);
				}
			}else
				throw new ScrapingException("not supported worldcat.org page. Select a publication page and scrape again.");
		}
		return false;
	}

	/**
	 * search publication on worldcat.org with a given isbn and returns it as bibtex
	 * @param isbn isbn for search
	 * @param sc ScrapingContext for download
	 * @return publication as bibtex
	 * @throws MalformedURLException
	 * @throws ScrapingException
	 */
	public String getBibtexByISBN(String isbn, ScrapingContext sc) throws MalformedURLException, ScrapingException{
		String bibtex = null;

		// clean up
		isbn = isbn.replace("-", ""); 

		URL searchURL = new URL("http://www.worldcat.org/search?qt=worldcat_org_all&q=" + isbn); 
		bibtex = getBibtex(searchURL, sc, true);
		
		return bibtex;
	}
	
	private String getBibtex(URL publPageURL, ScrapingContext sc, boolean search) throws MalformedURLException, ScrapingException{
		String exportUrl = null;
		if(search)
			exportUrl = publPageURL.getProtocol() + "://" + publPageURL.getHost() + publPageURL.getPath() + "?" + publPageURL.getQuery() + "&page=endnote&client=worldcat.org-detailed_record";
		else
			exportUrl = publPageURL.getProtocol() + "://" + publPageURL.getHost() + publPageURL.getPath() + "?page=endnote&client=worldcat.org-detailed_record";
		
		String endnote = sc.getContentAsString(new URL(exportUrl));
		
		RisToBibtexConverter converter = new RisToBibtexConverter();
		return converter.RisToBibtex(endnote);
	}
}
