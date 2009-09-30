package org.bibsonomy.scraper.url.kde.dblp;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class DBLPScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "University of Trier Digital Bibliography & Library Project";
	private static final String DBLP_HOST_NAME1  = "http://dblp.uni-trier.de";
	private static final String SITE_URL  = DBLP_HOST_NAME1+"/";
	private static final String info = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String DBLP_HOST1  = "dblp.uni-trier.de";
	private static final String DBLP_HOST_NAME2  = "http://search.mpi-inf.mpg.de/dblp/";
	private static final String DBLP_HOST2  = "search.mpi-inf.mpg.de";
	private static final String DBLP_PATH2  = "/dblp/";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST1), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + DBLP_HOST2), Pattern.compile(DBLP_PATH2 + ".*")));
	}
	
	/*
	 * These are no mirrors, they just link to above hosts
	 */
	/*
	private static final String DBLP_HOST_NAME3  = "http://www.sigmod.org/dblp/";
	private static final String DBLP_HOST_NAME4  = "http://www.vldb.org/dblp/";
	private static final String DBLP_HOST_NAME5  = "http://sunsite.informatik.rwth-aachen.de/dblp/";
	 */
	private static final Pattern DBLP_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);


	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		//Filtering the <a href="...">DBLP</a>: links out of the content
		int beginDBLPLink = sc.getPageContent().indexOf("<a href=\"http://dblp.uni-trier.de/db/about/bibtex.html\">");
		int endDBLPLink = sc.getPageContent().indexOf("DBLP</a>:");

		final String pageContent = new String(sc.getPageContent().substring(0, beginDBLPLink) + sc.getPageContent().substring(endDBLPLink+9));


		final Matcher m = DBLP_PATTERN.matcher(pageContent);	
		if (m.matches()) {
			StringBuffer bibtexResult = new StringBuffer(m.group(1));
			
			// append url
			BibTexUtils.addFieldIfNotContained(bibtexResult, "url", sc.getUrl().toString());
			
			// add downloaded bibtex to result 
			sc.setBibtexResult(bibtexResult.toString().trim());
			
			return true;
		}else
			throw new PageNotSupportedException("no bibtex snippet available");

	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
}

