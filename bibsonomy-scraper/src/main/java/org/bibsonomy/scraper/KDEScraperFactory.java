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
package org.bibsonomy.scraper;

import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.scraper.generic.CoinsScraper;
import org.bibsonomy.scraper.generic.DublinCoreScraper;
import org.bibsonomy.scraper.generic.EprintScraper;
import org.bibsonomy.scraper.generic.HighwireScraper;
import org.bibsonomy.scraper.generic.LiteratumScraper;
import org.bibsonomy.scraper.generic.UnAPIScraper;
import org.bibsonomy.scraper.id.kde.doi.ContentNegotiationDOIScraper;
import org.bibsonomy.scraper.id.kde.doi.DOIScraper;
import org.bibsonomy.scraper.id.kde.doi.HTMLMetaDataDOIScraper;
import org.bibsonomy.scraper.id.kde.isbn.ISBNScraper;
import org.bibsonomy.scraper.snippet.SnippetScraper;

/**  
 * Configures the scrapers used by BibSonomy.
 * 
 * @author rja
 *
 */
public class KDEScraperFactory {

	/**
	 * @return The scrapers produced by this factory.
	 */
	public CompositeScraper<Scraper> getScraper () {
		final CompositeScraper<Scraper> scraper = this.getScraperWithoutIE();

		/*
		 * If nothing works: do information extraction using MALLET.
		 */
		scraper.addScraper(new IEScraper());
		return scraper;
	}

	/**
	 *  @return All scrapers produced by this factory without the {@link IEScraper}. 
	 *  
	 */
	public CompositeScraper<Scraper> getScraperWithoutIE() {
		final CompositeScraper<Scraper> scraper = new CompositeScraper<Scraper>();

		/*
		 * first scraper: the DOIScraper, because it replaces dx.doi.org URLs 
		 * by the corresponding "real" URLs (i.e., the URLs, where the dx.doi.org
		 * URL points to using HTTP redirect)
		 */
		scraper.addScraper(new DOIScraper());
		
		scraper.addScraper(new KDEUrlCompositeScraper());
		
		//this scraper searches for a doi so ContentNegotiationDOIScraper has a better chance to getting bibtex
		scraper.addScraper(new HTMLMetaDataDOIScraper());
		
		//this scraper resolves DOI pages which could not be scraped by the URLScrapers
		scraper.addScraper(new ContentNegotiationDOIScraper());
		
		scraper.addScraper(new EprintScraper());
		
		// this scraper always crawls the content and thus accepts ALL URLs!
		scraper.addScraper(new UnAPIScraper());
		
		// handles several sites
		scraper.addScraper(new LiteratumScraper());
		
		//temporary solution to avoid manifold content download 
		scraper.addScraper(new HighwireScraper());
		
		scraper.addScraper(new SnippetScraper());

		scraper.addScraper(new CoinsScraper());
		
		// TODO: ISBNScraper can be used as a snippet scraper 
		scraper.addScraper(new ISBNScraper());
		
		// TODO: Scraper for searching BibTeX in HTML-Sourcecode 
		scraper.addScraper(new BibtexScraper());
		
		//scraper for Dublin Core metadata
		scraper.addScraper(new DublinCoreScraper());
		
		return scraper;
	}

}
