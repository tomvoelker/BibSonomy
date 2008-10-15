package org.bibsonomy.scraper.url.kde.openrepository;

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
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for openrepository pages
 * @author tst
 * @version $Id$
 */
public class OpenrepositoryScraper implements Scraper {
	
	private static final String SUPPORTED_HOST_OPENREPOSITORY = "openrepository.com";
	private static final String SUPPORTED_HOST_E_SPACE = "e-space.mmu.ac.uk/e-space";
	private static final String SUPPORTED_HOST_HIRSLA = "hirsla.lsh.is/lsh";
	private static final String SUPPORTED_HOST_GTCNI = "arrts.gtcni.org.uk/gtcni";
	private static final String SUPPORTED_HOST_EXETER = "eric.exeter.ac.uk/exeter";

	private static final String PATTERN_HANDLE = "handle/(.*)";
	
	private static final String INFO = "Scraper for openrepository pages. Following poages are supported: " + SUPPORTED_HOST_OPENREPOSITORY + ", " + SUPPORTED_HOST_E_SPACE + ", " + SUPPORTED_HOST_EXETER + ", " + SUPPORTED_HOST_GTCNI + ", " + SUPPORTED_HOST_HIRSLA;
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null){
			String downloadURL = null;
			
			if(sc.getUrl().toString().contains(SUPPORTED_HOST_OPENREPOSITORY)){
				downloadURL = "http://www." + SUPPORTED_HOST_OPENREPOSITORY + "/references?format=refman&handle=" + getHandle(sc.getUrl().toString());
			}else if(sc.getUrl().toString().contains(SUPPORTED_HOST_E_SPACE)){
				downloadURL = "http://www." + SUPPORTED_HOST_E_SPACE + "/references?format=refman&handle=" + getHandle(sc.getUrl().toString());
			}else if(sc.getUrl().toString().contains(SUPPORTED_HOST_EXETER)){
				downloadURL = "http://www." + SUPPORTED_HOST_EXETER + "/references?format=refman&handle=" + getHandle(sc.getUrl().toString());
			}else if(sc.getUrl().toString().contains(SUPPORTED_HOST_HIRSLA)){
				downloadURL = "http://www." + SUPPORTED_HOST_HIRSLA + "/references?format=refman&handle=" + getHandle(sc.getUrl().toString());
			}else if(sc.getUrl().toString().contains(SUPPORTED_HOST_GTCNI)){
				downloadURL = "http://" + SUPPORTED_HOST_GTCNI + "/references?format=refman&handle=" + getHandle(sc.getUrl().toString());
			}
			
			if(downloadURL != null){
				sc.setScraper(this);
				
				try {
					String ris = sc.getContentAsString(new URL(downloadURL));
					
					RisToBibtexConverter converter = new RisToBibtexConverter();
					String bibtex = converter.RisToBibtex(ris);
					
					if(bibtex != null){
						sc.setBibtexResult(bibtex);
						return true;
					}else
						throw new ScrapingFailureException("getting bibtex failed");
					
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
			}// else page is not supported. may be other scraper hits
		}
		return false;
	}

	/**
	 * get handle id from url
	 * @param url
	 * @return id, null if matching failed
	 */
	private String getHandle(String url){
		String handle = null;
		
		Pattern handlePattern = Pattern.compile(PATTERN_HANDLE);
		Matcher handleMatcher = handlePattern.matcher(url);
		if(handleMatcher.find())
			handle = handleMatcher.group(1);
		
		return handle;
	}
}
