package org.bibsonomy.scraper.url.kde.nejm;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author clemens
 * @version $Id$
 */
public class NEJMScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "The New England Journal of Medicine";
	private static final String SITE_URL = "http://www.nejm.org";
	private static final String INFO = "For references from the "+href(SITE_URL, SITE_NAME)+".";
	
	private static final String FORMAT_BIBTEX = "&format=bibtex";
	
	private static final List<Tuple<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "nejm.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pattern = Pattern.compile("doi/[^/]*/([^/]*/[^/#\\?]*)");
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
			sc.setScraper(this);
			final Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if (matcher.find()) {
				final String doi = matcher.group(1).replace("%2F", "/");
				final String downloadUrl = SITE_URL+"/action/downloadCitation?doi=" + doi + "&include=cit";
							
				try {
					final String bibtexContent = WebUtils.getContentAsString(downloadUrl + FORMAT_BIBTEX);
					
					if(bibtexContent != null){
						sc.setBibtexResult(bibtexContent);
						return true;
					}else
						throw new ScrapingFailureException("failure during download");
					
				} catch (IOException e) {
					throw new InternalFailureException(e);
				}

				
			}else
				throw new PageNotSupportedException("not found DOI in URL");
	}

	public String getInfo() {
		return INFO;
	}
	
	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}


}
