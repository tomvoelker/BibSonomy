package org.bibsonomy.scraper.url.kde.pubmedcentral;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/** Scrapder for PubMed (http://www.pubmedcentral.nih.gov).
 * 
 * @author rja
 *
 */
public class PubMedCentralScraper extends UrlScraper {
	private static final String info = "PudMedCentral Scraper: This scraper parses a publication page of citations from " + href("http://www.pubmedcentral.nih.gov/", "PubMedCentral");
	private static final String HOST = "pubmedcentral.nih.gov";
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			try {
				String bibtexresult = null;

				Pattern p = null;
				Matcher m = null;
				
				//save the original URL 
				String _origUrl = sc.getUrl().toString();
				
				//find the string in the content that contains the list_uid for hubmed.org
				p = Pattern.compile("pubmed/(\\d+)\">PubMed record");
				m = p.matcher(sc.getPageContent());

				//if the uid will be found, the bibtex string would be extracted from hubmed
				if (m.find()){
					String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids=" + m.group(1);
					bibtexresult = sc.getContentAsString(new URL(newUrl));
				} 			
				
				//replace the humbed url through the original URL
				p = Pattern.compile("url = \".*\"");
				m = p.matcher(bibtexresult);

				if (m.find()){
					bibtexresult = m.replaceFirst("url = \"" + _origUrl + "\"" );
				}
			
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
					return true;
				}else
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