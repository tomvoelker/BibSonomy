package org.bibsonomy.scraper.url.kde.acl;

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
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * Scraper for aclweb.org, given URL must be show on a PDF
 * TODO: Problem is that bibtex is only for few papers available 
 * TODO: add
 * @author tst
 * @version $Id$
 */
public class AclScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "ACL Scraper: Scraper for (PDF) references from <a herf=\"http://aclweb.org/\">Association for Computational Linguistics</a>";
	
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
		if(sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
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
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");
				
			}else
				throw new PageNotSupportedException("This aclweb.org page is not supported.");
		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH_PREFIX + ".*\\.pdf")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}

}
