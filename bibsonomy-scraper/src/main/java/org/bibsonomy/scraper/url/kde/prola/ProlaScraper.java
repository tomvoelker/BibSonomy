package org.bibsonomy.scraper.url.kde.prola;

import java.io.StringReader;
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


/**
 * Scraper for prola.aps.org. It scrapes selected bibtex snippets and selected articles.
 * @author tst
 */
public class ProlaScraper implements Scraper {
	
	private static final String INFO = "ProlaScraper: For selected bibtex snippets and articles from prola.aps.org.";
	
	/*
	 * needed URLs and components
	 */
	private static final String PROLA_APS_HOST = "prola.aps.org";
	
	private static final String PROLA_APS_URL_BASE = "http://prola.aps.org";
	
	private static final String PROLA_APS_BIBTEX_PARAM = "type=bibtex";

	/*
	 * needed regular expressions to extract download link
	 */
	private static final String PATTERN_LINK = "<a\\b[^<]*</a>";
	
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";
	
	private static final String PATTERN_LINK_VALUE = ">(.*)<";
	
	private static final String PATTERN_BIBTEX_ENTRY = "@\\b[^\\{@]*\\{.*";
	
	/*
	 * value of download link
	 */
	private static final String DOWNLOAD_LINK_VALUE = "BibTeX";
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	/**
	 * Extract atricles from prola.aps.org. It works with the article page, the bibtex page and a selected bibtex snippet.
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().equals(PROLA_APS_HOST)){
			String prolaPageContent = sc.getPageContent();
			
			// check if snippet is selected
			if(sc.getSelectedText() != null){
				String bibtex = sc.getSelectedText();
				
				//remove comments bevor reference
				bibtex = cleanBibtexEntry(bibtex);
				
				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex);
				sc.setScraper(this);
				return true;						
			}
			
			// check if selected page is a bibtex page
			if(sc.getUrl().getQuery() != null && sc.getUrl().getQuery().contains(PROLA_APS_BIBTEX_PARAM)){
				String bibtex = sc.getPageContent();
				
				//remove comments bevor reference
				bibtex = cleanBibtexEntry(bibtex);
				
				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex);
				sc.setScraper(this);
				return true;						
			}
			
			// no snippet, download bibtex
			String downloadLink = null;
			
			// extract all links from downloaded page
			Pattern linkPattern = Pattern.compile(PATTERN_LINK);
			Matcher linkMatcher = linkPattern.matcher(prolaPageContent);
			
			while(linkMatcher.find()){
				String linkMatch = linkMatcher.group();
				
				// extract the value between the a tags
				Pattern linkValuePattern = Pattern.compile(PATTERN_LINK_VALUE);
				Matcher linkValueMatcher = linkValuePattern.matcher(linkMatch);
				
				if(linkValueMatcher.find()){
					String linkValue = linkValueMatcher.group();
					
					// cut of the opening and closing brackets
					linkValue = linkValue.substring(1, linkValue.length()-1);
					
					// check if the link is the download bibtex link
					if(linkValue.equals(DOWNLOAD_LINK_VALUE)){
						
						// extracted link is the bibtex download link, search href attribute 
						Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
						Matcher hrefMatcher = hrefPattern.matcher(linkMatch);
						
						if(hrefMatcher.find()){
							String href = hrefMatcher.group();
							
							// cut of the leading herf=" and the ending "
							href = href.substring(6, href.length()-1);
							
							// build url to bibtex of this article
							downloadLink = PROLA_APS_URL_BASE + href;
							break;
						}
					}
				}
			}
			
			try {
				// check if download link is found
				if(downloadLink != null){
					
					// download article as bibtex
					String downloadedBibtex = null;
					downloadedBibtex = sc.getContentAsString(new URL(downloadLink));
					
					if(downloadedBibtex != null){
						
						//remove comments bevor reference
						downloadedBibtex = cleanBibtexEntry(downloadedBibtex);
						
						// add downloaded bibtex to result 
						sc.setBibtexResult(downloadedBibtex);
						sc.setScraper(this);
						return true;						
					}else
						throw new ScrapingException("ProlaScraper: can't get bibtex from this article");
				}else
					throw new PageNotSupportedException("ProlaScraper: This prola side has no bibtex download link.");
			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}
		}
		return false;
	}
	
	/**
	 * This method cuts of eveerything bevor bibtex entry.
	 * @param bibtexSnippet bibtex entry as String
	 * @return cleaned bibtex String
	 */
	private String cleanBibtexEntry(String bibtexSnippet){
		String bibtex = null;

		// search begin of bibtex entry
		Pattern bibPattern = Pattern.compile(PATTERN_BIBTEX_ENTRY, Pattern.DOTALL);
		Matcher bibMatcher = bibPattern.matcher(bibtexSnippet);
		
		if(bibMatcher.find())
			// cut of everything bevor bibtex entry
			bibtex = bibMatcher.group();
		
		return bibtex;
	}
}
