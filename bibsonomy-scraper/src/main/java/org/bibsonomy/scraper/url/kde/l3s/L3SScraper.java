package org.bibsonomy.scraper.url.kde.l3s;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/** Scrapes publications from L3S.
 * 
 * @author rja
 *
 */
public class L3SScraper extends AbstractUrlScraper {
	private static final String info 	= "L3S Scraper: Scrapes publications from " + href("http://www.l3s.de", "L3S");
	
	private static final String L3S_URL = "l3s.de";
	private static Pattern patternTd = Pattern.compile("<td class=\" value text\">([^<]*)</td>", Pattern.MULTILINE | Pattern.DOTALL);
	
	private static final List<Tuple<Pattern, Pattern>> pattern = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + L3S_URL), AbstractUrlScraper.EMPTY_PATTERN));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
				
				sc.setScraper(this);
				String bibtexresult = null;
				
				
				final Matcher matcherTd = patternTd.matcher(sc.getPageContent());
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
					// append url
					bibtexresult = BibTexUtils.addFieldIfNotContained(bibtexresult, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtexresult);
	
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");
		
	}

	public String getInfo() {
		return info;
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return pattern;
	}
}