package org.bibsonomy.scraper.url.kde.ieee;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper for csdl2.computer.org
 * @author tst
 */ 
public class IEEEComputerSocietyScraper implements Scraper {
	
	private static final String INFO = "Scraper for IEEE Computer Society from csdl2.computer.org";
	private static final String HOST = "csdl2.computer.org";
	
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";
	
	private static final String LINK_SUFFIX = "BibTex</A>";
	
	private static final Logger LOGGER = Logger
			.getLogger(IEEEComputerSocietyScraper.class);

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper)this);
	}

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null &&  sc.getUrl() != null && sc.getUrl().getHost().equals(HOST)){
			String page = sc.getPageContent();
			
			StringTokenizer tokenizer = new StringTokenizer(page, "\n");
			while(tokenizer.hasMoreTokens()){
			
				String linkMatch = tokenizer.nextToken();
				
				if(linkMatch.trim().endsWith(LINK_SUFFIX)){
					
					Pattern hrefPattern = Pattern.compile(PATTERN_HREF);
					Matcher hrefMatcher = hrefPattern.matcher(linkMatch);
					
					if(hrefMatcher.find()){
						String javascript = hrefMatcher.group();
						
						String window = javascript.substring(javascript.indexOf("Popup.document.write"), javascript.indexOf("Popup.document.close"));
						
						String bibtex = window.substring(22, window.length()-3);
							
						bibtex = bibtex.replace("<br/>", "\n");
						bibtex = bibtex.replace("<xsl:text>", "");
						bibtex = bibtex.replace("</xsl:text>", "");
						bibtex = bibtex.replace("&nbsp;", " ");
						
						try {
							bibtex = URLDecoder.decode(bibtex, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							LOGGER.error(e.getMessage());
							// do nothing. if encoding does not work well, then the bibtex entry is still invalid an import will be stopped
						}
						
						sc.setBibtexResult(bibtex);
						sc.setScraper(this);
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
