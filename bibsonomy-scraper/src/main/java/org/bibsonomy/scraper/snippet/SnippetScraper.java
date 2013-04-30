/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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

/**
 * scrapes BibTex from the selected text
 * @version $Id$
 */
public class SnippetScraper implements Scraper {
    private static final String info = "SnippetScraper: This scraper checks passed snippets for " +
    								   "valid BibTeX entries. Author: KDE";
    
	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException{
		final String selectedText = sc.getSelectedText();
		/*
		 * don't scrape, if there is nothing selected
		 */
		if ((selectedText == null) || selectedText.trim().equals("")) {
			return false;
		}

		try{
			/* **************************************************
			 * snippet parsing starts here
			 * **************************************************/

			final BibtexParser parser = new BibtexParser(true);
			final BibtexFile bibtexFile = new BibtexFile();
			final BufferedReader sr = new BufferedReader(new StringReader(selectedText));
			// parse file, exceptions are catched below
			parser.parse(bibtexFile, sr);

			for (final Object potentialEntry:bibtexFile.getEntries()) {
				if ((potentialEntry instanceof BibtexEntry)) {
					sc.setBibtexResult(selectedText);
					sc.setScraper(this);
					return true; 
				}
			}

		} catch (final ParseException pe) {
			throw new ScrapingException(pe);
		} catch (final IOException ioe) {
			throw new ScrapingException(ioe);			
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
		if ((selectedText == null) || selectedText.trim().equals("")) {
			return false;
		}

		try{
			/* **************************************************
			 * snippet parsing starts here
			 * **************************************************/

			final BibtexParser parser = new BibtexParser(true);
			final BibtexFile bibtexFile = new BibtexFile();
			final BufferedReader sr = new BufferedReader(new StringReader(selectedText));
			// parse file, exceptions are catched below
			parser.parse(bibtexFile, sr);

			for (final Object potentialEntry:bibtexFile.getEntries()) {
				if ((potentialEntry instanceof BibtexEntry)) {
					return true; 
				}
			}

		} catch(final ParseException pe) {
			return false;
		} catch (final IOException ioe) {
			return false;
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
