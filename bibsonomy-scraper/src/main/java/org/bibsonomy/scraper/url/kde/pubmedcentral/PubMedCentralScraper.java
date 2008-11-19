package org.bibsonomy.scraper.url.kde.pubmedcentral;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

public class PubMedCentralScraper implements Scraper, UrlScraper {
	private static final Logger log 	= Logger.getLogger(PubMedCentralScraper.class);
	private static final String info 	= "PudMedCentral Scraper: This scraper parses a publication page of citations from <a href=\"http://www.pubmedcentral.nih.gov/\">PubMedCentral</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String HOST = "pubmedcentral.nih.gov";
	private static final String PUBMEDCENTRAL_HOST = "www.pubmedcentral.nih.gov";
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
			
			String bibtexresult = null;

			try {
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
		return false;
	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}