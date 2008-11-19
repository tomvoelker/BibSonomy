package org.bibsonomy.scraper.url.kde.l3s;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

public class L3SScraper implements Scraper, UrlScraper {
	private static final String info 	= "L3S Scraper: Scrapes publications from <a href=\"http://www.l3s.de\">L3S</a> and returns it as bibtex. Author: KDE";
	
	private static final String L3S_URL = "l3s.de";
	
	private static final String PATTERN_HTML_TD = "<td class=\" value text\">([^<]*)</td>";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		//-- url shouldn't be null
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + L3S_URL), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}