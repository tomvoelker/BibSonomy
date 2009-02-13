package org.bibsonomy.scraper.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
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
	
	private static Logger log = Logger.getLogger(ReplaceBibtexFile.class);

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
		UnitTestRunner runner = new UnitTestRunner();
		URLScraperUnitTest test = runner.getUrlUnitTest(id);
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
