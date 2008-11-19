package org.bibsonomy.scraper.url.kde.editlib;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class EditLibScraper extends UrlScraper {

	private static final String info = "Ed/ITLib Scraper: This Scraper parses a publication from " + href("http://www.editlib.org", "Ed/ITLib");

	private static final String EDITLIB_HOST  = "editlib.org";
	private static final String EDITLIB_PATH  = "/index.cfm";
	private static final String EDITLIB_HOST_NAME  = "http://www.editlib.org";
	private static final String EDITLIB_ABSTRACT_PATH = "/index.cfm?fuseaction=Reader.ViewAbstract&paper_id=";
	private static final String EDITLIB_BIBTEX_PATH = "/index.cfm?fuseaction=Reader.ChooseCitationFormat&paper_id=";
	private static final String EDITLIB_BIBTEX_DOWNLOAD_PATH = "/index.cfm/files/citation_{id}.bib?fuseaction=Reader.ExportAbstract&format=BibTex&paper_id=";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + EDITLIB_HOST), Pattern.compile(EDITLIB_PATH + ".*")));
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		String url = sc.getUrl().toString();

		sc.setScraper(this);

		String id = null;

		if(url.startsWith(EDITLIB_HOST_NAME + EDITLIB_ABSTRACT_PATH)) {
			id = url.substring(url.indexOf(EDITLIB_ABSTRACT_PATH) + EDITLIB_ABSTRACT_PATH.length());
		}

		if(url.startsWith(EDITLIB_HOST_NAME + EDITLIB_BIBTEX_PATH)) {
			id = url.substring(url.indexOf(EDITLIB_BIBTEX_PATH) + EDITLIB_BIBTEX_PATH.length());
		}

		String bibResult = null;

		try {
			URL citURL = new URL(EDITLIB_HOST_NAME + EDITLIB_BIBTEX_DOWNLOAD_PATH.replace("{id}", id) + id);
			bibResult = sc.getContentAsString(citURL);
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		}

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
