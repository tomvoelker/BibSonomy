package org.bibsonomy.scraper.url.kde.jmlr;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper for papers from http://jmlr.csail.mit.edu/
 * @author tst
 * @version $Id$
 */
public class JMLRScraper implements Scraper {
	
	private static final String INFO = "Scraper for papers from http://jmlr.csail.mit.edu/";
	
	private static final String HOST = "jmlr.csail.mit.edu";
	
	private static final String PATH = "/papers/";
	
	
	/*
	 * pattern
	 */
	private static final String PATTERN_TITLE = "<h2>([^<]*)</h2>";
	private static final String PATTERN_AUTHOR = "<i>([^<]*)</i></b>";
	private static final String PATTERN_YEAR_PAGE = "</i></b>([^<]*)</p>";
	private static final String PATTERN_YEAR = "(\\d{4})";
	private static final String PATTERN_PAGE = ":([^,]*),";
	private static final String PATTERN_VOLUME = "/papers/([^/]*)/";
	private static final String PATTERN_FIRST_LASTNAME = " ([\\S]*) and";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			if(sc.getUrl().getPath().startsWith(PATH) && sc.getUrl().getPath().endsWith(".html")){
				String pageContent = sc.getPageContent();
				
				// get title (directly)
				String title = null;
				Pattern titlePattern = Pattern.compile(PATTERN_TITLE);
				Matcher titleMatcher = titlePattern.matcher(pageContent);
				if(titleMatcher.find())
					title = titleMatcher.group(1);

				// get author (directly)
				String author = null;
				Pattern authorPattern = Pattern.compile(PATTERN_AUTHOR);
				Matcher authorMatcher = authorPattern.matcher(pageContent);
				if(authorMatcher.find())
					author = authorMatcher.group(1);
				// clean up author string
				if(author != null)
					author = author.replace(",", " and");
				
				// get volume (from url)
				String volume = null;
				Pattern volumePattern = Pattern.compile(PATTERN_VOLUME);
				Matcher volumeMatcher = volumePattern.matcher(sc.getUrl().getPath());
				if(volumeMatcher.find())
					volume = volumeMatcher.group(1);

				// get pageYear (directly)
				String pageYear = null;
				Pattern pageYearPattern = Pattern.compile(PATTERN_YEAR_PAGE);
				Matcher pageYearMatcher = pageYearPattern.matcher(pageContent);
				if(pageYearMatcher.find())
					pageYear = pageYearMatcher.group(1);

				// extract year from pageYear string
				String year = null;
				Pattern yearPattern = Pattern.compile(PATTERN_YEAR);
				Matcher yearMatcher = yearPattern.matcher(pageYear);
				if(yearMatcher.find())
					year = yearMatcher.group(1);

				// extarct page from pageYear string
				String page = null;
				Pattern pagePattern = Pattern.compile(PATTERN_PAGE);
				Matcher pageMatcher = pagePattern.matcher(pageYear);
				if(pageMatcher.find())
					page = pageMatcher.group(1);

				/*
				 * build bibtex
				 */
				StringBuffer bibtex = new StringBuffer();
				bibtex.append("@proceedings{");
				
				// build bibtex key
				if(year != null && author != null){
					Pattern lastnamePattern = Pattern.compile(PATTERN_FIRST_LASTNAME);
					Matcher lastnameMatcher = lastnamePattern.matcher(author);
					if(lastnameMatcher.find()) // combination lastname and year
						bibtex.append(lastnameMatcher.group(1)).append(year);
					else // only year
						bibtex.append(year);
				}else // default value
					bibtex.append("jmlrKey");
				bibtex.append(",\n");
				
				// add title
				if(title != null){
					bibtex.append("title = {");
					bibtex.append(title);
					bibtex.append("},\n");
				}
				
				// add author
				if(author != null){
					bibtex.append("author = {");
					bibtex.append(author);
					bibtex.append("},\n");
				}
				
				// add year
				if(year != null){
					bibtex.append("year = {");
					bibtex.append(year);
					bibtex.append("},\n");
				}

				// add page
				if(page != null){
					bibtex.append("page = {");
					bibtex.append(page);
					bibtex.append("},\n");
				}

				// add page
				if(page != null){
					bibtex.append("page = {");
					bibtex.append(page);
					bibtex.append("},\n");
				}

				// add volume
				if(volume != null){
					bibtex.append("volume = {");
					bibtex.append(volume);
					bibtex.append("},\n");
				}
				
				// remove last ","
				bibtex.deleteCharAt(bibtex.lastIndexOf(","));
				
				// last part
				bibtex.append("}");
				
				// finish
				sc.setBibtexResult(bibtex.toString());
				sc.setScraper(this);
				return true;

			}else
				throw new PageNotSupportedException("Select a page with the abtract view from a jmlr paper.");
		}
		
		return false;
	}

}
