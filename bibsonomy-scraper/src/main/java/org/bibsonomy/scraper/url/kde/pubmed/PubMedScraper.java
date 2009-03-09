package org.bibsonomy.scraper.url.kde.pubmed;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author daill
 * @version $Id$
 */
public class PubMedScraper extends AbstractUrlScraper {
	private static final String info = "PudMed Scraper: This scraper parses a publication page of citations from "
			+ href("http://www.ncbi.nlm.nih.gov/sites/entrez/", "PubMed");

	private static final String HOST = "ncbi.nlm.nih.gov";
	private static final String PUBMED_EUTIL_HOST = "eutils.ncbi.nlm.nih.gov";

	private static final List<Tuple<Pattern, Pattern>> patterns = new LinkedList<Tuple<Pattern, Pattern>>();
	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST),
				AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*"
				+ PUBMED_EUTIL_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	protected boolean scrapeInternal(ScrapingContext sc)
			throws ScrapingException {
		String bibtexresult = null;
		sc.setScraper(this);

		Pattern pa = null;
		Matcher ma = null;

		// save the original URL
		String _origUrl = sc.getUrl().toString();

		try {
			if (_origUrl.matches("(?ms)^.+db=PubMed.+$")) {

				// try to get the PMID out of the paramters
				pa = Pattern.compile("\\d+");
				ma = pa.matcher(sc.getUrl().getQuery());

				// if the PMID is existent then get the bibtex from hubmed
				if (ma.find()) {
					String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids="
							+ ma.group();
					bibtexresult = sc.getContentAsString(new URL(newUrl));
				}

				// try to scrape with new URL-Pattern
				// avoid crashes
			} else if (sc.getPageContent().matches("(?ms)^.+db=PubMed.+$")) {

				// try to get the PMID out of the paramters
				pa = Pattern.compile("(?ms)^.+PMID: (\\d*) .+$");
				ma = pa.matcher(sc.getPageContent());

				// if the PMID is existent then get the bibtex from hubmed
				if (ma.find()) {
					String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids="
							+ ma.group(1);
					bibtexresult = sc.getContentAsString(new URL(newUrl));
				}
			}

			// replace the humbed url through the original URL
			pa = Pattern.compile("url = \".*\"");
			ma = pa.matcher(bibtexresult);

			if (ma.find()) {
				// escape dollar signs 
				bibtexresult = ma.replaceFirst("url = \"" + _origUrl.replace("$", "\\$") + "\"");
			}

			// -- bibtex string may not be empty
			if (bibtexresult != null && !"".equals(bibtexresult)) {
				sc.setBibtexResult(bibtexresult);
				return true;
			} else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (MalformedURLException e) {
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