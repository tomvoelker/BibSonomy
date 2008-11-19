package org.bibsonomy.scraper.url.kde.ieee;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * Scraper for csdl2.computer.org
 * @author tst
 */ 
public class IEEEComputerSocietyScraper implements Scraper, UrlScraper {
	
	private static final String INFO = "IEEE compüuter society Scraper: Scraper for publications from <a href=\"http://www2.computer.org/portal/web/guest/home\">IEEE Computer Society</a>. Author: KDE";
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
		if(sc != null &&  sc.getUrl() != null && supportsUrl(sc.getUrl())){
			sc.setScraper(this);
			
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
							throw new InternalFailureException(e);
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
