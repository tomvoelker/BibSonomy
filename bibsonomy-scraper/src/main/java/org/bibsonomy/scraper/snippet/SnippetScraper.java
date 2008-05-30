package org.bibsonomy.scraper.snippet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;

public class SnippetScraper implements Scraper {
    private static final String info = "SnippetScraper: This scraper checks passed snippets for " +
    								   "valid BibTeX entries. Author: KDE";
    
	public boolean scrape(ScrapingContext sc) throws ScrapingException{
		String selectedText = sc.getSelectedText();
		/*
		 * don't scrape, if there is nothing selected
		 */
		if (selectedText == null || selectedText.trim().equals("")) return false;

		try{
			/* **************************************************
			 * snippet parsing starts here
			 * **************************************************/

			BibtexParser parser = new BibtexParser(true);
			BibtexFile bibtexFile = new BibtexFile();
			BufferedReader sr = new BufferedReader(new StringReader(selectedText));
			// parse file, exceptions are catched below
			parser.parse(bibtexFile, sr);

			for (Object potentialEntry:bibtexFile.getEntries()) {
				if ((potentialEntry instanceof BibtexEntry)) {
					sc.setBibtexResult(selectedText);
					return true; 
				}
			}

		} catch(ParseException pe) {
			throw new ScrapingException(pe);
		} catch (IOException ioe) {
			throw new ScrapingException(ioe);			
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
