package org.bibsonomy.scraper.url.kde.informaworld;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraper extends AbstractUrlScraper {

	private static final String info = "Informaworld Scraper: This scraper parses a publication from " + href("http://www.informaworld.com/", "informaworld");

	private static final String INFORMAWORLD_HOST_NAME  = "informaworld.com";
	private static final String INFORMAWORLD_BIBTEX_DOWNLOAD_PATH = "/smpp/content?file.txt&tab=citation&popup=&group=&expanded=&mode=&maction=&backurl=&citstyle=endnote&showabs=false&format=file&toemail=&subject=&fromname=&fromemail=&content={id}&selecteditems={sid}";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + INFORMAWORLD_HOST_NAME), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern pattern = Pattern.compile("content=([^~]*)");

	private static final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();


	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {
			final String cookie = WebUtils.getCookies(sc.getUrl());

			if (cookie != null) {
				String id = null;

				final Matcher matcher = pattern.matcher(sc.getUrl().getPath());
				if(matcher.find())
					id = matcher.group(1);

				sc.setUrl(new URL(("http://www." + INFORMAWORLD_HOST_NAME + (INFORMAWORLD_BIBTEX_DOWNLOAD_PATH.replace("{id}", id)).replace("{sid}", id.substring(1)))));

				final String bibResult = converter.processEntry(WebUtils.getContentAsString(sc.getUrl(), cookie));

				if (bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				} else
					throw new ScrapingFailureException("getting BibTeX failed");
			}else
				throw new ScrapingFailureException("cookie is missing");
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

	}


	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
