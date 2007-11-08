package org.bibsonomy.importer.event.iswc;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.bibsonomy.importer.event.iswc.exceptions.RepositoryException;
import org.bibsonomy.importer.event.iswc.rdf.RDFRepositoryTest;

/**
 * test of the methods from {@link SWRCtoBibtexConverter}.
 * @author tst
 *
 */
public class SWRCtoBibtexConverterTest {

	/**
	 * checks if the convertion works through completely. 
	 */
	@Ignore
	public void convertToBibtexTest(){
		
		// init converter
		SWRCtoBibtexConverter converter = new SWRCtoBibtexConverter();
		
		try {
			// run convertion with test files
			converter.convertToBibtex(RDFRepositoryTest.TEST_RDF_FILE, "", "" );
			// it runs through all, assert as true
			assertTrue(true);
		} catch (RepositoryException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void dummy() {
		
	}
	
}
