package org.bibsonomy.scraper.url.kde.worldcat;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for http://www.worldcat.org 
 * @author tst
 */
public class WorldCatScraper extends AbstractUrlScraper {

	private static final String INFO = "Worldcat Scraper: Scraper for publications from " + href("http://www.worldcat.org", "worldcat") + ". Author: KDE";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "worldcat.org"), Pattern.compile("/oclc/")));

	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		try {
			final String bibtex = getBibtex(sc.getUrl(), sc, false);

			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	/**
	 * search publication on worldcat.org with a given isbn and returns it as bibtex
	 * @param isbn isbn for search
	 * @param sc ScrapingContext for download
	 * @return publication as bibtex
	 * @throws IOException 
	 * @throws ScrapingException
	 */
	public String getBibtexByISBN(final String isbn, final ScrapingContext sc) throws IOException, ScrapingException{
		final URL searchURL = new URL("http://www.worldcat.org/search?qt=worldcat_org_all&q=" + isbn.replace("-", "")); 
		return getBibtex(searchURL, sc, true);
	}

	private String getBibtex(final URL publPageURL, final ScrapingContext sc, final boolean search) throws IOException, ScrapingException{
		String exportUrl = null;
		if(search)
			exportUrl = publPageURL.getProtocol() + "://" + publPageURL.getHost() + publPageURL.getPath() + "?" + publPageURL.getQuery() + "&page=endnote&client=worldcat.org-detailed_record";
		else
			exportUrl = publPageURL.getProtocol() + "://" + publPageURL.getHost() + publPageURL.getPath() + "?page=endnote&client=worldcat.org-detailed_record";

		String endnote = WebUtils.getContentAsString(new URL(exportUrl));

		RisToBibtexConverter converter = new RisToBibtexConverter();
		return converter.RisToBibtex(endnote);
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
