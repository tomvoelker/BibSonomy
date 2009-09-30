package org.bibsonomy.scraper.url.kde.acl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for aclweb.org, given URL must be show on a PDF
 * TODO: Problem is that bibtex is only for few papers available 
 * TODO: add
 * @author tst
 * @version $Id$
 */
public class AclScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Association for Computational Linguistics";

	private static final String SITE_URL = "http://aclweb.org/";

	private static final String INFO = "Scraper for (PDF) references from " + href(SITE_URL, SITE_NAME)+".";

	private static final String ERROR_CODE_300 = "<TITLE>300 Multiple Choices</TITLE>";

	private static final Pattern hostPattern = Pattern.compile(".*" + "aclweb.org");
	private static final Pattern pathPattern = Pattern.compile("^" + "/anthology-new" + ".*\\.pdf$");
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(hostPattern, pathPattern));

	public String getInfo() {
		return INFO;
	}

	public boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		String downloadUrl = sc.getUrl().toString();

		// replace .pdf with .bib
		downloadUrl = downloadUrl.substring(0, downloadUrl.length()-4) + ".bib";

		String bibtex = null;
		try {
			bibtex = WebUtils.getContentAsString(new URL(downloadUrl));
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (IOException e) {
			throw new InternalFailureException(e);
		}

		if(bibtex != null){
			if(bibtex.contains(ERROR_CODE_300))
				throw new PageNotSupportedException("This aclweb.org page is not supported. BibTeX is not available.");

			// append url
			bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
			
			// add downloaded bibtex to result 
			sc.setBibtexResult(bibtex);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;	
	}

	public String getSupportedSiteName() {
		// TODO Auto-generated method stub
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		// TODO Auto-generated method stub
		return SITE_URL;
	}

}
