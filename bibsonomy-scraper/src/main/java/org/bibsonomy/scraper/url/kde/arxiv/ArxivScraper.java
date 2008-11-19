package org.bibsonomy.scraper.url.kde.arxiv;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.OAIConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;


/** Scraper for arXiv.
 * 
 * @author rja
 *
 */
public class ArxivScraper extends UrlScraper {
	
	private static final String info = "arXiv Scraper: This scraper parses a publication page from " + href("http://arxiv.org/", "arXiv");
	
	private static final String ARXIV_HOST = "arxiv.org";
	
	private static final Pattern patternID = Pattern.compile("abs/([^?]*)");

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(ARXIV_HOST), UrlScraper.EMPTY_PATTERN));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(ARXIV_HOST)) {
			try {
				sc.setScraper(this);
				
				final Matcher matcherID = patternID.matcher(sc.getUrl().toString());
				if(matcherID.find()) {
					final String id = matcherID.group(1);
					// build url for oai_dc export
					String exportURL = "http://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:" + id + "&metadataPrefix=oai_dc";
					
					// download oai_dc reference
					String reference = sc.getContentAsString(new URL(exportURL));
					
					String bibtex = OAIConverter.convert(reference);
					
					// add arxiv citation to note
					bibtex = bibtex.replace("note = {", "note = {cite arxiv:" + id + "\n");
					// if note not exist
					bibtex = bibtex.replaceFirst("},", "}\nnote = {cite arxiv:" + id + "}");
					
					// set result
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("no arxiv id found in URL");
			} catch (MalformedURLException me) {
				throw new InternalFailureException(me);
			}
		}		
		return false;
	}

	public String getInfo() {
		return info;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	
}
