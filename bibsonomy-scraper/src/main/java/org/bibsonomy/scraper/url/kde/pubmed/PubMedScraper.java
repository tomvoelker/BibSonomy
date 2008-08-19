package org.bibsonomy.scraper.url.kde.pubmed;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author daill
 * @version $Id$
 */
public class PubMedScraper implements Scraper {
	private static final Logger log 	= Logger.getLogger(PubMedScraper.class);
	private static final String info 	= "PudMed Scraper: This scraper parses a publication page of citations from <a href=\"http://www.ncbi.nlm.nih.gov/sites/entrez/\">PubMed</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String PUBMED_HOST = "www.ncbi.nlm.nih.gov";
	private static final String PUBMED_EUTIL_HOST = "eutils.ncbi.nlm.nih.gov";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		String bibtexresult = null;

		if ((sc.getUrl() != null && PUBMED_HOST.equals(sc.getUrl().getHost())) || (sc.getUrl() != null && PUBMED_EUTIL_HOST.equals(sc.getUrl().getHost())) ) {
			Pattern pa = null;
			Matcher ma = null;
			
			//save the original URL 
			String _origUrl = sc.getUrl().toString();
			
			try{
				if(_origUrl.matches("(?ms)^.+db=PubMed.+$")){
					
					//try to get the PMID out of the paramters
					pa = Pattern.compile("\\d+");
					ma = pa.matcher(sc.getUrl().getQuery());
						
					//if the PMID is existent then get the bibtex from hubmed
					if(ma.find()){
						String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids=" + ma.group();
						bibtexresult = sc.getContentAsString(new URL(newUrl));
					}

				// try to scrape with new URL-Pattern
				// avoid crashes
				} else if (sc.getPageContent().matches("(?ms)^.+db=PubMed.+$")){

					//try to get the PMID out of the paramters
					pa = Pattern.compile("(?ms)^.+PMID: (\\d*) .+$");
					ma = pa.matcher(sc.getPageContent());
					
					//if the PMID is existent then get the bibtex from hubmed
					if(ma.find()){
						String newUrl = "http://www.hubmed.org/export/bibtex.cgi?uids=" + ma.group(1);
						bibtexresult = sc.getContentAsString(new URL(newUrl));
					}
				}
				
				//replace the humbed url through the original URL
				pa = Pattern.compile("url = \".*\"");
				ma = pa.matcher(bibtexresult);

				if (ma.find()){
					bibtexresult = ma.replaceFirst("url = \"" + _origUrl + "\"" );
				}
				
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
		
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
}