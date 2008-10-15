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
	
	private static final String PATTERN_TITLE = "<dc:title>([^<]*)<";
	private static final String PATTERN_CREATOR = "<dc:creator>([^<]*)<";
	private static final String PATTERN_DESCRIPTION = "<dc:description>([^<]*)<";
	private static final String PATTERN_DATE = "<dc:date>([^<]*)<";
	private static final String PATTERN_IDENTIFIER = "<dc:identifier>([^<]*)<";
	
	private static final String PATTERN_YEAR = ".*(\\d{4}).*";
	private static final String PATTERN_ID = "abs/([^?]*)";
	
	private static final Logger log = Logger.getLogger(ArxivScraper.class);
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(ARXIV_HOST)) {
			try {
				sc.setScraper(this);
				
				StringBuffer bibtexResult = new StringBuffer(); 
				
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
					
					String key = "";
					//parse reference
					
					// get title
					String title = null;
					Pattern patternTitle = Pattern.compile(PATTERN_TITLE);
					Matcher matcherTitle = patternTitle.matcher(reference);
					if(matcherTitle.find())
						title = matcherTitle.group(1);
					
					//get authors
					String creator = "";
					Pattern patternCreator = Pattern.compile(PATTERN_CREATOR);
					Matcher matcherCreator = patternCreator.matcher(reference);
					while(matcherCreator.find()){
						if(creator.equals("")){
							creator = matcherCreator.group(1);
							// add lastname from the first author to bibtex key
							key = creator.substring(0, creator.indexOf(","));
						}else
							creator = creator + " and " + matcherCreator.group(1);
					}
					
					String description = "";
					Pattern patternDescription = Pattern.compile(PATTERN_DESCRIPTION, Pattern.MULTILINE);
					Matcher matcherDescription = patternDescription.matcher(reference);
					while(matcherDescription.find())
						if(description.equals(""))
							description = matcherDescription.group(1);
						else
							description = description + " " + matcherDescription.group(1);
					
					String year = null;
					Pattern patternDate = Pattern.compile(PATTERN_DATE);
					Matcher matcherDate = patternDate.matcher(reference);
					if(matcherDate.find()){
						String date = matcherDate.group(1);
						Pattern patternYear = Pattern.compile(PATTERN_YEAR);
						Matcher matcherYear = patternYear.matcher(date);
						if(matcherYear.find()){
							year = matcherYear.group(1);
							key = key + year;
						}
					}
					
					String identifier = null;
					Pattern patternIdentifier = Pattern.compile(PATTERN_IDENTIFIER);
					Matcher matcherIdentifier = patternIdentifier.matcher(reference);
					if(matcherIdentifier.find())
						identifier = matcherIdentifier.group(1);
					
					// build bibtex
					
					// start and bibtex key
					bibtexResult.append("@MISC{");
					bibtexResult.append(key);
					bibtexResult.append(",\n");
					
					// title
					if(title != null){
						bibtexResult.append("title = {");
						bibtexResult.append(title);
						bibtexResult.append("}");
						bibtexResult.append(",\n");
					}else
						throw new ScrapingFailureException("no title found");
					
					// author
					if(!creator.equals("")){
						bibtexResult.append("author = {");
						bibtexResult.append(creator);
						bibtexResult.append("}");
						bibtexResult.append(",\n");
					}else
						throw new ScrapingFailureException("no authors found");

					// year
					if(year != null){
						bibtexResult.append("year = {");
						bibtexResult.append(year);
						bibtexResult.append("}");
						bibtexResult.append(",\n");
					}else
						throw new ScrapingFailureException("no year found");

					// abstract
					if(!description.equals("")){
						bibtexResult.append("abstract = {");
						bibtexResult.append(description);
						bibtexResult.append("}");
						bibtexResult.append(",\n");
					}
					
					// url
					if(identifier != null){
						bibtexResult.append("url = {\\url{");
						bibtexResult.append(identifier);
						bibtexResult.append("}}");
						bibtexResult.append(",\n");
					}

					// remove last ","
					bibtexResult = new StringBuffer(bibtexResult.subSequence(0, bibtexResult.lastIndexOf(",")-1));
					
					// finisch bibtex
					bibtexResult.append("\n}\n");
					
					// set result
					sc.setBibtexResult(bibtexResult.toString());
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
