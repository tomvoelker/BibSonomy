package org.bibsonomy.scraper.url.kde.aanda;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class AandAScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "Astronomy and Astrophysics";

	private static final String SITE_URL = "http://www.aanda.org/";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final Pattern hostPattern = Pattern.compile(".*" + "aanda.org");
	
	private static final String downloadUrl = "http://www.aanda.org/index.php?option=com_makeref&task=output&type=bibtex&doi=";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(hostPattern, AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		
		try {
			String doi = null;
			
			// need to filter the DOI out of the context, because the DOI is a common but not constant finding in the URL
			doi = this.extractDOI(XmlUtils.getDOM(sc.getPageContent()));

			// if the doi is present
			if (doi != null){
	
				// get the bibtexcontent
				String bibtexContent = WebUtils.getContentAsString(downloadUrl + doi);
			
				// and return it
				if(bibtexContent != null){
					sc.setBibtexResult(bibtexContent);
					return true;
				}else {
					throw new ScrapingFailureException("failure during download");
				}
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
		
		return false;
	}
	
	/**
	 * Extracts the DOI out of the page source.
	 * Structure is as follows:
	 * 
	 *  <tr>
	 *	   <td class="gen">DOI</td>
	 *	   <td></td>
	 *	   <td>10.1051/0004-6361:20053694</td>
	 *	</tr>
	 * 
	 * @param document
	 * @return
	 */
	private String extractDOI(final Document document){
		String doi = null;
		
		NodeList tdS = document.getElementsByTagName("td");
		for(int i = 0; i < tdS.getLength(); i++){
			Node node = tdS.item(i);
			if(node.hasChildNodes()){
				if("DOI".equals(node.getFirstChild().getNodeValue())){
					Node parent = node.getParentNode();
					Node doiNode = parent.getLastChild();
					doi = doiNode.getFirstChild().getNodeValue();
				}
			}
		}
		
		return doi;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	public String getInfo() {
		return INFO;
	}
	
	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
