package org.bibsonomy.scraper.url.kde.spires;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.util.BibTeXUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Scraper for the SLAC National Accelerator Laboratory
 * @author rja
 *
 */
public class SpiresScraper extends AbstractUrlScraper{
	private static final String FORMAT_WWWBRIEFBIBTEX = "FORMAT=WWWBRIEFBIBTEX";

	private static final String info = "Spires Scraper: Gets publications from " + href("slac.stanford.edu", "SLAC National Accelerator Laboratory");

	private static final List<Tuple<Pattern, Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();
	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "slac.stanford.edu"), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + "www-library.desy.de"), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			try {
				final URL url = sc.getUrl();
				
				URL bibtexUrl = url;
				if (!url.getQuery().contains(FORMAT_WWWBRIEFBIBTEX)) { 
					bibtexUrl = new URL(url.toString() + "&" + FORMAT_WWWBRIEFBIBTEX);
				}
				
				final Document temp = XmlUtils.getDOM(sc.getContentAsString(bibtexUrl));
				
				//extract the bibtex snippet which is embedded in pre tags
				String bibtex = null;
				final NodeList nl = temp.getElementsByTagName("pre"); //get the pre tags (normally one)
				for (int i = 0; i < nl.getLength(); i++) {
					Node currNode = nl.item(i);
					if (currNode.hasChildNodes()){
						bibtex = currNode.getChildNodes().item(0).getNodeValue();	
					}
				}	
				
				/*
				 * add URL
				 */
				bibtex = BibTeXUtils.addFieldIfNotContained(bibtex, "url", url.toString());
				
				//-- bibtex string may not be empty
				if (bibtex != null && ! "".equals(bibtex)) {
					sc.setBibtexResult(bibtex);
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}