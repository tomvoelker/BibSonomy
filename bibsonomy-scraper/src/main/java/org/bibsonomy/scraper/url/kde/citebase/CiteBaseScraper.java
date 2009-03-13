package org.bibsonomy.scraper.url.kde.citebase;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Scraper for CiteBase.
 * 
 * @author rja
 *
 */
public class CiteBaseScraper extends AbstractUrlScraper {

//	http://www.citebase.org/abstract?id=oai:arXiv.org:cs/0408047

	private static final String info = "citebase Scraper: This scraper parses a publication page from " + href("http://www.citebase.org/", "Citebase");

	private static final String CITEBASE_HOST = "citebase.org";
	private static final String CITEBASE_HOST_NAME = "http://www.citebase.org";
	//private static final String CITEBASE_STRING_ON_ARXIV = "CiteBase"; //TODO: never used locally

	private static final String BIBTEX_STRING_ON_ARXIV = "BibTeX";
	private static final String BIBTEX_ABSTRACT_TAG = "div";

	private static final Logger log = Logger.getLogger(CiteBaseScraper.class);

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + CITEBASE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		try {

			final Document document = XmlUtils.getDOM(sc.getPageContent());
			String bibAbstract = extractAbstract(document, BIBTEX_ABSTRACT_TAG); 

			// get bibtex url on citebase publication page
			URL bibtexUrl = new URL(CITEBASE_HOST_NAME	+ extractUrlFromElementByTagNameAndValue(document, "a",	BIBTEX_STRING_ON_ARXIV, "href"));

			log.debug("bibtex url = " + bibtexUrl);

			// get bibtex page and add abstract
			String bibtexEntry = WebUtils.getContentAsString(bibtexUrl);
			if (bibAbstract != null) {
				bibtexEntry = addAbstractToBibtexEntry(bibtexEntry,
						bibAbstract);
			}
			// set result
			sc.setBibtexResult(bibtexEntry);
			return true;

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	public String getInfo() {
		return info;
	}

	/**
	 * Extracts URLs from specific and page-unique elements. Unique means, that the node value (here: CiteBase)
	 * of the requested element "a" appears only once as node value.
	 * We handle some like this: <a href="http://blabla.com">CiteBase</a>
	 * @param pageContent Page content as InputStream
	 * @param tagName E.g. a 
	 * @param tagValue  E.g. CiteBase 
	 * @param attribute E.g. href
	 * @return The extracted URL as a String - e.g. http://blabla.com or null
	 * @throws DOMException 
	 * @throws MalformedURLException 
	 */
	private String extractUrlFromElementByTagNameAndValue(Document doc, String tagName, String tagValue, String attribute) throws MalformedURLException, DOMException{
		NodeList as = doc.getElementsByTagName(tagName);
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);

			if (currNode.getChildNodes().getLength() > 0) {
				if (tagValue.equals(currNode.getChildNodes().item(0).getNodeValue())){
					return currNode.getAttributes().getNamedItem(attribute).getNodeValue();						
				}
			}

		}		
		return null;
	}

	private String extractAbstract(Document doc, String tagName){		
		NodeList as = doc.getElementsByTagName(tagName); 
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);
			if (currNode.getAttributes().getNamedItem("class")!=null && currNode.getAttributes().getNamedItem("class").getNodeValue().equals("abstract")) {	
				log.debug("abstract = " + currNode.getChildNodes().item(0).getNodeValue());
				return currNode.getChildNodes().item(0).getNodeValue();	
			}
		}		
		return null;
	}

	/**
	 *  Add abstract to bibtex entry by replacing the last occurrence of "}"
	 *  with ",abstract = {...}}"
	 */
	private String addAbstractToBibtexEntry(String bibtexEntry, String bibAbstract){
		StringBuffer buf = new StringBuffer (bibtexEntry);
		buf.replace(buf.lastIndexOf("}"), buf.length(), ", abstract={" + bibAbstract + "}}");
		return  buf.toString();		
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
