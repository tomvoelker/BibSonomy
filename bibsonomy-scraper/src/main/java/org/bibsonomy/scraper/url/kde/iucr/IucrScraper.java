package org.bibsonomy.scraper.url.kde.iucr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.exceptions.UseageFailureException;

/**
 * scraper for jornals from iucr.org. Because of the frame structure of 
 * journals.iucr.org pages only issues can be scraped which are sperated in a
 * another tab then the journal itself. The url of the issue in the new tab 
 * points dirctly to the issue and not to jounal page (if you open the issue in 
 * the same tab, then the url in the navgationbar will still point to the journal 
 * page and scraping is not possible.
 * 
 * example:
 * we want the second issue from this journal ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * if we open the doi-link in the same tab, we get this url ->
 * http://journals.iucr.org/b/issues/2008/03/00/issconts.html
 * 
 * the issue will only be loaded in the central frame of the page and has no 
 * effect on the url. So we cannot recognize which issue was selected by the user.
 * If the user open the doi-link in a new tab, only the content of the central 
 * frame will be loaded and we can get the URL to the page of the issue. Like this
 * one ->
 * http://scripts.iucr.org/cgi-bin/paper?S0108768108005119
 * 
 * The rest is simple: extract the cnor from the form and build a download link,
 * like this -> http://scripts.iucr.org/cgi-bin/biblio?Action=download&cnor=ck5030&saveas=BIBTeX
 * 
 * @author tst
 * @version $Id$
 */
public class IucrScraper implements Scraper {
	
	/*
	 * messages
	 */
	
	private static final String INFO = "Scraper for publications from http://scripts.iucr.org/.";

	private static final String USEAGE_FAILURE_MESSAGE = "Please open the publication in a new browser tab and post it again.";
	
	/*
	 * hosts
	 */
	
	private static final String HOST = "iucr.org";
	
	private static final String HOST_JOURNAL_PREFIX = "journal";
	
	private static final String HOST_SCRIPTS_PREFIX = "scripts";
	
	/*
	 * pattern
	 */
	
	private static final String PATTERN_CNOR = "<input name=\"cnor\" value=\"([^\"]*)\" type=\"hidden\">";

	/*
	 * Download link
	 */
	private static final String DOWNLOAD_LINK_PART = "http://scripts.iucr.org/cgi-bin/biblio?Action=download&saveas=BIBTeX&cnor=";
	

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			
			if(sc.getUrl().getHost().startsWith(HOST_JOURNAL_PREFIX)){
				throw new UseageFailureException(USEAGE_FAILURE_MESSAGE);
			
			}else if(sc.getUrl().getHost().startsWith(HOST_SCRIPTS_PREFIX)){
				
				try {
					String pageContent = sc.getPageContent();
					
					// extract cnor number from HTML
					String cnor = null;
					Pattern cnorPattern = Pattern.compile(PATTERN_CNOR);
					Matcher cnorMatcher = cnorPattern.matcher(pageContent);
					if(cnorMatcher.find())
						cnor = cnorMatcher.group(1);
					
					// check if cnor can be extracted
					if(cnor != null){
						
						// build download link
						String downloadLink = DOWNLOAD_LINK_PART + cnor;
						
						// download bibtex
						String bibtex = sc.getContentAsString(new URL(downloadLink));
						
						if(bibtex != null){
							
							// successful
							sc.setBibtexResult(bibtex);
							sc.setScraper(this);
							return true;
							
						}else{
							// bibtex == null, may be wrong download url
							throw new ScrapingFailureException("Bibtex download failed. Bibtex result is null.");
						}

					// can't extract cnor
					}else{
						// missing id
						throw new ScrapingFailureException("ID for donwload link is missing.");
					}
					
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
			}else{
				// no journal or scripts page
				throw new PageNotSupportedException(PageNotSupportedException.DEFAULT_ERROR_MESSAGE + this.getClass().getName());
			}
		}
		
		return false;
	}

}
