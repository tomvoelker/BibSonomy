package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.HTMLMetaDataHighwirePressToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper to extract bibtex information from a site, which holds Highwire Press Metadata
 * in it's HTML
 *
 * @author Johannes
 */
public class HighwirePressScraper implements Scraper {
	private static final HTMLMetaDataHighwirePressToBibtexConverter HIGHWIRE_PRESS_CONVERTER = new HTMLMetaDataHighwirePressToBibtexConverter();
	private static final String SITE_URL = "https://scholar.google.com/intl/en/scholar/inclusion.html#indexing";
	
	private static final String INFO = "The HighwirePressScraper resolves bibtex out of HTML Metatags, which are defined" + 
			" in the Highwire Press tags Metaformat, example given " + AbstractUrlScraper.href(SITE_URL, "here") + 
			"\n Because the values of HighwirePress-Metadata are not standardized, the scraper may not always be successful.";
	
	private static final Pattern HIGHWIRE_PRESS_PATTERN_TITLE = Pattern.compile("(?im)<\\s*meta(?=[^>]*name=\"citation_title\")[^>]*content=\"([^\"]*)\"[^>]*>");
	private static final Pattern HIGHWIRE_PRESS_PATTERN_AUTHOR = Pattern.compile("(?im)<\\s*meta(?=[^>]*name=\"citation_author\")[^>]*content=\"([^\"]*)\"[^>]*>");
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			String page = WebUtils.getContentAsString(scrapingContext.getUrl().toString());
			
			if (present(page)) {
				String result = HIGHWIRE_PRESS_CONVERTER.toBibtex(page);
				
				if (present(result)) {
					scrapingContext.setScraper(this);
					
					// add url
					result = BibTexUtils.addFieldIfNotContained(result, "url", scrapingContext.getUrl().toString());
					scrapingContext.setBibtexResult(result);
					return true;
				}
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getScraper()
	 */
	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		try {
			final String pageContent = WebUtils.getContentAsString(scrapingContext.getUrl().toString());
			return HIGHWIRE_PRESS_PATTERN_TITLE.matcher(pageContent).find() && HIGHWIRE_PRESS_PATTERN_AUTHOR.matcher(pageContent).find();
		}catch (Exception e) {
			return false;			
		}
	}

}
