package org.bibsonomy.scraper.url.kde.dblp;

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
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class DBLPScraper implements Scraper, UrlScraper {

	private static final String info = "DBLP Scraper: This scraper parses a publication page from the <a href=\"http://search.mpi-inf.mpg.de/dblp/\">University of Trier Digital Bibliography & Library Project</a> " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String DBLP_HOST1  = "dblp.uni-trier.de";
	private static final String DBLP_HOST_NAME1  = "http://dblp.uni-trier.de";
	private static final String DBLP_HOST_NAME2  = "http://search.mpi-inf.mpg.de/dblp/";
	private static final String DBLP_HOST2  = "search.mpi-inf.mpg.de";
	private static final String DBLP_PATH2  = "/dblp/";
	/*
	 * These are no mirrors, they just link to above hosts
	 */
	/*
	private static final String DBLP_HOST_NAME3  = "http://www.sigmod.org/dblp/";
	private static final String DBLP_HOST_NAME4  = "http://www.vldb.org/dblp/";
	private static final String DBLP_HOST_NAME5  = "http://sunsite.informatik.rwth-aachen.de/dblp/";
	*/
	private static final Pattern DBLP_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);
	
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				
				sc.setScraper(this);
				
				final String url = sc.getUrl().toString();

				//Filtering the <a href="...">DBLP</a>: links out of the content
				int beginDBLPLink = sc.getPageContent().indexOf("<a href=\"http://www.informatik.uni-trier.de/~ley/db/about/bibtex.html\">");
				int endDBLPLink = sc.getPageContent().indexOf("DBLP</a>:");
				
				String pageContent = new String(sc.getPageContent().substring(0, beginDBLPLink) + sc.getPageContent().substring(endDBLPLink+9));
		
				
				final Matcher m = DBLP_PATTERN.matcher(pageContent);	
				if (m.matches()) {
					sc.setBibtexResult(m.group(1));
					return true;
				}else
					throw new PageNotSupportedException("no bibtex snippet available");

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
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST1), UrlScraper.EMPTY_PATTERN));
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST2), Pattern.compile(DBLP_PATH2 + ".*")));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}

