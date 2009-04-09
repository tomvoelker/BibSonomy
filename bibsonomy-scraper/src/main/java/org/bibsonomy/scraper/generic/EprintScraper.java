package org.bibsonomy.scraper.generic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.id.DOIUtils;

/**
 * Scraper for repositories which use eprint
 * 
 * @author tst
 * @version $Id$
 */
public class EprintScraper implements Scraper {
	
	private static final String INFO = "Scraper for repositories which use " + AbstractUrlScraper.href("http://www.eprints.org/", "eprints");

	private Pattern patternMeta = Pattern.compile("<meta content=\"([^\\\"]*)\" name=\"([^\\\"]*)\" />");
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singleton((Scraper) this);
	}

	public boolean scrape(ScrapingContext scrapingContext)throws ScrapingException{
		scrapingContext.setScraper(this);
		
		HashMap<String, String> eprintToBibtexFields = new HashMap<String, String>();
		eprintToBibtexFields.put("eprints.creators_name", "author");
		eprintToBibtexFields.put("eprints.editors_name", "editor");
		eprintToBibtexFields.put("eprints.type", "bibtextype");
		eprintToBibtexFields.put("eprints.title", "title");
		eprintToBibtexFields.put("eprints.note", "note");
		eprintToBibtexFields.put("eprints.abstract", "abstract");
		eprintToBibtexFields.put("eprints.date", "year");
		eprintToBibtexFields.put("eprints.volume", "volume");
		eprintToBibtexFields.put("eprints.publisher", "publisher");
		eprintToBibtexFields.put("eprints.place_of_pub", "adddress");
		eprintToBibtexFields.put("eprints.pagerange", "pages");
		eprintToBibtexFields.put("eprints.pages", "pages");
		eprintToBibtexFields.put("eprints.isbn", "isbn");
		eprintToBibtexFields.put("eprints.book_title", "booktitle");
		eprintToBibtexFields.put("eprints.official_url", "url");
		eprintToBibtexFields.put("eprints.publication", "journal");
		eprintToBibtexFields.put("eprints.number", "number");
		eprintToBibtexFields.put("eprints.issn", "issn");
		eprintToBibtexFields.put("eprints.id_number", "doi");
		
		HashMap<String, String> eprintTypesToBibtexTypes = new HashMap<String, String>();
		eprintTypesToBibtexTypes.put("book", "book");
		eprintTypesToBibtexTypes.put("book_section", "inbook");
		eprintTypesToBibtexTypes.put("article", "article");
		eprintTypesToBibtexTypes.put("conference", "inproceeding");
		eprintTypesToBibtexTypes.put("monograph", "techreport");
		eprintTypesToBibtexTypes.put("thesis", "phdthesis");

		/*
		 * read eprint meta fields and resolve bibtex fields 
		 */
		
		HashMap<String, LinkedList<String>> bibtexFields = new HashMap<String, LinkedList<String>>();
		Matcher metaMatcher = patternMeta.matcher(scrapingContext.getPageContent());
		while(metaMatcher.find()){
			String content = metaMatcher.group(1);
			String name = metaMatcher.group(2);
			
			// get bib field
			String bibtexField = eprintToBibtexFields.get(name);
			
			// store in map
			if(bibtexFields.containsKey(bibtexField))
				bibtexFields.get(bibtexField).add(content);
			else{
				bibtexFields.put(bibtexField, new LinkedList<String>());
				bibtexFields.get(bibtexField).add(content);
			}
		}
		
		/*
		 * build bibtex
		 */
		
		// bibtex type
		String bibtextype = null;
		if(bibtexFields.containsKey("bibtextype")){
			String type = bibtexFields.get("bibtextype").getFirst();
			if(eprintTypesToBibtexTypes.containsKey(type))
				bibtextype = eprintTypesToBibtexTypes.get(type);
			else
				bibtextype = "misc";
		}else
			bibtextype = "misc";
		bibtexFields.remove("bibtextype"); //not needed anymore

		
		// bibtex key (author/editor lastname + year)
		String bibtexkey = "";
		String year = null;
		String firstLastname = null;
		// get components lastname and year
		if(bibtexFields.containsKey("author")){
			String author = bibtexFields.get("author").getFirst();
			Matcher matcherLastname = Pattern.compile("([^,]*)").matcher(author);
			if(matcherLastname.find())
				firstLastname = matcherLastname.group(1);
		}else if(bibtexFields.containsKey("editor")){
			String editor = bibtexFields.get("editor").getFirst();
			Matcher matcherLastname = Pattern.compile("([^,]*)").matcher(editor);
			if(matcherLastname.find())
				firstLastname = matcherLastname.group(1);
		}
		
		if(bibtexFields.containsKey("year")){
			String yearField = bibtexFields.get("year").getFirst();
			Matcher matcherYear = Pattern.compile("(\\d{4})").matcher(yearField);
			if(matcherYear.find())
				year = matcherYear.group(1);
		}
		// build bibtex key
		if(firstLastname != null && year != null){
			bibtexkey = firstLastname + year;
		}else
			bibtexkey = "defaultKey";
		
		// build bibtex
		StringBuffer bibtexBuffer = new StringBuffer();
		// intro elements
		bibtexBuffer.append("@");
		bibtexBuffer.append(bibtextype);
		bibtexBuffer.append("{");
		bibtexBuffer.append(bibtexkey);
		bibtexBuffer.append(",\n");

		// iterate over every bibtex field and append it to buffer
		for(String bibtexField: bibtexFields.keySet()){
			if(bibtexField != null){
				String value = null;
				// special handling for author
				if(bibtexField.equals("author")){
					String authorString = "";
					for(String author: bibtexFields.get("author"))
						authorString = authorString + author + " and ";
					// remove last " and "
					authorString = authorString.substring(0, authorString.length()-5);
					
					value = authorString;
				}
				// special handling for editor
				else if(bibtexField.equals("editor")){
					String editorString = "";
					for(String editor: bibtexFields.get("editor"))
						editorString = editorString + editor + " and ";
					// remove last " and "
					editorString = editorString.substring(0, editorString.length()-5);
				}
				// special handling for doi
				else if(bibtexField.equals("doi")){
					String doi = bibtexFields.get(bibtexField).getFirst();
					if(DOIUtils.isDOI(doi))
						value = doi;
				}
				// special handling for year
				else if(bibtexField.equals("year")){
					if(year != null)
						value = year;
					else
						value = bibtexFields.get(bibtexField).getFirst();
				}
				// rest, simply add 
				else{
					String bibtexFieldValue = "";
					for(String singelValue: bibtexFields.get(bibtexField))
						bibtexFieldValue = bibtexFieldValue + " " + singelValue;
					bibtexFieldValue = bibtexFieldValue.trim();
					
					value = bibtexFieldValue;
				}
				
				// append
				if(value != null){
					bibtexBuffer.append(bibtexField);
					bibtexBuffer.append(" = {");
					bibtexBuffer.append(value);
					bibtexBuffer.append("},\n");
				}
			}
		}

		bibtexBuffer.replace(bibtexBuffer.length()-2, bibtexBuffer.length(), "\n");
		// finish
		bibtexBuffer.append("}");
		
		String bibtex = null;
		bibtex = bibtexBuffer.toString();
		if(bibtex != null){
			scrapingContext.setBibtexResult(bibtex);
			return true;
		}
		
		return false;
	}

	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		try {
			String page = scrapingContext.getPageContent();
			if(page.contains("name=\"eprints.date\"") && page.contains("name=\"eprints.title\""))
				return true;
		} catch (ScrapingException ex) {
			return false;
		}
		return false;
	}

}
