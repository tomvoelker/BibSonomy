/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.acm;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.util.id.DOIUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Scrapes the ACM digital library
 * @author rja
 *
 */
public class ACMBasicScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "ACM Digital Library";
	private static final String SITE_URL  = "http://portal.acm.org/";
	private static final String info      = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private Log log = LogFactory.getLog(ACMBasicScraper.class);

	private static final String BIBTEX_STRING_ON_ACM = "BibTeX";

	private static final String BROKEN_END = "},\n }";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "portal.acm.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final String P_TAG_CLASS_FONT     = "<\\s*p\\s+class=[\"|\']abstract[\"|\']>\\s*<\\s*font\\s+color.*?<\\s*/\\s*p\\s*>";
	private static final String P_TAG_CLASS          = ".*(<\\s*p\\s+class=[\"|\']abstract[\"|\']>.*)";
	private static final String P_TAG_CLASS_ENCLOSED = ".*<\\s*p\\s+class=[\"|\']abstract[\"|\']>\\s*(.*?)<\\s*/\\s*p\\s*>.*";
	private static final String P_START_TAG          = "(<\\s*p\\s*>)(.*)";
	private static final String P_END_TAG            = "(<\\s*/\\s*p\\s*>)(.*)";
	private static final String HTML_TAG             = "\\<.*?\\>";
	private static final String MULTIPLE_WHITESPACE  = "\\s{2,}";
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		// This Scraper might handle the specified url
		try {

			final StringBuffer bibtexEntries = new StringBuffer("");

			// Parse the page and obtain a DOM
			final Document document = XmlUtils.getDOM(sc.getPageContent());

			// save path to popup page of current bibtex entry
			final List<String> pathsToScrape = extractSinglePath(document); // holds the paths to the popup pages


			/*
			 * Scrape entries from popup BibTeX site. BibTeX entry on these
			 * pages looks like this: <PRE id="155273">@article{155273,
			 * author = {The Author}, title = {This is the title}...}</pre>
			 */
			for (final String path: pathsToScrape) {
				final Document doc = XmlUtils.getDOM(new URL(SITE_URL + path));

				final NodeList pres = doc.getElementsByTagName("pre");
				for (int i = 0; i < pres.getLength(); i++) {
					final Node currNode = pres.item(i);
					final NodeList childnodes = currNode.getChildNodes();
					if (childnodes.getLength() > 0) {
						bibtexEntries.append(" " + currNode.getChildNodes().item(0).getNodeValue());
					}
				}
			}

			/*
			 * Some entries (e.g., http://portal.acm.org/citation.cfm?id=500737.500755) seem
			 * to have broken BibTeX entries with a "," too much at the end. We remove this
			 * here.
			 */
			final int indexOf = bibtexEntries.indexOf(BROKEN_END, bibtexEntries.length() - BROKEN_END.length() - 1);
			if (indexOf > 0) {
				bibtexEntries.replace(indexOf, bibtexEntries.length(), "}\n}");
			}

			
			/*
			 * append URL
			 */
			BibTexUtils.addFieldIfNotContained(bibtexEntries, "url", sc.getUrl().toString());
			/*
			 * at first, try to extract the abstract via DOM
			 */
			String abstrct = extractAbstract(document);
			
			/*
			 * if this fails, try to extract the abstract via REGEX
			 */
			if (abstrct == null) {
				abstrct = extractAbstract(sc);
			}
			
			if (abstrct != null) {
				BibTexUtils.addFieldIfNotContained(bibtexEntries, "abstract", abstrct);
			} else // log if abstract is not available
				log.info("ACMBasicScraper: Abstract not available");

			final String result = DOIUtils.cleanDOI(bibtexEntries.toString().trim());

			if (!"".equals(result)) {
				sc.setBibtexResult(result);
				return true;
			} else
				throw new ScrapingFailureException("getting bibtex failed");
		} catch (Exception e) {
			throw new InternalFailureException(e);
		}
	}

	
	/**
	 * extract the abstract
	 * 
	 * structure is as follows:
	 * 
	 * <div class="abstract">
	 *   <A HREF="citation.cfm?id=345231.345249&coll=Portal&dl=GUIDE&CFID=23770365&CFTOKEN=52785444#CIT">
	 *     <img name="top" src="http://portal.acm.org/images/arrowu.gif" hspace="10" border="0">
	 *   </A>
	 *   <SPAN class=heading>
	 *     <A NAME="abstract">ABSTRACT</A>
	 *   </span>
     *   <p class="abstract">
     *     <p>
     *     
     *       abstract here
     *     </p>
     *   </p>
     * </div>
     * 
     * NOTE: there might be other (empty!) divs with class="abstract". 
     * We have to pick the right one.
     * 
     * ALSO: sometimes the innermost <p> is missing!
     *
	 * @param document
	 * @return
	 */
	private String extractAbstract(final Document document) {
		String abstrct = null;
		final NodeList as1 = document.getElementsByTagName("a"); // get all <a>-Tags
		for (int i = 0; i < as1.getLength(); i++) {
			final Node a = as1.item(i);

			/*
			 * check for "name" attribute which has value "abstract"
			 */
			if (a.hasAttributes()) {
				final Attr name = ((Element)a).getAttributeNode("name");
				if (name != null && "abstract".equals(name.getValue())) {
					/*
					 * we have found the correct <a name="abstract" ... now check its contents
					 * (child text node)
					 */
					if ("ABSTRACT".equals(a.getChildNodes().item(0).getNodeValue())) {
						/*
						 * we now have to find the next <p class="abstract">
						 */
						final Node span = a.getParentNode();
						final Node textNode = span.getNextSibling(); // there is whitespace between the nodes ...
						final Node p = textNode.getNextSibling();    // this should be the <p class="abstract">
						if (p.hasAttributes()) {
							final Attr clazz = ((Element)p).getAttributeNode("class");
							if (clazz != null && "abstract".equals(clazz.getValue())) {
								 /*
								  * Check for the innermost <p>. 
								  *  
								  * Although this <p> which contains the abstract is contained in the previous
								  * <p class="abstract"> in the source, <p>'s can't be nested in the DOM tree.
								  * Hence, we must take the next sibling. 
								  */
								final Node nextSibling = p.getNextSibling();
								if (nextSibling != null && "p".equals(nextSibling.getNodeName())) {
									abstrct = XmlUtils.getText(nextSibling);
								} else {
									/*
									 * If the innermost <p> is missing, we take the text of the <p ...> at hand
									 */
									abstrct = XmlUtils.getText(p);
								}
								break;
							}
						}
					}

				}
			}
		}
		return abstrct;
	}
	
	/**
	 * This method is called when the extraction of the abstract
	 * via DOM is failed.
	 * Here we're trying to get the abstract via REGEX
	 * 
	 * Structure like above
	 * @throws ScrapingException 
	 */
	final String extractAbstract(final ScrapingContext sc) throws ScrapingException {
		String content = sc.getPageContent();
		
		/*
		 * removing whole tags like: <p class"abstract"><font color.....</p>
		 */
		content = content.replaceAll(P_TAG_CLASS_FONT, "");
		
		/*
		 * calculate deep of <p class="abstract"> tag and build the abstract
		 */
		int index = 0;
		
		Pattern p = Pattern.compile(P_TAG_CLASS, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher(content);
		StringBuilder _content = new StringBuilder();
		
		if (m.find()) {
			_content.append(m.group(1));
		}
		
		int pStartIndex = 0;
		int pEndIndex   = 0;
		int deep        = 1;
		int c           = 1;
		
		// it could be < p> or <p>... also </ p> or </p> or < /p> a.s.o.
		int pStartLength = 0;
		int pEndLength   = 0;
		
		// to make searching in html a little bit more secure
		boolean moreStartTags = true;
		boolean updateContent = true;
		
		do {
			p = Pattern.compile(P_END_TAG, Pattern.MULTILINE | Pattern.DOTALL);
			m = p.matcher(_content);
			
			if (m.find(index)) {
				pEndIndex = m.start();
				pEndLength = m.group(1).length();
			}
			
			p = Pattern.compile(P_START_TAG, Pattern.MULTILINE | Pattern.DOTALL);
			m = p.matcher(_content);
			
			if (m.find(index)) {
				pStartIndex = m.start();
				pStartLength = m.group(1).length();
			} else {
				moreStartTags = false;
			}
			
			if (pEndIndex < pStartIndex || !moreStartTags) {
				deep--;
				index = pEndIndex;
				_content.delete(index, pEndLength + index);
			} else {
				index = pStartIndex;
				deep++;
				_content.delete(index, pStartLength + index);
			}
			
			c++;
			
			/*
			 * unfortunately we could enter an endless loop if the html is corrupt
			 * and in this fact, the following lines could make the stuff more secure
			 */
			if (c > 10) {
				updateContent = false;
				break;
			}
		} while (deep > 1); 
		
		if (updateContent) {
			content = _content.toString();
		}
		
		/*
		 * searching for occurrences of <p class="abstract">...</p>
		 */
		p = Pattern.compile(P_TAG_CLASS_ENCLOSED, Pattern.MULTILINE | Pattern.DOTALL);
		m = p.matcher(content);
		
		if (m.matches() && m.group(1) != null) {
			content = m.group(1);
			content = content.trim();
			
			/*
			 *  removing inner paragraphs and other tags
			 */
			p = Pattern.compile(HTML_TAG, Pattern.MULTILINE | Pattern.DOTALL);
			m = p.matcher(content);
			content = m.replaceAll("");
			
			/*
			 * removing linebreaks and multiple whitespaces
			 */
			content = content.replaceAll(MULTIPLE_WHITESPACE, " ");
			
			/*
			 * unescape html characters
			 */
			content = StringEscapeUtils.unescapeHtml(content);
			
			return content;
		}
			
		
		return content;
	}

	/**
	 * Extract the path from the onclick attribute of the <a>-Element including
	 * the value "BibTex". Watch out for changes of this element on ACM sites
	 * otherwise our extraction does not work anymore. On 04.09.2006 the element
	 * looked like this: <a
	 * href="citation.cfm?id=62605&dl=ACM&coll=ACM&CFID=72100071&CFTOKEN=98542378#"
	 * onClick="window.open('popBibTex.cfm?id=62605&ids=SERIES326.62597.62605&types=series.proceeding.article&reqtype=article&coll=ACM&dl=ACM&CFID=72100071&CFTOKEN=98542378',
	 * 'BibTex','width=800,height=100,top=100,left=100,scrollbars=Yes,resizable=yes');"
	 * class="small-link-text">BibTex</a>
	 * 
	 * @param nodeValue
	 *            the node value to extract the url from
	 * @return the extracted path
	 */
	private String extractPathFromOnclickNode(String nodeValue) {
		int firstPrimePos = nodeValue.indexOf("'") + 1;
		return nodeValue.substring(firstPrimePos, nodeValue.indexOf("'", firstPrimePos));
	}

	/**
	 * This method extracts the popup path of current page. We use this method
	 * whenever the snippet is empty.
	 * 
	 * @param doc The document to extract the popup path from.
	 * @param snippet
	 */
	private List<String> extractSinglePath(final Document doc) {
		final List<String> pathsToScrape = new ArrayList<String>(); // holds the paths to the popup pages
		final NodeList as = doc.getElementsByTagName("a");
		for (int i = 0; i < as.getLength(); i++) {
			final Node currNode = as.item(i);
			final NodeList childnodes = currNode.getChildNodes();

			if (childnodes.getLength() > 0) {
				if (BIBTEX_STRING_ON_ACM.equals(currNode.getChildNodes().item(0).getNodeValue())) {
					pathsToScrape.add(extractPathFromOnclickNode(currNode.getAttributes().getNamedItem("onclick").getNodeValue()));
				}
			}
		}
		return pathsToScrape;
	}

	public String getInfo() {
		return info;
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
