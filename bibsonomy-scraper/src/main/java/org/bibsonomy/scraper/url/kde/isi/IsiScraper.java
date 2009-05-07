package org.bibsonomy.scraper.url.kde.isi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author tst
 * @version $Id$
 */
public class IsiScraper extends AbstractUrlScraper {
	
	private static final String INFO = "ISI Scraper: Scraper for Publication from " + href("http://apps.isiknowledge.com", "ISI Web of Knowledge");

	private static final String HOST = "apps.isiknowledge.com";
	private static final String PATH = "/full_record.do";
	
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		 try {
			
			// get cookie
			String cookie = WebUtils.getCookies(sc.getUrl());

			// get sid from url
			String sid = null;
			Pattern sidPattern = Pattern.compile("SID=([^\\&]*)");
			Matcher sidMatcher = sidPattern.matcher(sc.getUrl().getQuery());
			if(sidMatcher.find())
				sid = sidMatcher.group(1);
			else
				throw new ScrapingFailureException("sid not available");
			
			// get selectedIds from given page 
			String selectedIds = null;
			Pattern selectedIdsPattern = Pattern.compile("name=\\\"selectedIds\\\" id=\\\"selectedIds\\\" value=\\\"([^\\\"]*)");
			Matcher selectedIdsMatcher = selectedIdsPattern.matcher(WebUtils.getContentAsString(sc.getUrl(), cookie));
			if(selectedIdsMatcher.find())
				selectedIds = selectedIdsMatcher.group(1);
			else
				throw new ScrapingFailureException("selected publications not found (selectedIds is missing)");
			
			// build post request for getting bibtex download page
			
			// post content
			String post = "action=go&" +
				"mode=quickOutput&" +
				"product=UA&" +
				"SID=" + sid + "&" +
				"format=save&" +
				"fields=FullNoCitRef&" +
				"mark_id=WOS&" +
				"count_new_items_marked=0&" +
				"selectedIds=" + selectedIds + "&" +
				"qo_fields=fullrecord&" +
				"save_options=bibtex&" +
				"save.x=27&" +
				"save.y=12&" +
				"next_mode=&" +
				"redirect_url= ";
			
			// call post request
			URL url = new URL("http://apps.isiknowledge.com/OutboundService.do");
			String content = WebUtils.getPostContentAsString(cookie, url, post);
			
			// extract direct bibtex download link from post result
			Pattern downloadLinkPattern = Pattern.compile("href=\\\"([^\\\"]*bibtex&)\\\"><img");
			Matcher downloadLinkMatcher = downloadLinkPattern.matcher(content);
			String downloadLink = null;
			if(downloadLinkMatcher.find())
				downloadLink = "http://pcs.isiknowledge.com/uml/" + downloadLinkMatcher.group(1);
			else
				throw new ScrapingFailureException("cannot find bibtex download link");
			
			// get bibtex
			String bibtex = WebUtils.getContentAsString(new URL(downloadLink), cookie);
			
			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
		
		return false;		
	}

	public String getInfo() {
		return INFO;
	}

}
