package org.bibsonomy.scraper.url.kde.arxiv;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.OAIConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


public class ArxivScraper implements Scraper {
	
	private static final String info = "arXiv Scraper: This scraper parses a publication page from <a href=\"http://arxiv.org/\">arXiv</a> and " +
	   								   "extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String ARXIV_HOST = "arxiv.org";
	
	private static final String PATTERN_ID = "abs/([^?]*)";
	
	private static final Logger log = Logger.getLogger(ArxivScraper.class);
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(ARXIV_HOST)) {
			try {
				sc.setScraper(this);
				
				//get id
				String id = null;
				
				Pattern patternID = Pattern.compile(PATTERN_ID);
				Matcher matcherID = patternID.matcher(sc.getUrl().toString());
				if(matcherID.find())
					id = matcherID.group(1);
				
				if(id != null){
					// build url for oai_dc export
					String exportURL = "http://export.arxiv.org/oai2?verb=GetRecord&identifier=oai:arXiv.org:" + id + "&metadataPrefix=oai_dc";
					
					// download oai_dc reference
					String reference = sc.getContentAsString(new URL(exportURL));
					
					String bibtex = OAIConverter.convert(reference);
					
					// add arxiv citation to note
					bibtex = bibtex.replace("note = {", "note = {cite arxiv:" + id + "\n");
					// if note not exist
					bibtex = bibtex.replaceFirst("},", "}\nnote = {cite arxiv:" + id + "}");
					
					// set result
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("no arxiv id found in URL");
			} catch (MalformedURLException me) {
				throw new InternalFailureException(me);
			}
		}		
		return false;
	}

	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
