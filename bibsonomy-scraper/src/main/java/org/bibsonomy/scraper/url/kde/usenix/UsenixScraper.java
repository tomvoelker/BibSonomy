package org.bibsonomy.scraper.url.kde.usenix;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;

/**
 * Scraper for usenix.org
 * It works only with new publications. Pattterns for old data will be added soon.
 * @author tst
 */
public class UsenixScraper implements Scraper {
	
	private static final String INFO = "Scraper for papaers from events which are postetd on usenix.org";
	
	private static final String HOST = "usenix.org";
	
	private static final String PATTERN_GET_TITLE = "<h2>(.*)</h2>";
	
	private static final String PATTERN_GET_AUTHOR = "</h2>(.*)<h3>";
	
	private static final String PATTERN_GET_EVENT = "sans-serif\"><b>(.*)</b></font>";
	
	private static final String PATTERN_GET_PAGES = "<b>Pp.(.*)</b>";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(HOST)){
			String path = sc.getUrl().getPath();
			try {
				if(path.startsWith("/events/") && path.endsWith(".html")){
					/*
					 * examples for current event layout:
					 * http://usenix.org/events/sec07/tech/drimer.html
					 */
					
					String content = sc.getPageContent();
					
					String title = null;
					String author = null;
					String event = null;
					String pages = null;
					
					/*
					 * Pattern
					 */
					
					Pattern titlePattern = Pattern.compile(PATTERN_GET_TITLE, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					Matcher titleMatcher = titlePattern.matcher(content);
					if(titleMatcher.find())
						title = cleanup(titleMatcher.group(1), false);
					
					Pattern authorPattern = Pattern.compile(PATTERN_GET_AUTHOR, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					Matcher authorMatcher = authorPattern.matcher(content);
					if(authorMatcher.find())
						author = cleanup(authorMatcher.group(1), true);
					
					Pattern eventPattern = Pattern.compile(PATTERN_GET_EVENT, Pattern.CASE_INSENSITIVE);
					Matcher eventMatcher = eventPattern.matcher(content);
					if(eventMatcher.find())
						event = cleanup(eventMatcher.group(1), false);
					
					Pattern pagesPattern = Pattern.compile(PATTERN_GET_PAGES, Pattern.CASE_INSENSITIVE);
					Matcher pagesMatcher = pagesPattern.matcher(content);
					if(pagesMatcher.find())
						pages = cleanup("Pp." + pagesMatcher.group(1), false);
					
					/*
					 * TODO: may be abstract also
					 * String abstract = null;
					 */
					
					StringBuffer resultBibtex = new StringBuffer();
					resultBibtex.append("@inproceedings{usenix,\n");			
					
					if(author != null)
						resultBibtex.append("\tauthor = {" + author + "},\n");
					if(title != null)
						resultBibtex.append("\ttitle = {" + title + "},\n");
					if(event != null)
						resultBibtex.append("\tseries = {" + event + "},\n");
					if(pages != null)
						resultBibtex.append("\tpages = {" + pages + "},\n");
						
					String bibResult = resultBibtex.toString();
					bibResult = bibResult.substring(0, bibResult.length()-2) + "\n}\n";

					sc.setBibtexResult(bibResult);
					sc.setScraper(this);
					return true;
					
				}else if(false){
					// Url pattern for older proceedings
					
					/*
					 * TODO:
					 * 
					 * simple version from current layout
					 * http://usenix.org/publications/library/proceedings/sd96/wilkes.html
					 * 
					 * old collection layout
					 * http://usenix.org/publications/library/proceedings/sd93/#uhler
					 * 
					 * current layout, but no event
					 * http://usenix.org/publications/library/proceedings/tcl97/libes_writing.html
					 * 
					 */
					
				}else
					throw new ScrapingException("Not supported usenix url!");
			} catch (UnsupportedEncodingException ex) {
				throw new ScrapingException("Decoding failure in UsenixScraper");
			}
		}
		return false;
	}

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

		return bibContent.trim();
	}
	
}
