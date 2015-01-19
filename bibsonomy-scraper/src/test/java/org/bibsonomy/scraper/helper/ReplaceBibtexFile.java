/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.bibsonomy.scraper.ScraperTestData;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;
import org.bibsonomy.util.StringUtils;

/**
 * Replace bibtex file with a scraped bibtex entry.
 * Simply add the test id to TEST_ID and run main.
 * 
 * @author tst
 */
public class ReplaceBibtexFile {
	private static IUnitTestImporter IMPORTER = new XMLUnitTestImporter();
	private static final String PATH_TO_BIBS = "src/test/resources/org/bibsonomy/scraper/data/";
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(final String[] args) throws Exception {
		// update bibtex file for every given test
		for (final String testID: args) {
			replaceBibtex(testID);
		}
	}
	
	/**
	 * Update the bibtex file which is associated with the given test. 
	 * @param id
	 * @throws IOException
	 */
	private static void replaceBibtex(final String id) throws Exception {
		System.out.println("Test: " + id);
		System.out.println("running test");
		ScraperTestData test = IMPORTER.getUnitTests().get(id);
		if (test != null){
			System.out.println("test finished");
			final String bibFile = test.getBibTeXFileName();
			final String scrapedBibtex = UnitTestRunner.callScraper(test);
			System.out.println("scraped bibtex:");
			System.out.println(scrapedBibtex);
			if ((bibFile != null) && (scrapedBibtex != null)) {
				// override bibtex file
				final OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(PATH_TO_BIBS + bibFile), StringUtils.CHARSET_UTF_8);
				final StringReader reader = new StringReader(scrapedBibtex);
					
				int read = reader.read();
				while(read != -1){
					os.write(read);
					read = reader.read();
				}
					
				// clean up
				os.flush();
				os.close();
				reader.close();
				
				System.out.println("old bibtex replaced");
				System.out.println("**********************************************");
			} else {
				System.err.println("bibfile(" + bibFile + ") and scraped bibtex(" + scrapedBibtex + ") is not available");
			}
		} else {
			System.err.println("Scraping failed");
		}
	}

}
