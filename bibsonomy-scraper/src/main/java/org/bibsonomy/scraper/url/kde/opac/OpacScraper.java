package org.bibsonomy.scraper.url.kde.opac;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.PicaToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class OpacScraper extends UrlScraper {
	private static final String info = "OPAC Scraper: This scraper parses a publication page from " + href("http://opac.bibliothek.uni-kassel.de/" , "Bibliothek Kassel");

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile("^http.*"), Pattern.compile(".*?/CHARSET=UTF-8/PRS=PP/PPN\\?PPN=[0-9X]+$")));
	
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
