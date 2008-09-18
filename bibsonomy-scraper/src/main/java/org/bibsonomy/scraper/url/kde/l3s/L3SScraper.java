package org.bibsonomy.scraper.url.kde.l3s;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

public class L3SScraper implements Scraper {
	private static final String info 	= "arXiv Scraper: This scraper parses a publication page from <a href=\"http://www.l3s.de/\">L3S</a> and " +
	   									  "extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String L3S_URL = "l3s.de";
	
	private static final String PATTERN_HTML_TD = "<td class=\" value text\">([^<]*)</td>";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		//-- url shouldn't be null
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(L3S_URL)) {
				
				sc.setScraper(this);
				String bibtexresult = null;
				
				Pattern patternTd = Pattern.compile(PATTERN_HTML_TD, Pattern.MULTILINE | Pattern.DOTALL);
				Matcher matcherTd = patternTd.matcher(sc.getPageContent());
				while(matcherTd.find()){
					
					String td = matcherTd.group();
					td = td.substring(24, td.length()-5);
				
					//create the regex pattern to indicate if the content is bibtex or not 
					Pattern p = Pattern.compile("@\\w+\\{.+,");
					Matcher m = p.matcher(td);
					
					//if its a bibtex entry then extract it
					if (m.find()){
						bibtexresult = td;
						break;
					}
				}

				
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
	
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

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