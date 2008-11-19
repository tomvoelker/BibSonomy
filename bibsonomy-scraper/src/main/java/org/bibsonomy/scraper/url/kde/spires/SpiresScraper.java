package org.bibsonomy.scraper.url.kde.spires;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/** Scraper for the SLAC National Accelerator Laboratory
 * @author rja
 *
 */
public class SpiresScraper extends UrlScraper{
	private static final String info = "Spires Scraper: Gets publications from " + href("slac.stanford.edu", "SLAC National Accelerator Laboratory");

	private static final String SPIRES_HOST = "slac.stanford.edu";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + SPIRES_HOST), UrlScraper.EMPTY_PATTERN));
	
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			try {
				String bibtexresult = null;
				
				//form the dom to extract the bibtex-url
				Document doc = getDOM(sc.getPageContent());
				
				//create the URL to the bibtex snippet
				URL bibtex = new URL("http://" + SPIRES_HOST + extractUrlFromElementByTagNameAndValue(doc, "a","BibTeX", "href"));
				
				//create the dom of the bibtex page
				Document temp = getDOM(sc.getContentAsString(bibtex));
				
				//extract the bibtex snippet which is embedded in pre tags
				NodeList nl = temp.getElementsByTagName("pre"); //get the pre tags (normally one)
				for (int i = 0; i < nl.getLength(); i++) {
					Node currNode = nl.item(i);
					if (currNode.hasChildNodes()){
						bibtexresult = currNode.getChildNodes().item(0).getNodeValue();	
					}
				}	
				
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

				
			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}
	}

	public String getInfo() {
		return info;
	}
	/** Parses a page and returns the DOM
	 * @param content
	 * @return
	 */
	private Document getDOM(String content) {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);// turns off warning lines
		Document doc = tidy.parseDOM(new ByteArrayInputStream(content.getBytes()), null);
		return doc;
	}
	
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}