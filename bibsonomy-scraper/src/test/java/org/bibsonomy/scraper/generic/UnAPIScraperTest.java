/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.generic;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class UnAPIScraperTest {

	/* removed URLs:
	 * 
	 * "http://ebsees.staatsbibliothek-berlin.de/simple_search.php?title=%27Aleksej%20Tolstojs%20Erz%C3%A4hlung%20%C2%ABBrot%C2%BB.%20Der%20literarische%20Text%20als%20fiktive%20Dokumentation%27,%20in:%20Schriften%20-%20Dinge%20-%20Phantasmen:%20Literatur%20und%20Kultur%20der%20russischen%20Moderne%20I,%20Mirjam%20Goller,%20Susanne%20Str%C3%A4tling,%20Hrsg.&data=96527&hits=364&ds=1",
	 * --> bieten kein BibTeX an (nur "mods"), siehe http://ebsees.staatsbibliothek-berlin.de/unapi.php
	 *
	 * 	"http://iwblog.vili.de/2008/06/05/vibi-mit-unapi-unterstutzung/"
	 * --> bieten kein BibTeX an, siehe http://iwblog.vili.de/wp-content/plugins/unapi/server.php
	 */

	final String[] urls = new String[] {
//			"http://canarydatabase.org/record/488",
			"http://www.bibsonomy.org/"
	};

	@Test
	public void testScrape() {
		final UnAPIScraper scraper = new UnAPIScraper();
		for (final String urlString: urls) {
			try {
				final URL url = new URL(urlString);
				final ScrapingContext scrapingContext = new ScrapingContext(url);

				scraper.scrape(scrapingContext);

				final String bibtexResult = scrapingContext.getBibtexResult();

				assertNotNull(bibtexResult);

			} catch (ScrapingException ex) {
				fail(ex.getMessage());
			} catch (MalformedURLException ex) {
				fail(ex.getMessage());
			} catch (IOException ex) {
				fail(ex.getMessage());
			}
		}
	}

}
