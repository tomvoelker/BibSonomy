package org.bibsonomy.scraper.url.kde.opac;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.converter.PicaToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class OpacScraper extends AbstractUrlScraper {
	private static final String info = "OPAC Scraper: This scraper parses a publication page from " + href("http://opac.bibliothek.uni-kassel.de/" , "Bibliothek Kassel");

	/**
	 * TODO: This Scraper match only on URL's with es specific query value in path and queries. The current patterns don't work.
	 */
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(AbstractUrlScraper.EMPTY_PATTERN, Pattern.compile(".*?PRS=PP/PPN")));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		//log.fatal("Opac");
		//log.fatal(sc.getUrl().toString());
		//Pattern.matches("^http.*?+/CHARSET=UTF-8/PRS=PP/PPN\\?PPN=[0-9X]+$", sc.getUrl().toString())
		//sc.getUrl().toString().startsWith(OPAC_URL)
		sc.setScraper(this);

		try {
			// create a converter and start converting :)
			final PicaToBibtexConverter converter = new PicaToBibtexConverter(sc.getPageContent(), "xml", sc.getUrl().toString());

			final String bibResult = converter.getBibResult();

			if(bibResult != null){
				sc.setBibtexResult(bibResult);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (Exception e){
			throw new InternalFailureException(e);
		}
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}


}
