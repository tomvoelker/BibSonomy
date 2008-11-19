package org.bibsonomy.scraper.url.kde.informaworld;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraper extends UrlScraper {

	private static final String info = "Informaworld Scraper: This scraper parses a publication from " + href("http://www.informaworld.com/", "informaworld");

	private static final String INFORMAWORLD_HOST_NAME  = "informaworld.com";
	private static final String INFORMAWORLD_ABSTRACT_PATH = "/smpp/content~content=";

	private static final String PATTERN_ID = "content=([^~]*)";

	private static final String INFORMAWORLD_BIBTEX_PATH = "/smpp/content~db=all";
	private static final String INFORMAWORLD_BIBTEX_DOWNLOAD_PATH = "/smpp/content?file.txt&tab=citation&popup=&group=&expanded=&mode=&maction=&backurl=&citstyle=endnote&showabs=false&format=file&toemail=&subject=&fromname=&fromemail=&content={id}&selecteditems={sid}";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + INFORMAWORLD_HOST_NAME), UrlScraper.EMPTY_PATTERN));

	private static final Pattern pattern = Pattern.compile("content=([^~]*)");

	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String id = null;

		final Matcher matcher = pattern.matcher(sc.getUrl().getPath());
		if(matcher.find())
			id = matcher.group(1);

		final String citUrl = "http://www." + INFORMAWORLD_HOST_NAME + (INFORMAWORLD_BIBTEX_DOWNLOAD_PATH.replace("{id}", id)).replace("{sid}", id.substring(1));

		try {
			sc.setUrl(new URL(citUrl));
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		}

		final EndnoteToBibtexConverter bib = new EndnoteToBibtexConverter();
		final String bibResult = bib.processEntry(sc.getPageContent());

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
