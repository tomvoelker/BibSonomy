package org.bibsonomy.scraper.url.kde.iop;

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


/**
 * SCraper for http://www.iop.org
 * @author tst
 */
public class IOPScraper implements Scraper {

	private static final String INFO = "IOPScraper: Scraper for citations from http://www.iop.org/EJ.";

	/*
	 * URL parts
	 */
	private static final String IOP_URL_HOST = "www.iop.org";
	
	private static final String IOP_URL_PATH_START = "/EJ";
		
	private static final String IOP_EJ_URL_BASE = "http://www.iop.org";
	
	/*
	 * needed regular expressions to extract download citation link
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";
	
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";
	
	private static final String PATTERN_LINK_VALUE = ">(.*)<";
	
	/*
	 * value of citation download link
	 */
	private static final String DOWNLOAD_LINK_VALUE = "Download citation";

	public String getInfo(){
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * This scraper extract the citation download link and builds the direct link to the bibtex reference.
	 * It supports only http://www.iop.org sides which starts in the path with "/EJ". EJ stands for electrionic journals.
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().equals(IOP_URL_HOST) && sc.getUrl().getPath().startsWith(IOP_URL_PATH_START)){
			sc.setScraper(this);
			
			// download article page
			String articlePageContent = sc.getPageContent();
			
			// the link to the citation
			String citationLink = null;
			
			// extract all links from downloaded page
			Pattern linkPattern = Pattern.compile(PATTERN_LINK);
			Matcher linkMatcher = linkPattern.matcher(articlePageContent);
			
			while(linkMatcher.find()){
				String linkMatch = linkMatcher.group();
				
				// extract the value between the a tags
				Pattern linkValuePattern = Pattern.compile(PATTERN_LINK_VALUE);
				Matcher linkValueMatcher = linkValuePattern.matcher(linkMatch);
				
				if(linkValueMatcher.find()){
					String linkValue = linkValueMatcher.group();
					
					// cut of the opening and closing brackets
					linkValue = linkValue.substring(1, linkValue.length()-1);
					
					// check if the link is the download citation link
					if(linkValue.equals(DOWNLOAD_LINK_VALUE)){
						
						// extracted link is the citation download link, search href attribute 
						Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
						Matcher hrefMatcher = hrefPattern.matcher(linkMatch);
						
						if(hrefMatcher.find()){
							String href = hrefMatcher.group();
							
							// cut of the leading herf=" and the ending "
							href = href.substring(6, href.length()-1);
							
							// build url to citation of this article
							citationLink = IOP_EJ_URL_BASE + href;
							break;
						}
					}
				}
			}
			
			try {
				// check if citation link is found
				if(citationLink != null){
					// add bibtex params to citation url
					citationLink = citationLink + "?format=bibtex&submit=1";
					
					// download citation as bibtex
					String citationBibtex = null;
					citationBibtex = sc.getContentAsString(new URL(citationLink));
					
					if(citationBibtex != null){
						// add downloaded bibtex to result 
						sc.setBibtexResult(citationBibtex);
						return true;						
					}else
						throw new ScrapingFailureException("getting bibtex failed");

				}else
					throw new PageNotSupportedException("IOPScraper: This iop side has no citation download link.");
			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}
		}
		return false;
	}

}
