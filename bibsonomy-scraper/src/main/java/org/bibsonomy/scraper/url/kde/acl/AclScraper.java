package org.bibsonomy.scraper.url.kde.acl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * Scraper for aclweb.org, given URL must be show on a PDF
 * TODO: Problem is that bibtex is only for few papers available 
 * @author tst
 * @version $Id$
 */
public class AclScraper implements Scraper {
	
	private static final String INFO = "Scraper for (PDF) references from aclweb.org";
	
	private static final String HOST = "aclweb.org";
	
	private static final String ERROR_CODE_300 = "<TITLE>300 Multiple Choices</TITLE>";
	
	private static final String PATH_PREFIX = "/anthology-new";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			if(sc.getUrl().getPath().startsWith(PATH_PREFIX) && sc.getUrl().getPath().endsWith(".pdf")){
				String downloadUrl = sc.getUrl().toString();
				
				// replace .pdf with .bib
				downloadUrl = downloadUrl.substring(0, downloadUrl.length()-4) + ".bib";
				
				String bibtex = null;
				try {
					bibtex = sc.getContentAsString(new URL(downloadUrl));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibtex != null){
					if(bibtex.contains(ERROR_CODE_300))
						throw new PageNotSupportedException("This aclweb.org page is not supported. Bibtex is not available.");
					
					sc.setBibtexResult(bibtex);
					sc.setScraper(this);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");
				
			}else
				throw new PageNotSupportedException("This aclweb.org page is not supported.");
		}
		return false;
	}

}
