/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.digitalhumanities;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Scraper for the "Zeitschrift für digitale Geisteswissenschaften" (ZfdG) webpages
 *  
 * @author Andreas Lüschow
 */
public class ZfdGScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(ZfdGScraper.class);

	private static final String AUTHOR_LAST_KEY = "rft.aulast";
	private static final String AUTHOR_FIRST_KEY = "rft.aufirst";
	private static final String A_TITLE = "rft.atitle";
	private static final String TITLE = "rft.title";
	private static final String DATE_KEY = "rft.date";
	private static final String END_PAGE_KEY = "rft.epage";
	private static final String START_PAGE_KEY = "rft.spage";
	private static final String PAGES_KEY = "rft.pages";
	private static final String ENTRY_TYPE_KEY = "rft_val_fmt";
	private static final String GENRE = "rft.genre";
	private static final String RFT_AU = "rft.au";
	private static final String DOI = "rft_id";
	
	private static final String SITE_NAME = "ZfdG";
	private static final String SITE_URL = "http://zfdg.de/";
	private static final String ZFDG_HOST = "zfdg.de";
	private static final String INFO = "<a href=\"http://zfdg.de/\">ZfdG</a> Scraper: Scraper for ZfdG journal.";

	private static final String PATTERN_HTML_TAG = "</?\\s*+\\w++.*?>";
	private static final Pattern PATTERN_COINS = Pattern.compile("<span class=\"Z3988\" title=\"([^\"]*)\"");
	private static final Pattern PATTERN_TEI_XML = Pattern.compile("<div id=\"xml_but\" class=\"but\"><a href=\"([^\"]*)\"");
	private static final Pattern PATTERN_KEY_VALUE = Pattern.compile("([^=]*)=(([^&]|&(?!amp;))*)(&amp;|&)?");
	private static final Pattern PATTERN_DATE = Pattern.compile("(\\d{4})");
	private static final Pattern PATTERN_DOI = Pattern.compile("http://dx.doi.org/(.*)");
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<h1>Abstract</h1>(([^<]|<(?!/p>))*)</p>", Pattern.DOTALL);
	// TODO: weitere Abstracts berücksichtigen ("<div id=\"abstract_de\" class=\"abstract\"><h1>Abstract</h1>(([^<]|<(?!/p>))*)</p>" funktioniert nicht...)

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS; 

	static {
		URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + ZFDG_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	@Override
	public boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {

		final String page = sc.getPageContent();
		final StringBuffer bibtex = new StringBuffer();
		final Matcher matcherCoins = PATTERN_COINS.matcher(page);
		final Matcher matcherTEI = PATTERN_TEI_XML.matcher(page);
		final boolean isZfdGPage = sc.getUrl().getHost().endsWith(ZFDG_HOST);

		/*
		 * start scraping if Coins element is found on ZfdG page
		 */
		if (isZfdGPage && matcherCoins.find()) {
			sc.setScraper(this);
			
			final String titleValue = matcherCoins.group(1);

			// store all key/value tuples
			final Map<String, String> tuples = new HashMap<>();
			final Matcher m = PATTERN_KEY_VALUE.matcher(titleValue);

			// search tuples and store key-value pairs in map
			while (m.find()) {
				final String key = UrlUtils.safeURIDecode(m.group(1));
				String value = UrlUtils.safeURIDecode(m.group(2));	
				// store only values which are not null and not empty
				if (present(key) && present(value)) {
					// rft.au is repeatable
					if (key.equals(RFT_AU) && tuples.containsKey(RFT_AU)) {
						value = tuples.get(RFT_AU) + PersonNameUtils.PERSON_NAME_DELIMITER + value;
					}
					tuples.put(key, value);
				}
			}

			// get author
			final StringBuilder authorBuf = new StringBuilder();
			if (tuples.containsKey(RFT_AU)) {
				authorBuf.append(tuples.get(RFT_AU));
			}
			if (tuples.containsKey(AUTHOR_FIRST_KEY) || tuples.containsKey(AUTHOR_LAST_KEY)) {
				final String au = getAuthorFirstLast(tuples.get(AUTHOR_FIRST_KEY), tuples.get(AUTHOR_LAST_KEY));
				if (authorBuf.length() == 0) {
					authorBuf.append(au);
				} else {
					authorBuf.insert(0, PersonNameUtils.PERSON_NAME_DELIMITER).insert(0, au);
				}
			}
			final String author = authorBuf.toString();

			// get title
			String title = null;
			if (tuples.containsKey(A_TITLE)) {
				title = tuples.get(A_TITLE);
			} else {
				title = tuples.get(TITLE);
			}
			// remove formatting mistakes from title string
			String mistake = "&#xA;";  // HTML entity for line feed (\n)
			title = title.replace(mistake, " ");
			title = cleanText(title);  // remove unnecessary whitespaces

			// get year
			String year = null;
			if (tuples.containsKey(DATE_KEY)) {
				// get year from date
				final Matcher dateMatcher = PATTERN_DATE.matcher(tuples.get(DATE_KEY));
				if (dateMatcher.find()) {
					year = dateMatcher.group(1);
				}
			}

			// get pages
			String pages = null;
			if (tuples.containsKey(PAGES_KEY)) {
				pages = tuples.get(PAGES_KEY);
			} else if (tuples.containsKey(START_PAGE_KEY) && tuples.containsKey(END_PAGE_KEY)) {
				final String spage = tuples.get(START_PAGE_KEY);
				final String epage = tuples.get(END_PAGE_KEY);
				pages = spage + "--" + epage;
			}
			
			// get doi
			String doi = null;
			if (tuples.containsKey(DOI)) {
				final Matcher doiMatcher = PATTERN_DOI.matcher(tuples.get(DOI));
				if (doiMatcher.find()) {
					doi = doiMatcher.group(1);
				}
			}
			
			// get abstract
			String abstr = "";
			final Matcher abstrMatcher = PATTERN_ABSTRACT.matcher(page);
			if (abstrMatcher.find()) {
				abstr = abstrMatcher.group(1);
				abstr = cleanText(abstr);  // remove unnecessary whitespaces
			}

			// start building BibTex entry
			if (tuples.containsKey(ENTRY_TYPE_KEY)) {
				final String entryType = tuples.get(ENTRY_TYPE_KEY);
				final String genre = tuples.get(GENRE);
				if (entryType.contains(":journal") || genre.contains("article") || genre.contains("bookitem")) {
					final String journal = get(tuples, "rft.title");
					bibtex.append("@article{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");
					if (journal != null) {
						append("journal", journal, bibtex); 
					} else {
						append("journal", get(tuples, "rft.series"), bibtex);
					}
				} else if (entryType.contains(":book")) {
					final String btitle = get(tuples, "rft.btitle");
					bibtex.append("@book{").append(BibTexUtils.generateBibtexKey(author, null, year, btitle)).append(",\n");
					if (btitle != null) append("booktitle", btitle, bibtex);
				} else {
					bibtex.append("@misc{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");
				}
				
				append("title", title, bibtex);
				append("author", author, bibtex);
				append("year", year, bibtex);
				append("volume", get(tuples, "rft.volume"), bibtex);
				append("number", get(tuples, "rft.issue"), bibtex);
				append("pages", pages, bibtex);
				append("abstract", abstr, bibtex);
				append("doi", doi, bibtex);

				bibtex.append("\n}\n");
			}

			return returnBibTeX(sc, bibtex);
			
			/*
			 * if no Coins element is found on ZfdG page, start scraping with TEI-XML representation of the article
			 */
		}  else if (isZfdGPage && matcherTEI.find()) {
			sc.setScraper(this);
			
			final String xmlTEILink = matcherTEI.group(1);
			final String TEIUrl = SITE_URL + xmlTEILink;
			
			if (!present(TEIUrl)) {
				log.error("can't parse publication");
				return false;
			}
			
			try {
				// initialize basic XML parsing objects
				final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				final DocumentBuilder db = dbf.newDocumentBuilder();
				final Document doc = db.parse(new URL(TEIUrl).openStream());
				final XPathFactory xPathfactory = XPathFactory.newInstance();
				final XPath xpath = xPathfactory.newXPath();
				
				// get author
				final XPathExpression persname = xpath.compile("//biblStruct/analytic/respStmt/resp/persName/name");
				final NodeList nlPersname = (NodeList)persname.evaluate(doc, XPathConstants.NODESET);
				final StringBuilder authorBuf = new StringBuilder();
				for (int i = 0; i < nlPersname.getLength(); i++) {
					final String authorName = nlPersname.item(i).getTextContent();
					if (authorBuf.length() == 0) {
						authorBuf.append(authorName);
					} else {
						authorBuf.insert(0, PersonNameUtils.PERSON_NAME_DELIMITER).insert(0, authorName);
					}
				}
				final String author = cleanText(authorBuf.toString());
				
				// get title
				final String title = cleanText(doc.getElementsByTagName("title").item(1).getTextContent());
				// get year
				final String year = cleanText(doc.getElementsByTagName("date").item(0).getTextContent().substring(6));
				// get abstract
				String abstr = getXMLElement(doc, xpath, "//argument", 0);
				// get journal name 
				final String journal = getXMLElement(doc, xpath, "//monogr/title", 0);
				// get volume
				final String volume = getXMLElement(doc, xpath, "//monogr/title", 1);
				// get doi
				final String doi = getXMLElement(doc, xpath, "//analytic/idno", 0);
				
				bibtex.append("@article{").append(BibTexUtils.generateBibtexKey(author, null, year, title)).append(",\n");			
					append("title", title, bibtex);
					append("author", author, bibtex);
					append("year", year, bibtex);
					append("volume", volume, bibtex);
					append("abstract", abstr, bibtex);
					append("journal", journal, bibtex);
					append("doi", doi, bibtex);
				bibtex.append("\n}\n");
				
				return returnBibTeX(sc, bibtex);

			} catch (final IOException | DOMException | ParserConfigurationException | XPathExpressionException | SAXException | ScrapingFailureException e) {
				throw new InternalFailureException(e);
			}
		} 
		return false;
		
	}
	
	/*
	 * remove substrings from a given string
	 */
	private static String cleanText(String txt) {
		txt = txt.replaceAll("\\s+", " ").trim();  // remove unnecessary whitespaces
		txt = txt.replaceAll(PATTERN_HTML_TAG, "");  // remove HTML tags
		txt = txt.replaceAll("&nbsp;", " ");  // replace HTML entity
		txt = txt.replaceAll("&lt;", "<");  // replace HTML entity
		txt = txt.replaceAll("&gt;", ">");  // replace HTML entity
		return txt;
	} 
	
	/*
	 * get text from a XML node
	 */
	private static String getXMLElement(Document doc, XPath xpath, String path, int itemNr) {
		try {
			final XPathExpression pathExpr = xpath.compile(path);
			NodeList nl = (NodeList)pathExpr.evaluate(doc, XPathConstants.NODESET);
			String nodetext = cleanText(nl.item(itemNr).getTextContent());
			return nodetext;
		} catch (XPathExpressionException e) {
			log.error("XPath not valid or reachable", e);
		}
		return null;
	}
	
	/*
	 * return BibTeX
	 */
	private static boolean returnBibTeX(final ScrapingContext sc, final StringBuffer bibtex) throws ScrapingFailureException {
		if (present(bibtex)) {
			// append url
			BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
			// add bibtex to result 
			sc.setBibtexResult(bibtex.toString());
			return true;
		}
		throw new ScrapingFailureException("span does not contain a book or journal");
	}
	
	private static String getAuthorFirstLast(final String aufirst, final String aulast) {
		if (present(aufirst)) {
			if (present(aulast)) {
				return aulast + ", " + aufirst;
			}
			return aufirst;
		} else if (present(aulast)) {
			return aulast;
		}
		return "";
	}

	private static void append(final String fieldName, final String fieldValue, final StringBuffer bibtex) {
		if (present(fieldValue)) {
			bibtex.append(fieldName).append(" = {").append(fieldValue).append("},\n");
		}
	}

	private static String get(final Map<String, String> tuples, final String key) {
		if (tuples.containsKey(key)) {
			return tuples.get(key);
		}
		return null;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singleton(this);
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		if (present(sc.getUrl())) {
			try {
				return (PATTERN_COINS.matcher(sc.getPageContent()).find()) || (PATTERN_TEI_XML.matcher(sc.getPageContent()).find());
			} catch (final ScrapingException ex) {
				return false;
			}
		}
		return false;
	}

	@Override
	public String getInfo() {
		return INFO;
	}
	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return SITE_URL;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

}
