/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.url.kde.usenix;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for usenix.org
 * It works only with new publications. Pattterns for old data will be added soon.
 * @author tst
 */
public class UsenixScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(UsenixScraper.class);
	
	private static final String SITE_NAME = "USENIX";
	private static final String SITE_URL = "http://usenix.org/";
	private static final String INFO = "Scraper for papers from events which are postetd on " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String HOST = "usenix.org";
	
	private static final String PATH_1 = "/events/";
	private static final String PATH_2 = "/publications/library/proceedings/.*\\.html";
	
	private static final String PATTERN_YEAR_EVENTS = "/events/.*(\\d{2})/";
	
	private static final String PATTERN_YEAR_PROCEEDING = "/publications/library/proceedings/\\D*(\\d{2})/";
	
	private static final String PATTERN_KEY_EVENTS = "/events/([^/]*)/";
	
	private static final String PATTERN_KEY_PROCEEDING = "/publications/library/proceedings/([^/]*)/";
	
	private static final String CURRENT_PATTERN_GET_TITLE = "<h2>(.*)</h2>";
	
	private static final String CURRENT_PATTERN_GET_AUTHOR = "</h2>(.*)<h3>";
	
	private static final String CURRENT_WITH_BORDER_PATTERN_GET_AUTHOR = "</h2>(.*)<h4>";

	private static final String OLD_PATTERN_GET_AUTHOR = "<PRE>\\s*(.*)";

	private static final String CURRENT_PATTERN_GET_EVENT = "sans-serif\"><b>([^<]*)</b></font>";
	
	private static final String OLD_PATTERN_GET_EVENT = "<title>(.*)</title>";
	
	private static final String CURRENT_PATTERN_GET_PAGES = "<b>Pp.(.*)</b>";

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>(); 
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("(?i)(?s)<H\\d>Abstract</H\\d>(.*)\\s+(<LI>View|View|Download) the full text");
	static {
		final Pattern hostPattern = Pattern.compile(".*" + HOST);
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(PATH_1 + ".*")));
		patterns.add(new Pair<Pattern, Pattern>(hostPattern, Pattern.compile(PATH_2)));
	}
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
			sc.setScraper(this);
			
			try {
				String path = sc.getUrl().getPath();

				String title = null;
				String author = null;
				String event = null;
				String pages = null;
				String year = null;
				String key = null;
				

					/*
					 * examples for current event/proceeding layout:
					 * http://usenix.org/events/sec07/tech/drimer.html
					 * http://usenix.org/publications/library/proceedings/tcl97/libes_writing.html
					 * 
					 * TODO:
					 * http://www.usenix.org/events/evt07/tech/full_papers/sandler/sandler_html/
					 */
					
					String content = null;
					try {
						/*
						 * Force the content type to be the null value
						 */
						HttpClient client = WebUtils.getHttpClient();
						GetMethod getmethod = new GetMethod(sc.getUrl().toExternalForm());
						if (client.executeMethod(getmethod) == HttpStatus.SC_OK) {
							content = WebUtils.inputStreamToStringBuilder(getmethod.getResponseBodyAsStream(), null).toString();
						}
					} catch (IOException ex) {
						throw new ScrapingException(ex);
					}
					
					if (!present(content)) throw new ScrapingException("content not available");
					
					/*
					 * Pattern
					 */
					
					// get year and key (event page)
					if(path.startsWith("/events/")){
						// get year
						Pattern yearPattern = Pattern.compile(PATTERN_YEAR_EVENTS);
						Matcher yearMatcher = yearPattern.matcher(path);
						if(yearMatcher.find())
							year = expandYear(yearMatcher.group(1));
						
						//get key
						Pattern keyPattern = Pattern.compile(PATTERN_KEY_EVENTS);
						Matcher keyMatcher = keyPattern.matcher(path);
						if(keyMatcher.find())
							key = keyMatcher.group(1);

					// get year and key (proceeding page)
					}else if(path.startsWith("/publications/library/proceedings/")){
						// get year
						Pattern yearPattern = Pattern.compile(PATTERN_YEAR_PROCEEDING);
						Matcher yearMatcher = yearPattern.matcher(path);
						if(yearMatcher.find())
							year = expandYear(yearMatcher.group(1));
						
						//get key
						Pattern keyPattern = Pattern.compile(PATTERN_KEY_PROCEEDING);
						Matcher keyMatcher = keyPattern.matcher(path);
						if(keyMatcher.find())
							key = keyMatcher.group(1);
						
					}
					
					// get title
					Pattern titlePattern = Pattern.compile(CURRENT_PATTERN_GET_TITLE, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					Matcher titleMatcher = titlePattern.matcher(content);
					if(titleMatcher.find())
						title = cleanup(titleMatcher.group(1), false);
					
					// get author
					Pattern authorPattern = Pattern.compile(CURRENT_PATTERN_GET_AUTHOR, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					Matcher authorMatcher = authorPattern.matcher(content);
					if(authorMatcher.find())
						author = cleanup(authorMatcher.group(1), true);
					else{
						/*
						 * matching for different layout
						 * example: http://usenix.org/publications/library/proceedings/ec96/geer.html
						 */
						authorPattern = Pattern.compile(CURRENT_WITH_BORDER_PATTERN_GET_AUTHOR, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
						authorMatcher = authorPattern.matcher(content);
						if(authorMatcher.find()){
							author = cleanup(authorMatcher.group(1), true);
							author = author.replace("<HR>", "");
							author = author.replace("<hr>", "");
							author = author.replace("<P>", "");
							author = author.replace("<p>", "");
							
							// because of this: http://usenix.org/publications/library/proceedings/mob95/raja.html
							if(author.contains("<PRE>")){
								authorPattern = Pattern.compile(OLD_PATTERN_GET_AUTHOR, Pattern.CASE_INSENSITIVE);
								authorMatcher = authorPattern.matcher(content);
								if(authorMatcher.find()){
									author = cleanup(authorMatcher.group(1), true);
									author = author.replaceAll("\\s{2,}", " and ");
								}
							}
						}
					}
					if(author!=null){
						// replace "\n" with "and"
						author = author.replace("\n", " and ");
						// replace "," with "and"
						author = author.replace(",", " and ");
						// and cleanup
						while(author.contains("and  and"))
							author = author.replaceAll("and\\s*and", "and");
						if(author.endsWith(" and "))
							author = author.substring(0, author.length()-5);
						if(author.startsWith(" and "))
							author = author.substring(5);
					}
					
					// get event
					Pattern eventPattern = Pattern.compile(CURRENT_PATTERN_GET_EVENT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher eventMatcher = eventPattern.matcher(content);
					if(eventMatcher.find()){
						event = cleanup(eventMatcher.group(1), false);
						event = event.replace("\n", "");
					}else{
						// old layout example: http://usenix.org/publications/library/proceedings/mob95/raja.html
						eventPattern = Pattern.compile(OLD_PATTERN_GET_EVENT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
						eventMatcher = eventPattern.matcher(content);
						if(eventMatcher.find()){
							event = cleanup(eventMatcher.group(1), false);
							event = event.replace("\n", "");
						}
					}
					
					// get pages
					Pattern pagesPattern = Pattern.compile(CURRENT_PATTERN_GET_PAGES, Pattern.CASE_INSENSITIVE);
					Matcher pagesMatcher = pagesPattern.matcher(content);
					if(pagesMatcher.find())
						pages = cleanup("Pp." + pagesMatcher.group(1), false);
					
					/*
					 * TODO: may be abstract also
					 * String abstract = null;
					 */
					
					StringBuffer resultBibtex = new StringBuffer();
					
					if(key != null)
						resultBibtex.append("@inproceedings{" + key + ",\n");
					else
						resultBibtex.append("@inproceedings{usenix,\n");
					
					if(author != null)
						resultBibtex.append("\tauthor = {" + author + "},\n");
					if(title != null)
						resultBibtex.append("\ttitle = {" + title + "},\n");
					if(year != null)
						resultBibtex.append("\tyear = {" + year + "},\n");
					if(event != null)
						resultBibtex.append("\tseries = {" + event + "},\n");
					if(pages != null)
						resultBibtex.append("\tpages = {" + pages + "},\n");
						
					String bibResult = resultBibtex.toString();
					bibResult = bibResult.substring(0, bibResult.length()-2) + "\n}\n";

					// append url
					bibResult = BibTexUtils.addFieldIfNotContained(bibResult, "url", sc.getUrl().toString());
					bibResult = BibTexUtils.addFieldIfNotContained(bibResult, "abstract", abstractParser(sc.getUrl()));
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibResult);
					return true;
					
			} catch (UnsupportedEncodingException ex) {
				throw new InternalFailureException(ex);
			}
	}
	
	private static String abstractParser(URL url){
		try {
			Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				return m.group(1);
			}
		} catch(Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}

	/**
	 * Removes some HTML-Elements and Codings which are not needed in Bibtex
	 * @param bibContent bibliographic information as HTML
	 * @param cut flag for cutting off the conten between <i></i>
	 * @return bibliographic information without HTML-Elements
	 * @throws UnsupportedEncodingException
	 */
	private String cleanup(String bibContent, boolean cut) throws UnsupportedEncodingException{
		bibContent = bibContent.replace("&#150;", "-");
		bibContent = StringEscapeUtils.unescapeHtml(bibContent);
		
		bibContent = bibContent.replaceAll("<!-- CHANGE -->", "");
		
		if(cut){
			int indexStartI = -1;
			int indexEndI = -1;

			do{
				
				// cut all content between <i> and </i>
				indexStartI = bibContent.indexOf("<i>");
				if(indexStartI == -1)
					indexStartI = bibContent.indexOf("<I>");
				
				indexEndI = bibContent.indexOf("</i>");
				if(indexEndI == -1)
					indexEndI = bibContent.indexOf("</I>");
				
				if(indexStartI != -1){
					// cut string
					String firstSection = bibContent.substring(0, indexStartI);
					
					String secondSection = ""; 
					if(indexEndI != -1)// has end tag
						secondSection = bibContent.substring(indexEndI +4);
					// else no end tag, remove rest of the string (rest of the string is "")
					
					// concat
					bibContent = firstSection + secondSection;
				}
			
			// only start i tag is importent (may be closing tag ist missing)
			}while(indexStartI != -1);
		}else{
			bibContent = bibContent.replaceAll("<i>", "");
			bibContent = bibContent.replaceAll("<I>", "");
			bibContent = bibContent.replaceAll("</i>", "");
			bibContent = bibContent.replaceAll("</I>", "");
		}

		bibContent = bibContent.replace("<BR>", "\n");
		bibContent = bibContent.replace("<br>", "\n");
		
		return bibContent.trim();
	}
	
	/**
	 * expand a given decade to a complete year
	 * @param decade the last two numbers of a year (with 4 digits)
	 * @return decade expande with 199X or 20XX
	 */
	private String expandYear(String decade){
		/*
		 * TODO:
		 * Problem with year 21XX
		 */
		if(decade.startsWith("9"))
			return "19" + decade;
		else
			return "20" + decade;
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
