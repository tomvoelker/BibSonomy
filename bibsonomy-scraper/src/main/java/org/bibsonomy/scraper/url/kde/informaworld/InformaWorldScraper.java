package org.bibsonomy.scraper.url.kde.informaworld;

import java.net.MalformedURLException;
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
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class InformaWorldScraper implements Scraper, UrlScraper {

	private static final String info = "Informaworld Scraper: This Scraper parses a publication from <a herf=\"http://www.informaworld.com/\">informaworld</a> "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String INFORMAWORLD_HOST_NAME  = "informaworld.com";
	private static final String INFORMAWORLD_ABSTRACT_PATH = "/smpp/content~content=";
	
	private static final String PATTERN_ID = "content=([^~]*)";
	
	private static final String INFORMAWORLD_BIBTEX_PATH = "/smpp/content~db=all";
	private static final String INFORMAWORLD_BIBTEX_DOWNLOAD_PATH = "/smpp/content?file.txt&tab=citation&popup=&group=&expanded=&mode=&maction=&backurl=&citstyle=endnote&showabs=false&format=file&toemail=&subject=&fromname=&fromemail=&content={id}&selecteditems={sid}";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				sc.setScraper(this);
				
				String url = sc.getUrl().toString();
				
				String id = null;
				
				Pattern pattern = Pattern.compile(PATTERN_ID);
				Matcher matcher = pattern.matcher(sc.getUrl().getPath());
				if(matcher.find())
					id = matcher.group(1);
				
				
				String citUrl = "http://www." + INFORMAWORLD_HOST_NAME + (INFORMAWORLD_BIBTEX_DOWNLOAD_PATH.replace("{id}", id)).replace("{sid}", id.substring(1));
				
				try {
					sc.setUrl(new URL(citUrl));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
				EndnoteToBibtexConverter bib = new EndnoteToBibtexConverter();
				String bibResult = bib.processEntry(sc.getPageContent());
								
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + INFORMAWORLD_HOST_NAME), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
