package org.bibsonomy.scraper.generic;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/** Scrapes pages providing BibTeX via the <a href="http://unapi.info/">UN-API</a>.
 * 
 * @author rja
 * @version $Id$
 */
public class UnAPIScraper implements Scraper {


	public String getInfo() {
		return "Scrapes pages providing BibTeX (format=bibtex) via the the <a href=\"http://unapi.info/\">UN-API</a>.";
	}

	public Collection<Scraper> getScraper() {
		return Collections.singleton((Scraper) this);
	}

	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		final String pageContents = scrapingContext.getPageContent();
		/*
		 * search for 
		 * 
		 * <link rel="unapi-server" type="application/xml" title="unAPI" href="http://canarydatabase.org/unapi" /> 
		 * 
		 * and
		 * 
		 * <abbr class='unapi-id' title='http://canarydatabase.org/record/488'> </abbr> 
		 */
		if (pageContents != null && pageContents.contains("unapi-server") && pageContents.contains("unapi-id")) {
			/*
			 * do the expensive JTidy stuff to extract the server and id
			 */
			final Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false); // turn off warning lines
			final Document document = tidy.parseDOM(new ByteArrayInputStream(pageContents.getBytes()), null);
			/*
			 * get the server id
			 */
			final String href = getApiHref(document);
			if (href != null) {
				/*
				 * get record identifier
				 */
				final String id = getRecordIdentifier(document);
				if (id != null) {
					/*
					 * query for bibtex
					 */
					try {
						/*
						 * build URL to get record in bibtex format
						 */
						final URL url = new URL(href + "?format=bibtex&id=" + URLEncoder.encode(id, "UTF-8"));
						/*
						 * get the data
						 */
						final String bibtex = scrapingContext.getContentAsString(url);
						if (bibtex != null) {
							/*
							 * success! 
							 */
							scrapingContext.setScraper(this);
							scrapingContext.setBibtexResult(bibtex);
							return true;
						}
					} catch (MalformedURLException ex) {
						// ignore, maybe bibtex just isn't supported
					} catch (UnsupportedEncodingException ex) {
						// ignore, maybe bibtex just isn't supported
					}
				}
			}
		}
		return false;
	}

	/** Extracts the "href" attribute from "link" tags whose "rel" attribute equals "unapi-server".
	 * 
	 * @param document
	 * @return The href attribute of the proper link-tag or <code>null</code> if it could not be found.
	 */
	private String getApiHref(final Document document) {
		final NodeList elementsByTagName = document.getElementsByTagName("link");
		for (int i = 0; i < elementsByTagName.getLength(); i++) {
			final Node node = elementsByTagName.item(i);
			final NamedNodeMap attributes = node.getAttributes();
			final Node relAttribute = attributes.getNamedItem("rel");
			if (relAttribute != null && "unapi-server".equals(relAttribute.getNodeValue())) {
				/*
				 * link to server found -> extract href
				 */
				final Node href = attributes.getNamedItem("href");
				if (href != null) {
					return href.getNodeValue();
				}
			}
		}
		return null;
	}

	
	/** Extracts the "title" attribute from the first (!) "abbr" tag whose "class" attribute equals "unapi-id".
	 * 
	 * @param document
	 * @return The "title" attribute of the proper abbr-tag or <code>null</code> if it could not be found.
	 * 
	 */
	private String getRecordIdentifier(final Document document) {
		final NodeList abbrTags = document.getElementsByTagName("abbr");
		for (int i = 0; i < abbrTags.getLength(); i++) {
			final Node node = abbrTags.item(i);
			final NamedNodeMap attributes = node.getAttributes();
			final Node classAttribute = attributes.getNamedItem("class");
			if (classAttribute != null && "unapi-id".equals(classAttribute.getNodeValue())) {
				/*
				 * record found -> extract id
				 */
				final Node title = attributes.getNamedItem("title");
				if (title != null) {
					return title.getNodeValue();
				}
			}
		}
		return null;

	}

}