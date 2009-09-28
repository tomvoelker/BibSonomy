package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for csdl2.computer.org
 * @author tst
 */ 
public class IEEEComputerSocietyScraper extends AbstractUrlScraper {

	private static final String INFO = "IEEE compüuter society Scraper: Scraper for publications from " + href("http://www2.computer.org/portal/web/guest/home", "IEEE Computer Society");
	private static final String HOST_OLD= "csdl2.computer.org";
	private static final String HOST_NEW = "computer.org";

	private static final String PATTERN_HREF = "href=\"[^\"]*\"";

	private static final String LINK_SUFFIX = "BibTex</A>";
	
	private static final String DOWNLOAD_URL = "http://www2.computer.org/plugins/dl/doi/";
	
	private static final Pattern bibtexPattern = Pattern.compile("<div id=\"bibText-content\">(.*})</div>", Pattern.DOTALL);
	
	private static final Pattern doiPattern1 = Pattern.compile("doi/(.*)");
	private static final Pattern doiPattern2 = Pattern.compile("\\&DOI=([^\\&]*)");

	public String getInfo() {
		return INFO;
	}
	
	private static final List<Tuple<Pattern, Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();
	
	static{
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST_OLD), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST_NEW), AbstractUrlScraper.EMPTY_PATTERN));
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String doi = null;
		Matcher doi1Matcher = doiPattern1.matcher(sc.getUrl().toString());
		Matcher doi2Matcher = doiPattern2.matcher(sc.getUrl().toString());
		if(doi1Matcher.find())
			doi = doi1Matcher.group(1);
		else if(doi2Matcher.find())
			doi = doi2Matcher.group(1);
		
		if(doi != null){
			try {
				if(doi.contains("#"))
					doi = doi.substring(0, doi.indexOf("#"));
				
				final String page = WebUtils.getContentAsString(new URL(DOWNLOAD_URL + doi));

				String bibtex = null;
				Matcher bibtexMatcher = bibtexPattern.matcher(page);
				if(bibtexMatcher.find())
					bibtex = bibtexMatcher.group(1).trim();

				if(bibtex != null){
					bibtex = bibtex.replace("<br>", "\n");
					bibtex = bibtex.replace("<xsl:text>", "");
					bibtex = bibtex.replace("</xsl:text>", "");
					bibtex = bibtex.replace("&nbsp;", " ");
	
					bibtex = URLDecoder.decode(bibtex, "UTF-8");
	
					// append url
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtex);
					sc.setScraper(this);
					return true;
				}else
					throw new ScrapingFailureException("Cannot download bibtex.");
				
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return "IEEE Computer Society";
	}

	public String getSupportedSiteURL() {
		return "http://www2.computer.org/portal/web/guest/home";
	}
}
