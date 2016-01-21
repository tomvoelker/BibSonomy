/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.dlib;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for www.dlib.org
 * @author tst
 */
public class DLibScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(DLibScraper.class);
	
	private static final String SITE_URL = "http://www.dlib.org/";
	private static final String SITE_NAME = "D-Lib";
	private static final String INFO = "Scraper for metadata from " + href(SITE_URL, SITE_NAME)+".";

	/**
	 * D-Lib host
	 */
	private static final String DLIB_HOST = "dlib.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + DLIB_HOST + "$"), AbstractUrlScraper.EMPTY_PATTERN));

	/*
	 * FIXME: refactor all patterns into static Patterns
	 */

	/**
	 * URL ending from a normal publication page (HTML)  
	 */
	private static final String HTML_PAGE = "html";

	/**
	 * URL ending from a meta publication page (XML)  
	 */
	private static final String META_DATA_PAGE = "meta.xml";
	
	/**
	 * dlib title -> bibtex title 
	 */
	private static final String PATTERN_TITLE = "<title>(.*)</title>";

	/**
	 * dlib creator -> bibtex author
	 */
	private static final String PATTERN_CREATOR = "<creator>(.*)</creator>";

	/**
	 * dlib date -> bibtex year & month
	 */
	private static final String PATTERN_DATE = "<date date-type = \"publication\">(.*)</date>";

	/**
	 * Pattern for year (used with dlib date)
	 */
	private static final String PATTERN_YEAR = ".*([0-9]{4}).*";
	
	/**
	 * dlib type -> bibtex type -> not used, article is default content type
	 */
	private static final String PATTERN_TYPE = "<type resource-type = \"work\">(.*)</type>";

	/**
	 * dlib identifier -> bibtex doi & url
	 */
	private static final String PATTERN_IDENTIFIER_DOI = "<identifier uri-type = \"DOI\">(.*)</identifier>";
	
	/**
	 * dlib identifier -> bibtex doi & url
	 */
	private static final String PATTERN_IDENTIFIER_URL = "<identifier uri-type = \"URL\">(.*)</identifier>";
	
	/**
	 * dlib serial-name -> bibtex journal
	 */
	private static final String PATTERN_JOURNAL = "<serial-name>(.*)</serial-name>";
	
	/**
	 * dlib issn -> bibtex issn
	 */
	private static final String PATTERN_ISSN = "<issn>(.*)</issn>";
	
	/**
	 * dlib volume -> bibtex volume
	 */
	private static final String PATTERN_VOLUME = "<volume>(.*)</volume>";
	
	/**
	 * dlib issue -> bibtex issue
	 */
	private static final String PATTERN_ISSUE = "<issue>(.*)</issue>";
	
	/**
	 * pattern for bibtexkey (used with url from scraping context)
	 */
	private static final String PATTERN_BIBTEX_KEY = "dlib/(.*)/(.*)/";
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<H3 class=\"blue\">Abstract</H3>\\s+<p class=\"blue\">\\s+(.*)\\s+</p>");
	
	//Pattern for references
	private static final Pattern REFERENCES = Pattern.compile("(?s)<h3>Notes.*</h3>(.*)<center><h6>Copyright");
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl().getHost().endsWith(DLIB_HOST)) {
			try {
				sc.setScraper(this);
				
				String metaData = null;
				
				// get metadata
				if(sc.getUrl().toString().endsWith(META_DATA_PAGE)){
					metaData = sc.getPageContent();
				}else if(sc.getUrl().toString().endsWith(HTML_PAGE)){
					String metaDataUrl = sc.getUrl().toString().substring(0, sc.getUrl().toString().length()-4) + META_DATA_PAGE;
					metaData = WebUtils.getContentAsString(new URL(metaDataUrl));
				}
				
				// build xml to bibtex
				if (metaData != null) {
					String bibtex = null;
					
					// extract & build bibtex
					bibtex = buildBibtex(metaData, sc.getUrl().toString());
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractParser(sc.getUrl()));
					if(bibtex != null){
						// success 
						sc.setBibtexResult(StringEscapeUtils.unescapeHtml(bibtex));
						return true;
					}
					throw new ScrapingFailureException("getting bibtex failed");

				}
				throw new PageNotSupportedException("This dlib page is not supported.");
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}
		}
		return false;
	}
	
	private static String abstractParser(URL url){
		try{
			Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
			if (m.find()) {
				return m.group(1);
			}
		} catch (final Exception e) {
			log.error("error while getting abstract for " + url, e);
		}
		return null;
	}
	
	private String buildBibtex(String metaData, String publUrl){
		StringBuffer buffer = new StringBuffer();
		
		// article is default content type
		buffer.append("@article{");
		
		// extract key from url 
		for(String keyPart: extractElement(PATTERN_BIBTEX_KEY, publUrl))
			buffer.append(keyPart);
		
		// add title
		List<String> title = extractElement(PATTERN_TITLE, metaData);
		if(title.size() > 0){
			buffer.append(",\ntitle = {");
			buffer.append(title.get(0));
			buffer.append("}");
		}

		// add author
		List<String> authors = extractElement(PATTERN_CREATOR, metaData);
		if(authors.size() > 0){
			buffer.append(",\nauthor = {");
			for(String author: authors){
				buffer.append(author);
				buffer.append(" and ");
			}
			buffer = buffer.delete(buffer.length()-5, buffer.length());
			buffer.append("}");
		}

		List<String> date = extractElement(PATTERN_DATE, metaData);
		if(date.size() > 0){
			// add year
			String year = extractElement(PATTERN_YEAR, date.get(0)).get(0);
			buffer.append(",\nyear = {");
			buffer.append(year);
			buffer.append("}");
			
			// add month
			buffer.append(",\nmonth = {");
			buffer.append(date.get(0).replace(year, ""));
			buffer.append("}");
		}

		// add doi
		List<String> doi = extractElement(PATTERN_IDENTIFIER_DOI, metaData);
		if(doi.size() > 0){
			buffer.append(",\ndoi = {");
			buffer.append(doi.get(0));
			buffer.append("}");
		}

		// add url
		List<String> url = extractElement(PATTERN_IDENTIFIER_URL, metaData);
		if(url.size() > 0){
			buffer.append(",\nurl = {");
			buffer.append(url.get(0));
			buffer.append("}");
		}

		// add journal
		List<String> journal = extractElement(PATTERN_JOURNAL, metaData);
		if(journal.size() > 0){
			buffer.append(",\njournal = {");
			buffer.append(journal.get(0));
			buffer.append("}");
		}

		// add issn
		List<String> issn = extractElement(PATTERN_ISSN, metaData);
		if(issn.size() > 0){
			buffer.append(",\nissn = {");
			buffer.append(issn.get(0));
			buffer.append("}");
		}

		// add volume
		List<String> volume = extractElement(PATTERN_VOLUME, metaData);
		if(volume.size() > 0){
			buffer.append(",\nvolume = {");
			buffer.append(volume.get(0));
			buffer.append("}");
		}

		// add issue
		List<String> issue = extractElement(PATTERN_ISSUE, metaData);
		if(issue.size() > 0){
			buffer.append(",\nnumber = {");
			buffer.append(issue.get(0));
			buffer.append("}");
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}
	
	/**
	 * Extract elements by regex
	 * @param patternString regex
	 * @param metaData publication
	 * @return List with extracted elements
	 */
	private List<String> extractElement(String patternString, String metaData){
		List<String> elements = new LinkedList<String>();
		
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(metaData);
		
		while(matcher.find()){
			int groups = matcher.groupCount();
			for(int i=1; i<=groups; i++)
				elements.add(matcher.group(i));
		}
		
		return elements;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		try {
			final Matcher m = REFERENCES.matcher(WebUtils.getContentAsString(scrapingContext.getUrl()));
			if (m.find()) {
				scrapingContext.setReferences(m.group(1));
				return true;
			}	
		} catch (IOException ex) {
			throw new ScrapingException(ex);
		}
		return false;
	}
	
}
