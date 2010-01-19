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

package org.bibsonomy.scraper.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.UnitTestRunner;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;

/**
 * Replace bibtex file with a scraped bibtex entry.
 * Simply add the test id to TEST_ID and run main.
 * 
 * @author tst
 * @version $Id$
 */
public class ReplaceBibtexFile {
	
	private static final String PATH_TO_BIBS = "src/test/resources/org/bibsonomy/scraper/data/";
	
	private static Log log = LogFactory.getLog(ReplaceBibtexFile.class);

	/**
	 * enter test ids here
	 */
	private static final String[] TEST_ID = {"url_20", "url_115"};
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// update bibtex file for every given test
		for(String testID: TEST_ID)
			replaceBibtex(testID);

	}
	
	/**
	 * Update the bibtex file which is associated with the given test. 
	 * @param id
	 * @throws IOException
	 */
	public static void replaceBibtex(String id) throws IOException{
		System.out.println("Test: " + id);
		System.out.println("running test");
		final UnitTestRunner runner = new UnitTestRunner();
		final URLScraperUnitTest test = runner.getUrlUnitTest(id);
		if(test != null){
			System.out.println("test finished");
			String bibFile = test.getBibFile();
			String scrapedBibtex = test.getScrapedReference();
			System.out.println("scraped bibtex:");
			System.out.println(scrapedBibtex);
			 
			if(bibFile != null && scrapedBibtex != null){
				// override bibtex file
				OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(PATH_TO_BIBS + bibFile), "UTF-8");
				StringReader reader = new StringReader(scrapedBibtex);
					
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
			}else
				log.error("bibfile(" + bibFile + ") and scraped bibtex(" + scrapedBibtex + ") is not available");
		}else
			log.error("Scraping failed", test.getException());
	}

}
