package org.bibsonomy.scraper.url.kde.acm;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.util.BibTeXUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/** Scrapes the ACM digital library
 * @author rja
 *
 */
public class ACMBasicScraper extends UrlScraper {
	
	private Logger log = Logger.getLogger(ACMBasicScraper.class);
	
	private static final String info = "ACM Scraper: This scraper parses a publication page from the " + href("http://portal.acm.org/portal.cfm", "ACM Digital Library");

	private static final String ACM_HOST_NAME        = "http://portal.acm.org/";
	private static final String BIBTEX_STRING_ON_ACM = "BibTex";

	private static final String BROKEN_END = "},\n }";


	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "portal.acm.org"), UrlScraper.EMPTY_PATTERN));

	private List<String> pathsToScrape; // holds the paths to the popup pages

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		// This Scraper might handle the specified url
		try {

			final StringBuffer bibtexEntries = new StringBuffer("");
			pathsToScrape  = new ArrayList<String>();

			// Parse the page and obtain a DOM
			final Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false); // turn off warning lines
			Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);
			// save path to popup page of current bibtex entry
			extractSinglePath(doc);

			/*
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
			 * 
			 */
			String abstrct = null;
			final NodeList as1 = doc.getElementsByTagName("a"); // get all <a>-Tags
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

			/*
			 * Scrape entries from popup BibTeX site. BibTeX entry on these
			 * pages looks like this: <PRE id="155273">@article{155273,
			 * author = {The Author}, title = {This is the title}...}</pre>
			 */
			for (String path: pathsToScrape) {
				doc = tidy.parseDOM(new ByteArrayInputStream(WebUtils.getContentAsString(new URL(ACM_HOST_NAME + path)).getBytes()), null);

				NodeList pres = doc.getElementsByTagName("pre");
				for (int i = 0; i < pres.getLength(); i++) {
					Node currNode = pres.item(i);
					NodeList childnodes = currNode.getChildNodes();
					if (childnodes.getLength() > 0) {
						bibtexEntries.append(" " + currNode.getChildNodes().item(0).getNodeValue());
					}
				}
			}

			/*
			 * Some entries (e.g., http://portal.acm.org/citation.cfm?id=500737.500755) seem
			 * to have broken BibTeX entries with an "," too much at the end. We remove this
			 * here.
			 */
			final int indexOf = bibtexEntries.indexOf(BROKEN_END, bibtexEntries.length() - BROKEN_END.length() - 1);
			if (indexOf > 0) {
				bibtexEntries.replace(indexOf, bibtexEntries.length(), "}\n}");
			}

			
			/*
			 * append URL
			 */
			BibTeXUtils.addFieldIfNotContained(bibtexEntries, "url", sc.getUrl().toString());
			/*
			 * append abstract
			 */
			if (abstrct != null) {
				BibTeXUtils.addFieldIfNotContained(bibtexEntries, "abstract", abstrct);
			} else // log if abstract is not available
				log.info("ACMBasicScraper: Abstract not available \n" + sc.getPageContent());

			final String result = bibtexEntries.toString().trim();

			if (!"".equals(result)) {
				sc.setBibtexResult(result);
				return true;
			} else
				throw new ScrapingFailureException("getting bibtex failed");
		} catch (Exception me) {
			me.printStackTrace();
			throw new InternalFailureException(me);
		}
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
	private void extractSinglePath(Document doc) {
		NodeList as = doc.getElementsByTagName("a");
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);
			NodeList childnodes = currNode.getChildNodes();

			if (childnodes.getLength() > 0) {
				if (BIBTEX_STRING_ON_ACM.equals(currNode.getChildNodes().item(0).getNodeValue())) {
					pathsToScrape.add(extractPathFromOnclickNode(currNode.getAttributes().getNamedItem("onclick").getNodeValue()));
				}
			}
		}
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns; 
	}

}
