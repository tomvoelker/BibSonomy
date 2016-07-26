/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.snippet;

import static org.bibsonomy.util.ValidationUtils.present;

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

/**
 * scrapes BibTex from the selected text
 */
public class SnippetScraper implements Scraper {
    private static final String info = "SnippetScraper: This scraper checks passed snippets for " +
    								   "valid BibTeX entries. Author: KDE";
    
	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException{
		if (this.supportsScrapingContext(sc)) {
			sc.setBibtexResult(sc.getSelectedText());
			sc.setScraper(this);
			return true;
		}
		return false;
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		final String selectedText = sc.getSelectedText();
		/*
		 * don't scrape, if there is nothing selected
		 */
		if (!present(selectedText)) {
			return false;
		}
		
		/*
		 * parse the selected text with our bibtex parser
		 * to check if the selected test is valid BibTeX
		 */
		return this.isValidBibTeX(selectedText);
	}

	private boolean isValidBibTeX(final String selectedText) {
		try {
			final BibtexParser parser = new BibtexParser(true);
			final BibtexFile bibtexFile = new BibtexFile();
			final BufferedReader sr = new BufferedReader(new StringReader(selectedText));
			// parse selected text
			parser.parse(bibtexFile, sr);
	
			for (final Object potentialEntry:bibtexFile.getEntries()) {
				if ((potentialEntry instanceof BibtexEntry)) {
					return true; 
				}
			}
		} catch (final ParseException pe) {
			// no valid BibTeX
		} catch (final IOException ioe) {
			// ignore
		}
		return false;
	}
	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return null;
	}
	
	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return null;
	}

}
